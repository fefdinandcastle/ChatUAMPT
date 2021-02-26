package com.uam.chatuam.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChatObject {
    String nombreChat;
    ArrayList<Mensaje> mensajes;  //Esto  es un comentario Edgar
    //Es mi comentario Gerardo

    public ChatObject(String nombreChat, ArrayList<Mensaje> mensaje) {
        this.nombreChat = nombreChat;
        this.mensajes = mensaje;
    }

    public String getNombreChat() {
        return nombreChat;
    }

    public void setNombreChat(String nombreChat) {
        this.nombreChat = nombreChat;
    }

    public ArrayList<Mensaje> getMensajes() {
        return mensajes;
    }

    public void agregarMensaje(String mensaje, String usuario, Date date){
        Mensaje mensajeTemp = new Mensaje(mensaje,usuario,getFormattedHour(date));
        mensajes.add(0,mensajeTemp);
    }

    public void limpiar(){
        mensajes.clear();
    }

    public String getFormattedHour(Date date){
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        return formatter.format(date);
    }

}
