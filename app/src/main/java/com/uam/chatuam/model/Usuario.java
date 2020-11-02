package com.uam.chatuam.model;

import java.util.ArrayList;

public class Usuario {
    private String nombre;
    private String matricula;
    private ArrayList<UEA> ueas;
    private String tipo;
    private String password;

    public Usuario(String nombre, String matricula, ArrayList<UEA> ueas, String tipo, String password) {
        this.nombre = nombre;
        this.matricula = matricula;
        this.ueas = ueas;
        this.tipo = tipo;
        this.password = password;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public ArrayList<UEA> getUeas() {
        return ueas;
    }

    public void setUeas(ArrayList<UEA> ueas) {
        this.ueas = ueas;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getPassword() {
        return password;
    }
}
