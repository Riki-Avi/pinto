package com.pinto.dominio;

import java.util.List;

/**
 * DTO para mensajes WebSocket relacionados con la sala.
 */
public class MensajeSala {

    private String tipo; // "unirse", "equipo", "salir", "empezar", "actualizar", "error"
    private String salaId;
    private String codigo;
    private String username;
    private String equipo; // "rojo" o "azul"
    private List<String> equipoRojo;
    private List<String> equipoAzul;
    private boolean puedeEmpezar;
    private String mensaje; // Para errores o notificaciones

    public MensajeSala() {
    }

    // Constructor para actualizaciones de estado
    public static MensajeSala actualizacion(Sala sala) {
        MensajeSala msg = new MensajeSala();
        msg.setTipo("actualizar");
        msg.setSalaId(sala.getId());
        msg.setCodigo(sala.getCodigo());
        msg.setEquipoRojo(sala.getEquipoRojo());
        msg.setEquipoAzul(sala.getEquipoAzul());
        msg.setPuedeEmpezar(sala.puedeEmpezar());
        return msg;
    }

    // Constructor para iniciar partida
    public static MensajeSala empezar(Sala sala) {
        MensajeSala msg = new MensajeSala();
        msg.setTipo("empezar");
        msg.setSalaId(sala.getId());
        msg.setCodigo(sala.getCodigo());
        return msg;
    }

    // Constructor para errores
    public static MensajeSala error(String mensaje) {
        MensajeSala msg = new MensajeSala();
        msg.setTipo("error");
        msg.setMensaje(mensaje);
        return msg;
    }

    // Getters y Setters
    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getSalaId() {
        return salaId;
    }

    public void setSalaId(String salaId) {
        this.salaId = salaId;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEquipo() {
        return equipo;
    }

    public void setEquipo(String equipo) {
        this.equipo = equipo;
    }

    public List<String> getEquipoRojo() {
        return equipoRojo;
    }

    public void setEquipoRojo(List<String> equipoRojo) {
        this.equipoRojo = equipoRojo;
    }

    public List<String> getEquipoAzul() {
        return equipoAzul;
    }

    public void setEquipoAzul(List<String> equipoAzul) {
        this.equipoAzul = equipoAzul;
    }

    public boolean isPuedeEmpezar() {
        return puedeEmpezar;
    }

    public void setPuedeEmpezar(boolean puedeEmpezar) {
        this.puedeEmpezar = puedeEmpezar;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
