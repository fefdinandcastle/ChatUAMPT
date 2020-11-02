package com.uam.chatuam.model;

public class Mensaje {
    String mensaje;
    String usuario;
    String tiempo;

    public Mensaje(String mensaje, String usuario, String tiempo) {
        this.mensaje = mensaje;
        this.usuario = usuario;
        this.tiempo = tiempo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getTiempo() {
        return tiempo;
    }

    public void setTiempo(String tiempo) {
        this.tiempo = tiempo;
    }
}
