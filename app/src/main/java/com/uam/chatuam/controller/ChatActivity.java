package com.uam.chatuam.controller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.muc.MucEnterConfiguration;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Localpart;
import org.jxmpp.jid.parts.Resourcepart;
import org.jxmpp.stringprep.XmppStringprepException;

public class ChatActivity extends AppCompatActivity {

    int chatIndex;
    int ueaIndex;
    EditText entradaMensaje;
    private RecyclerView rvMessages;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLmanager;
    private ArrayList<Mensaje> mMessagesData = new ArrayList<Mensaje>();
    private AbstractXMPPConnection mConnection;
    public static final String TAG = MainActivity.class.getSimpleName();
    private MultiUserChat multiUserChat=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.CuajimalpaLightActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        ueaIndex=intent.getIntExtra("ueaIndex",0);
        chatIndex=intent.getIntExtra("chatIndex",0);
        setTitle((((Utils.usuario.getUeas()).get(ueaIndex).getChats()).get(chatIndex)).getNombreChat());
        entradaMensaje = findViewById(R.id.input_text_message);

        if(chatIndex>0){
            UnicastConexion();
        }else{
            MulticastConexion();
        }

        rvMessages = findViewById(R.id.rvMessages);
        rvMessages.setHasFixedSize(true);
        mLmanager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,true);
        rvMessages.setLayoutManager(mLmanager);
        mAdapter = new AdapterMessages((((Utils.usuario.getUeas()).get(ueaIndex).getChats()).get(chatIndex)).getMensajes());
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
        if(messageSend.length()>0){
            if(chatIndex>0){
                if(Utils.usuario.getTipo().equals("AL")){
                    sendMessageUnicast(messageSend,Utils.usuario.getUeas().get(ueaIndex).getProfesor()+"@chatuam");
                }else if(Utils.usuario.getTipo().equals("PR")){
                    sendMessageUnicast(messageSend,Utils.usuario.getUeas().get(ueaIndex).getChats().get(chatIndex).getNombreChat()+"@chatuam");
                }
            }else{
                sendMessageMulticast(messageSend);
            }

        }
    }

    void recibirMensaje(){
        ChatObject mensajes = ((((Utils.usuario.getUeas()).get(ueaIndex)).getChats()).get(chatIndex));
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
                ChatObject mensajes = ((((Utils.usuario.getUeas()).get(ueaIndex)).getChats()).get(chatIndex));
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
                ChatObject mensajes = ((((Utils.usuario.getUeas()).get(ueaIndex)).getChats()).get(chatIndex));
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
                    addr = InetAddress.getByName("54.226.48.61");
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
                try {
                    serviceName = JidCreate.domainBareFrom(Utils.usuario.getMatricula()+"@chatuam");
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
                        .setDebuggerEnabled(true)
                        .build();
                mConnection = new XMPPTCPConnection(config);

                try {
                    mConnection.connect();
                    mConnection.login();
                    if(mConnection.isAuthenticated()&&mConnection.isConnected()){
                        //Enviar y recibir mensaje
                        // Assume we've created an XMPPConnection name "connection"._
                        Log.e(TAG,"run: auth done and connected sucessfully");
                        ChatManager chatManager = ChatManager.getInstanceFor(mConnection);
                        chatManager.addIncomingListener(new IncomingChatMessageListener() {
                            @Override
                            public void newIncomingMessage(EntityBareJid from, Message message, org.jivesoftware.smack.chat2.Chat chat) {
                                Log.e(TAG,"Nuevo mensaje de " + from + ": " + message.getBody());

                                ChatObject mensajes = ((((Utils.usuario.getUeas()).get(ueaIndex)).getChats()).get(chatIndex));
                                //Mensaje data = new Mensaje("Recibido",message.getBody().toString()," ");
                                mensajes.agregarMensaje(message.getBody().toString(),from.toString(),new Date());
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
                    addr = InetAddress.getByName("54.226.48.61");
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
                try {
                    serviceName = JidCreate.domainBareFrom(Utils.usuario.getMatricula()+"@chatuam");
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
                        .setDebuggerEnabled(true)
                        .build();
                mConnection = new XMPPTCPConnection(config);

                DomainBareJid xmppServiceGroupDomain = null;
                EntityBareJid mucJid = null;
                try {
                    String claveGrupo = Utils.usuario.getUeas().get(ueaIndex).getClaveGrupo().toLowerCase();
                    Log.d("TAG","ClaveGrupo"+claveGrupo+"");
                    xmppServiceGroupDomain = JidCreate.domainBareFrom(claveGrupo+"@conference.chatuam");
                    mucJid = JidCreate.entityBareFrom(Localpart.from(claveGrupo), xmppServiceGroupDomain);
                } catch (XmppStringprepException e) {
                    e.printStackTrace();
                }


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
                        MucEnterConfiguration mucEnterConfiguration = multiUserChat.getEnterConfigurationBuilder(nickName).requestNoHistory().build();
                        if(!multiUserChat.isJoined()){
                            multiUserChat.join(mucEnterConfiguration);
                        }
                        multiUserChat.addMessageListener(new MessageListener() {
                            @Override
                            public void processMessage(Message message) {
                                if(!TextUtils.isEmpty(message.getBody())) {
                                    if(!message.getFrom().getResourceOrEmpty().toString().equals(Utils.usuario.getMatricula())){
                                        ChatObject mensajes = ((((Utils.usuario.getUeas()).get(ueaIndex)).getChats()).get(chatIndex));
                                        mensajes.agregarMensaje(message.getBody(),message.getFrom().getResourceOrEmpty().toString(),new Date());
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

}