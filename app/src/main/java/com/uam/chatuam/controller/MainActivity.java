package com.uam.chatuam.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.uam.chatuam.Connect;
import com.uam.chatuam.R;
import com.uam.chatuam.Theme;
import com.uam.chatuam.Utils;
import com.uam.chatuam.model.Chat;
import com.uam.chatuam.model.Mensaje;
import com.uam.chatuam.model.UEA;
import com.uam.chatuam.model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    EditText etUser;
    EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeViews();
        //Utils.configurarTema(this);
        //if(Utils.darkTheme=false) {
        //    Theme.cambiarATema(this,Theme.Cuajimalpa_Light_NoActionBar);
        //}
        //else if(Utils.darkTheme=true){
        //    Theme.cambiarATema(this,Theme.Cuajimalpa_Dark_NoActionBar);
        //}
    }

    private void initializeViews(){
        etUser = (EditText) findViewById(R.id.input_usr);
        etPassword = (EditText) findViewById(R.id.input_pass);
    }

    public void login(View v){
        //startActivity(new Intent(MainActivity.this, HubActivity.class));
        ArrayList<String> taskParams = new ArrayList<String>();
        taskParams.add(etUser.getText().toString());
        taskParams.add(etPassword.getText().toString());
        LoginAsync loginAsync = new LoginAsync(this);
        loginAsync.execute(taskParams);
    }

    public class LoginAsync extends AsyncTask<ArrayList<String>,String,String>{
        LoadingDialog dialog;
        Activity activity;
        String z = "";
        boolean isSuccess = false;
        Connect connect = new Connect();
        String usuario="";
        String nombre="";
        String contrasena="";
        String tipo="";

        public LoginAsync(Activity activity) {
            this.activity = activity;
            dialog = new LoadingDialog(this.activity);
        }

        @Override
        protected void onPreExecute() {
            dialog.startLoadingDialog();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismissDialog();
            if (isSuccess) {
                Toast.makeText(getApplicationContext(), "Has iniciado sesion", Toast.LENGTH_LONG).show();
                GroupsAsync groupsAsync = new GroupsAsync(activity);
                groupsAsync.execute();
                //startActivity(new Intent(MainActivity.this, HubActivity.class));
            }else {
                Toast.makeText(getApplicationContext(), "No has podido iniciar sesion", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected String doInBackground(ArrayList<String>... params) {
            String usernameInput = params[0].get(0);
            String passwordInput = params[0].get(1);
            try {
                Connection con = connect.getConnection();
                if (con == null) {
                    z = "Please check your internet connection";
                } else {
                    String sql="SELECT * FROM usuarios WHERE matricula = ?;";
                    PreparedStatement ps=con.prepareStatement(sql);
                    ps.setString(1,usernameInput);
                    ResultSet resp = ps.executeQuery();
                    while (resp.next()){
                        usuario = resp.getString("matricula");
                        nombre = resp.getString("nombre");
                        contrasena = resp.getString("contrasena");
                        tipo = resp.getString("tipo");
                        Log.d("ReadUser",tipo);
                    }
                    if(usuario.equals(usernameInput)&&contrasena.equals(passwordInput)){
                        isSuccess=true;
                        Utils.usuario = new Usuario(nombre,usuario,new ArrayList<UEA>(),tipo);
                        sql="SELECT * FROM inscripciones WHERE matricula = ?;";
                        ps=con.prepareStatement(sql);
                        ps.setString(1,usernameInput);
                        resp = ps.executeQuery();
                        while (resp.next()){
                            String clave = resp.getString("claveGrupo");
                            String profesor = resp.getString("numeroEconomico");
                            ArrayList<Chat> chats = new ArrayList<Chat>();
                            UEA uea = new UEA(clave,profesor,chats);
                            (Utils.usuario.getUeas()).add(uea);
                        }
                        for(int i=0;i<Utils.usuario.getUeas().size();i++){
                            String grupo = Utils.usuario.getUeas().get(i).getClaveGrupo();
                            sql="SELECT nombre FROM grupos WHERE claveGrupo = ?;";
                            ps=con.prepareStatement(sql);
                            ps.setString(1,grupo);
                            resp = ps.executeQuery();
                            while (resp.next()){
                                String nombre = resp.getString("nombre");
                                Utils.usuario.getUeas().get(i).setNombre(nombre);
                            }
                        }
                    }
                }
                con.close();
            }catch (Exception ex) {
                isSuccess = false;
                z = "Exceptions" + ex;
                Log.e("ERRO", z);
            }

            return z;
        }
    }

    public class GroupsAsync extends AsyncTask<Void,String,String>{
        LoadingDialog dialog;
        Activity activity;
        String z = "";
        boolean isSuccess = false;
        Connect connect = new Connect();

        public GroupsAsync(Activity activity) {
            this.activity = activity;
            dialog = new LoadingDialog(this.activity);
        }

        @Override
        protected void onPreExecute() {
            dialog.startLoadingDialog();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismissDialog();
            if (isSuccess) {
                if(Utils.usuario.getTipo().equals("PR")){
                    for(int i=0;i<Utils.usuario.getUeas().size();i++){
                        ArrayList<Chat> chatsActuales = Utils.usuario.getUeas().get(i).getChats();
                        Collections.sort(chatsActuales.subList(1,chatsActuales.size()), new Comparator<Chat>() {
                            public int compare(Chat v1, Chat v2) {
                                return v1.getNombreChat().compareTo(v2.getNombreChat());
                            }
                        });
                    }
                }
                Toast.makeText(getApplicationContext(), "Se han cargado los chats", Toast.LENGTH_LONG).show();
                startActivity(new Intent(MainActivity.this, HubActivity.class));
            }else {
                Toast.makeText(getApplicationContext(), "No se han podido cargar los chats", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected String doInBackground(Void... params) {
            for(int i=0;i<(Utils.usuario.getUeas().size());i++){
                String tipoUsuario = Utils.usuario.getTipo();
                UEA ueaActual = (Utils.usuario.getUeas()).get(i);
                if(tipoUsuario.equals("AL")){
                    ueaActual.addChat("Grupo");
                    ueaActual.addChat("Profesor");
                }else if(tipoUsuario.equals("PR")){
                    Log.d("EsProfesor","paso");
                    ueaActual.addChat("Grupo");
                    try{
                        Connection con = connect.getConnection();
                        if (con == null) {
                            z = "Please check your internet connection";
                        } else{
                            String sql="SELECT matricula FROM inscripciones WHERE numeroEconomico = ? AND claveGrupo = ?;";
                            PreparedStatement ps=con.prepareStatement(sql);
                            ps.setString(1,Utils.usuario.getMatricula());
                            ps.setString(2,ueaActual.getClaveGrupo());
                            Log.d("Consulta",Utils.usuario.getMatricula()+" y "+ueaActual.getClaveGrupo());
                            ResultSet resp = ps.executeQuery();
                            while (resp.next()){
                                Log.d("prueba","paso");
                                String matriculaActual = resp.getString("matricula");
                                ueaActual.addChat(matriculaActual+"");
                            }
                        }
                    }catch (Exception ex) {
                        isSuccess = false;
                        z = "Exceptions" + ex;
                        Log.e("ERRO", z);
                    }
                }
                isSuccess=true;
            }

            return z;
        }
    }

}