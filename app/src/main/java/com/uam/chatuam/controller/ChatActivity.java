package com.uam.chatuam.controller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.uam.chatuam.R;
import com.uam.chatuam.Utils;
import com.uam.chatuam.adapters.AdapterGroup;
import com.uam.chatuam.adapters.AdapterMessages;
import com.uam.chatuam.model.Chat;
import com.uam.chatuam.model.Mensaje;
import com.uam.chatuam.model.UEA;

import java.util.ArrayList;
import java.util.Date;

public class ChatActivity extends AppCompatActivity {

    int chatIndex;
    int ueaIndex;
    EditText entradaMensaje;
    private RecyclerView rvMessages;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLmanager;

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
        String mensajeTemp = entradaMensaje.getText().toString();
        ((((Utils.usuario.getUeas()).get(ueaIndex)).getChats()).get(chatIndex)).agregarMensaje(mensajeTemp,Utils.usuario.getNombre(),new Date());
        rvMessages.setAdapter(mAdapter);
    }

    void recibirMensaje(){
        Chat mensajes = ((((Utils.usuario.getUeas()).get(ueaIndex)).getChats()).get(chatIndex));
        mensajes.agregarMensaje("Recibido","UsuarioX",new Date());
        rvMessages.setAdapter(mAdapter);
    }

}