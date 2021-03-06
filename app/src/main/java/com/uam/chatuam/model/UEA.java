package com.uam.chatuam.model;

import android.util.Log;

import java.util.ArrayList;

public class UEA {
    String nombre;
    String claveGrupo;
    String profesor;
    ArrayList<ChatObject> chats;
    ArrayList<String> inscritos;

    public UEA(String claveGrupo, String profesor, ArrayList<ChatObject> chats) {
        this.claveGrupo = claveGrupo;
        this.profesor = profesor;
        this.chats = chats;
        inscritos=new ArrayList<String>();
    }

    public void addAlumno(String s){
        inscritos.add(s);
    }

    public ArrayList<String> getInscritos() {
        return inscritos;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public ArrayList<ChatObject> getChats() {
        return chats;
    }

    public void addChat(String nombreChat){
        chats.add(new ChatObject(nombreChat,new ArrayList<Mensaje>()));
        Log.d("Agregado","Se agrego "+nombreChat+" y el tamano es "+chats.size());
    }

    public String getClaveGrupo() {
        return claveGrupo;
    }

    public String getProfesor() {
        return profesor;
    }
}
