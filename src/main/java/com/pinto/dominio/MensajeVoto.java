package com.pinto.dominio;

/**
 * DTO para mensajes de votación por WebSocket
 */
public class MensajeVoto {

    private String tipo; // "votar", "todos_votaron", "siguiente_fase", "resultado"
    private String salaId;
    private String username;
    private String equipoVotado; // "rojo" o "azul"
    private int puntaje; // 1-10
    private int faseActual; // 1=votando rojo, 2=votando azul, 3=resultado

    // Datos para mostrar en UI
    private String dibujoActual; // base64 del dibujo actual
    private int puntajeRojo;
    private int puntajeAzul;
    private String ganador;
    private int votosRecibidos;
    private int siguienteFase; // índice del siguiente dibujo a votar

    public MensajeVoto() {
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEquipoVotado() {
        return equipoVotado;
    }

    public void setEquipoVotado(String equipoVotado) {
        this.equipoVotado = equipoVotado;
    }

    public int getPuntaje() {
        return puntaje;
    }

    public void setPuntaje(int puntaje) {
        this.puntaje = puntaje;
    }

    public int getFaseActual() {
        return faseActual;
    }

    public void setFaseActual(int faseActual) {
        this.faseActual = faseActual;
    }

    public String getDibujoActual() {
        return dibujoActual;
    }

    public void setDibujoActual(String dibujoActual) {
        this.dibujoActual = dibujoActual;
    }

    public int getPuntajeRojo() {
        return puntajeRojo;
    }

    public void setPuntajeRojo(int puntajeRojo) {
        this.puntajeRojo = puntajeRojo;
    }

    public int getPuntajeAzul() {
        return puntajeAzul;
    }

    public void setPuntajeAzul(int puntajeAzul) {
        this.puntajeAzul = puntajeAzul;
    }

    public String getGanador() {
        return ganador;
    }

    public void setGanador(String ganador) {
        this.ganador = ganador;
    }

    public int getVotosRecibidos() {
        return votosRecibidos;
    }

    public void setVotosRecibidos(int votosRecibidos) {
        this.votosRecibidos = votosRecibidos;
    }

    public int getSiguienteFase() {
        return siguienteFase;
    }

    public void setSiguienteFase(int siguienteFase) {
        this.siguienteFase = siguienteFase;
    }
}
