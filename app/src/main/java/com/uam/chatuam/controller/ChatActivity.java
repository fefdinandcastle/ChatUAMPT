package com.uam.chatuam.controller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.database.DatabaseErrorHandler;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Debug;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.uam.chatuam.R;
import com.uam.chatuam.Utils;
import com.uam.chatuam.adapters.AdapterMessages;
import com.uam.chatuam.model.ChatObject;
import com.uam.chatuam.model.Mensaje;

import org.jivesoftware.smack.AbstractXMPPConnection;

import java.util.ArrayList;
import java.util.Date;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Observable;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.forward.packet.Forwarded;
import org.jivesoftware.smackx.mam.MamManager;
import org.jivesoftware.smackx.mam.element.MamQueryIQ;
import org.jivesoftware.smackx.muc.MucEnterConfiguration;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.offline.OfflineMessageManager;
import org.jivesoftware.smackx.push_notifications.PushNotificationsManager;
import org.jivesoftware.smackx.xdata.FormField;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Localpart;
import org.jxmpp.jid.parts.Resourcepart;
import org.jxmpp.stringprep.XmppStringprepException;

public class ChatActivity extends AppCompatActivity {

    EditText entradaMensaje;
    private RecyclerView rvMessages;
    private RecyclerView.Adapter mAdapter;
    private RelativeLayout noneView;
    private RecyclerView.LayoutManager mLmanager;
    private ArrayList<Mensaje> mMessagesData = new ArrayList<Mensaje>();
    private AbstractXMPPConnection mConnection;
    public static final String TAG = MainActivity.class.getSimpleName();
    private MultiUserChat multiUserChat=null;
    private MamManager mamManager;
    private Observable disposableMessages=null;
    Context context;
    private boolean vacio = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(!Utils.darkTheme) setTheme(R.style.CuajimalpaLightActionBar);
        else setTheme(R.style.CuajimalpaDarkActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.color_unidad_cuajimalpa)));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        Utils.ueaIndex=intent.getIntExtra("ueaIndex",0);
        Utils.chatIndex=intent.getIntExtra("chatIndex",0);
        setTitle((((Utils.usuario.getUeas()).get(Utils.ueaIndex).getChats()).get(Utils.chatIndex)).getNombreChat());
        entradaMensaje = findViewById(R.id.input_text_message);
        noneView = (RelativeLayout) findViewById(R.id.viewNone);
        context=this;
        if(Utils.chatIndex>0){
            UnicastConexion();
        }else{
            MulticastConexion();
        }

        rvMessages = findViewById(R.id.rvMessages);
        rvMessages.setHasFixedSize(true);
        mLmanager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,true);
        rvMessages.setLayoutManager(mLmanager);
        mAdapter = new AdapterMessages((((Utils.usuario.getUeas()).get(Utils.ueaIndex).getChats()).get(Utils.chatIndex)).getMensajes());
        rvMessages.setAdapter(mAdapter);

        FloatingActionButton floatingActionButton = findViewById(R.id.fab_receiveMessage);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recibirMensaje();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void enviarMensaje(View v){
        String messageSend = entradaMensaje.getText().toString();
        entradaMensaje.setText("");
        //noneView.setVisibility(View.GONE);
        //rvMessages.setVisibility(View.VISIBLE);
        if(messageSend.length()>0){
            if(Utils.chatIndex>0){
                if(Utils.usuario.getTipo().equals("AL")){
                    sendMessageUnicast(messageSend,Utils.usuario.getUeas().get(Utils.ueaIndex).getProfesor()+"@chatuam");
                }else if(Utils.usuario.getTipo().equals("PR")){
                    sendMessageUnicast(messageSend,Utils.usuario.getUeas().get(Utils.ueaIndex).getChats().get(Utils.chatIndex).getNombreChat()+"@chatuam");
                }
            }else{
                sendMessageMulticast(messageSend);
            }

        }
    }

    void recibirMensaje(){
        //noneView.setVisibility(View.GONE);
        //rvMessages.setVisibility(View.VISIBLE);
        ChatObject mensajes = ((((Utils.usuario.getUeas()).get(Utils.ueaIndex)).getChats()).get(Utils.chatIndex));
        mensajes.agregarMensaje("Recibido","UsuarioX",new Date());
        rvMessages.setAdapter(mAdapter);
    }

    private void sendMessageUnicast(String messageSend, String entityBareId) {
        EntityBareJid jid = null;
        try {
            jid = JidCreate.entityBareFrom(entityBareId);
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        }
        if(mConnection!=null){
            ChatManager chatManager = ChatManager.getInstanceFor(mConnection);
            Chat chat = chatManager.chatWith(jid);
            Message newMessage = new Message();
            newMessage.setBody(messageSend);
            try {
                chat.send(newMessage);
                ChatObject mensajes = ((((Utils.usuario.getUeas()).get(Utils.ueaIndex)).getChats()).get(Utils.chatIndex));
                mensajes.agregarMensaje(messageSend,Utils.usuario.getMatricula(),new Date());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        rvMessages.setAdapter(mAdapter);
                    }
                });

            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendMessageMulticast(String messageSend) {
        if(mConnection!=null){
            Message newMessage = new Message();
            newMessage.setBody(messageSend);
            try {
                multiUserChat.sendMessage(newMessage);
                ChatObject mensajes = ((((Utils.usuario.getUeas()).get(Utils.ueaIndex)).getChats()).get(Utils.chatIndex));
                mensajes.agregarMensaje(messageSend,Utils.usuario.getMatricula(),new Date());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        rvMessages.setAdapter(mAdapter);
                    }
                });

            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void UnicastConexion(){
        new Thread(){
            @Override
            public void run() {
                InetAddress addr = null;
                try {
                    addr = InetAddress.getByName("libio.cua.uam.mx");
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                HostnameVerifier verifier = new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return false;
                    }
                };
                DomainBareJid serviceName = null;
                EntityBareJid mucJid = null;
                try {
                    serviceName = JidCreate.domainBareFrom(Utils.usuario.getMatricula()+"@chatuam");
                } catch (XmppStringprepException e) {
                    e.printStackTrace();
                }

                if(Utils.usuario.getTipo().equals("AL")){
                    try {
                        mucJid = JidCreate.entityBareFrom(Utils.usuario.getUeas().get(Utils.ueaIndex).getProfesor()+"@chatuam");
                        //mucJid = JidCreate.entityBareFrom(Localpart.from(Utils.usuario.getUeas().get(ueaIndex).getProfesor()+"@chatuam"), serviceName);
                    } catch (XmppStringprepException e) {
                        e.printStackTrace();
                    }
                }else if(Utils.usuario.getTipo().equals("PR")){
                    try {
                        //Log.d("xd",""+Utils.usuario.getUeas().get(ueaIndex).getChats().get(chatIndex).getNombreChat());
                        //mucJid = JidCreate.entityBareFrom(Localpart.from(Utils.usuario.getUeas().get(ueaIndex).getChats().get(chatIndex).getNombreChat()+"@chatuam"), serviceName);
                        mucJid = JidCreate.entityBareFrom(Utils.usuario.getUeas().get(Utils.ueaIndex).getChats().get(Utils.chatIndex).getNombreChat()+"@chatuam");
                    } catch (XmppStringprepException e) {
                        e.printStackTrace();
                    }
                }



                XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                        .setUsernameAndPassword(Utils.usuario.getMatricula(),Utils.usuario.getPassword())
                        .setPort(5222)
                        .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                        .setXmppDomain(serviceName)
                        .setHostnameVerifier(verifier)
                        .setHostAddress(addr)
                        //.setDebuggerEnabled(true)
                        .build();
                mConnection = new XMPPTCPConnection(config);

                try {
                    mConnection.connect();
                    mConnection.login();

                    //PushNotificationsManager pn = new PushNotificationsManager(mConnection);
                    //OfflineMessageManager po = new OfflineMessageManager(mConnection);
                    //Log.d("offline","Recibiste: "+po.getMessageCount());
                    String send="";

                    if(Utils.usuario.getTipo().equals("AL")){
                            send = Utils.usuario.getUeas().get(Utils.ueaIndex).getProfesor()+"@chatuam";
                    }else if(Utils.usuario.getTipo().equals("PR")){
                            send = Utils.usuario.getUeas().get(Utils.ueaIndex).getChats().get(Utils.chatIndex).getNombreChat()+"@chatuam";
                    }
                    initMam(send,null);
                    if(mConnection.isAuthenticated()&&mConnection.isConnected()){
                        //Enviar y recibir mensaje
                        // Assume we've created an XMPPConnection name "connection"._
                        Log.e(TAG,"run: auth done and connected sucessfully");
                        ChatManager chatManager = ChatManager.getInstanceFor(mConnection);
                        chatManager.addIncomingListener(new IncomingChatMessageListener() {
                            @Override
                            public void newIncomingMessage(EntityBareJid from, Message message, org.jivesoftware.smack.chat2.Chat chat) {
                                Log.e(TAG,"Nuevo mensaje de " + from + ": " + message.getBody());

                                ChatObject mensajes = ((((Utils.usuario.getUeas()).get(Utils.ueaIndex)).getChats()).get(Utils.chatIndex));
                                //Mensaje data = new Mensaje("Recibido",message.getBody().toString()," ");
                                mensajes.agregarMensaje(message.getBody().toString(),from.toString(),new Date());
                                String nombreUEA=Utils.usuario.getUeas().get(Utils.ueaIndex).getNombre();
                                Utils.mostrarNotificacion(context,mensajes.getNombreChat().toString()+" "+nombreUEA,message.getBody().toString());

                                //mMessagesData.add(data);

                                //Actualizar la lista
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        rvMessages.setAdapter(mAdapter);
                                    }
                                });
                            }
                        });

                    }
                } catch (SmackException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XMPPException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void MulticastConexion(){
        new Thread(){
            @Override
            public void run() {
                InetAddress addr = null;
                try {
                    addr = InetAddress.getByName("libio.cua.uam.mx");
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                HostnameVerifier verifier = new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return false;
                    }
                };
                String claveGrupo = Utils.usuario.getUeas().get(Utils.ueaIndex).getClaveGrupo().toLowerCase();
                DomainBareJid serviceName = null;
                try {
                    //serviceName = JidCreate.domainBareFrom(Utils.usuario.getMatricula()+"@chatuam");
                    serviceName = JidCreate.domainBareFrom(claveGrupo+"@conference.chatuam");
                } catch (XmppStringprepException e) {
                    e.printStackTrace();
                }

                XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                        .setUsernameAndPassword(Utils.usuario.getMatricula(),Utils.usuario.getPassword())
                        .setPort(5222)
                        .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                        .setXmppDomain(serviceName)
                        .setHostnameVerifier(verifier)
                        .setHostAddress(addr)
                        //.setDebuggerEnabled(true)
                        .build();
                mConnection = new XMPPTCPConnection(config);



                DomainBareJid xmppServiceGroupDomain = null;
                EntityBareJid mucJid = null;
                EntityBareJid jidN = null;
                try {
                    Log.d("TAG","ClaveGrupo"+claveGrupo+"");
                    xmppServiceGroupDomain = JidCreate.domainBareFrom(claveGrupo+"@conference.chatuam");
                    mucJid = JidCreate.entityBareFrom(Localpart.from(claveGrupo), serviceName);
                    jidN =  JidCreate.entityBareFrom(claveGrupo+"@conference.chatuam");

                } catch (XmppStringprepException e) {
                    e.printStackTrace();
                }


                Date d = new Date(1999,1,1);
                try {
                    mConnection.connect();
                    mConnection.login();
                    if(mConnection.isAuthenticated()&&mConnection.isConnected()){
                        //Enviar y recibir mensaje
                        // Assume we've created an XMPPConnection name "connection"._

                        Log.e(TAG,"run: auth done and connected sucessfully");
                        MultiUserChatManager mucChatManager = MultiUserChatManager.getInstanceFor(mConnection);
                        multiUserChat = mucChatManager.getMultiUserChat(mucJid);
                        Resourcepart nickName = Resourcepart.from(Utils.usuario.getMatricula());
                        MucEnterConfiguration mucEnterConfiguration = multiUserChat.getEnterConfigurationBuilder(nickName).requestMaxStanzasHistory(100).build();
                        if(!multiUserChat.isJoined()){
                            multiUserChat.join(mucEnterConfiguration);
                        }
                        //Log.d("mam","lol: "+oldMsg.getBody());
                        initMam(claveGrupo+"@conference.chatuam",claveGrupo);
                        //initMam();
                        //OfflineMessageManager moff = new OfflineMessageManager(mConnection);
                        //MamManager mm = MamManager.getInstanceFor(mConnection);
                        //if(mm.isSupportedByServer()){
                         //   String claveGrupo = Utils.usuario.getUeas().get(ueaIndex).getClaveGrupo().toLowerCase();
                         //   MamManager.MamQueryResult mquery = mm.mostRecentPage(mucJid,15);
                         //   Log.d("mam",mquery.forwardedMessages.size()+"");
                        //}

                        multiUserChat.addMessageListener(new MessageListener() {
                            @Override
                            public void processMessage(Message message) {
                                if(!TextUtils.isEmpty(message.getBody())) {
                                    if(!message.getFrom().getResourceOrEmpty().toString().equals(Utils.usuario.getMatricula())){
                                        ChatObject mensajes = ((((Utils.usuario.getUeas()).get(Utils.ueaIndex)).getChats()).get(Utils.chatIndex));
                                        mensajes.agregarMensaje(message.getBody(),message.getFrom().getResourceOrEmpty().toString(),new Date());
                                        String nombreUEA=Utils.usuario.getUeas().get(Utils.ueaIndex).getNombre();
                                        Utils.mostrarNotificacion(context,mensajes.getNombreChat().toString()+" "+nombreUEA,message.getBody().toString());
                                        Log.d("TAG2",message.getFrom().getResourceOrEmpty().toString());
                                        //Actualizar la lista
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                rvMessages.setAdapter(mAdapter);
                                            }
                                        });
                                    }
                                }
                            }
                        });
                    }
                } catch (SmackException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XMPPException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }


    private void initMam(String jidId,String mucx) throws XMPPException.XMPPErrorException, InterruptedException, SmackException.NotConnectedException, SmackException.NotLoggedInException, SmackException.NoResponseException, XmppStringprepException {
        //MamManager manager = MamManager.getInstanceFor(mConnection);
        MamManager manager;
        if(mucx==null){
            manager = MamManager.getInstanceFor(mConnection);
        }else{
            manager = MamManager.getInstanceFor(mConnection);
        }
        MamManager.MamQuery r = null;
        MamManager.MamQueryArgs na=null;
        Date d = new Date(1999,1,1);
        //Log.d("jiddebug",jidId);
        try {
            MamManager.MamQueryArgs.Builder b = MamManager.MamQueryArgs.builder().queryLastPage();
            if (mucx == null) {
                na = b.limitResultsToJid(JidCreate.from(jidId)).build();
            }else{
                ArrayList<String> alumnos = Utils.usuario.getUeas().get(Utils.getUEAindex(mucx)).getInscritos();
                b.limitResultsToJid(JidCreate.bareFrom(mucx+"@conference.chatuam"));
                for(int i=0;i<alumnos.size();i++){
                    Log.d("jidImport",""+alumnos.get(i));
                    //formFields.add(formField);
                    //b.limitResultsToJid(JidCreate.bareFrom(Localpart.from(alumnos.get(i))));

                }
                //b.withAdditionalFormFields(formFields);
                na = b.build();
            }

            //r = manager.queryMostRecentPage(j,100);
            r = manager.queryArchive(na);
            if (r.getMessages().size() >= 1) //printing first of them
            {
                for(int i=0;i<r.getMessages().size();i++){
                    Message message = (Message) r.getMessages().get(i);
                    Log.d("mam", "message received" + message.getBody());
                    ChatObject mensajes = ((((Utils.usuario.getUeas()).get(Utils.ueaIndex)).getChats()).get(Utils.chatIndex));
                    if(mucx==null){
                        mensajes.agregarMensaje(message.getBody(),message.getFrom().getLocalpartOrNull().toString(),new Date());
                    }else{
                        String s = message.getFrom().toString();
                        s = s.substring(s.lastIndexOf("/") + 1);
                        Log.d("jiddebug","Mensaje recibido de: "+s);
                        mensajes.agregarMensaje(message.getBody(),s,new Date());
                    }

                    //Log.d("jiddebug","Mensaje recibido de: "+message.getFrom().toString());
                    //Actualizar la lista
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            rvMessages.setAdapter(mAdapter);
                        }
                    });
                }

            }
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotLoggedInException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        }
        ChatObject mensajes = ((((Utils.usuario.getUeas()).get(Utils.ueaIndex)).getChats()).get(Utils.chatIndex));
        Log.d("prueba",""+mensajes.getMensajes().isEmpty());
        if(!mensajes.getMensajes().isEmpty()){
            //vacio=false;
            //noneView.setVisibility(View.GONE);
            //rvMessages.setVisibility(View.VISIBLE);
        }else{
           // vacio=true;
            //noneView.setVisibility(View.VISIBLE);
            //rvMessages.setVisibility(View.GONE);
        }

    }

    private static FormField getWithFormField(Jid withJid) {
        FormField formField = new FormField("with");
        formField.addValue(withJid.toString());
        return formField;
    }


    @Override
    public void onBackPressed() {
        ChatObject mensajes = ((((Utils.usuario.getUeas()).get(Utils.ueaIndex)).getChats()).get(Utils.chatIndex));
        mensajes.limpiar();
        mConnection.disconnect();
        super.onBackPressed();
    }
}