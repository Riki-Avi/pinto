package com.pinto.dominio;

/**
 * DTO que representa un trazo de dibujo.
 * Se envía por WebSocket cuando un jugador dibuja.
 */
public class MensajeTrazo {

    private String tipo; // "inicio", "dibujo", "fin", "borrar"
    private double x; // Coordenada X
    private double y; // Coordenada Y
    private String color; // Color del trazo (#RRGGBB)
    private int grosor; // Grosor del pincel
    private String jugador; // Nombre del jugador que dibuja

    public MensajeTrazo() {
    }

    public MensajeTrazo(String tipo, double x, double y, String color, int grosor, String jugador) {
        this.tipo = tipo;
        this.x = x;
        this.y = y;
        this.color = color;
        this.grosor = grosor;
        this.jugador = jugador;
    }

    // Getters y Setters
    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getGrosor() {
        return grosor;
    }

    public void setGrosor(int grosor) {
        this.grosor = grosor;
    }

    public String getJugador() {
        return jugador;
    }

    public void setJugador(String jugador) {
        this.jugador = jugador;
    }
}
