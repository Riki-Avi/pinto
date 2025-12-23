package com.pinto.dominio;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Representa una sala de juego con dos equipos.
 * Se mantiene en memoria, no se persiste en BD.
 */
public class Sala {

    private String id;
    private String codigo;
    private List<String> equipoRojo;
    private List<String> equipoAzul;
    private String creador;
    private boolean enPartida;

    public Sala(String creador) {
        this.id = UUID.randomUUID().toString();
        this.codigo = generarCodigo();
        this.equipoRojo = new ArrayList<>();
        this.equipoAzul = new ArrayList<>();
        this.creador = creador;
        this.enPartida = false;
    }

    private String generarCodigo() {
        // Genera código de 6 caracteres alfanuméricos
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            int index = (int) (Math.random() * chars.length());
            sb.append(chars.charAt(index));
        }
        return sb.toString();
    }

    public boolean agregarJugador(String username, String equipo) {
        if (estaLlena() || estaEnSala(username)) {
            return false;
        }

        if ("rojo".equals(equipo) && equipoRojo.size() < 2) {
            equipoRojo.add(username);
            return true;
        } else if ("azul".equals(equipo) && equipoAzul.size() < 2) {
            equipoAzul.add(username);
            return true;
        }
        return false;
    }

    public boolean cambiarEquipo(String username, String nuevoEquipo) {
        // Remover del equipo actual
        equipoRojo.remove(username);
        equipoAzul.remove(username);

        // Agregar al nuevo equipo
        if ("rojo".equals(nuevoEquipo) && equipoRojo.size() < 2) {
            equipoRojo.add(username);
            return true;
        } else if ("azul".equals(nuevoEquipo) && equipoAzul.size() < 2) {
            equipoAzul.add(username);
            return true;
        }
        return false;
    }

    public void removerJugador(String username) {
        equipoRojo.remove(username);
        equipoAzul.remove(username);
    }

    public boolean estaLlena() {
        return equipoRojo.size() + equipoAzul.size() >= 4;
    }

    public boolean estaEnSala(String username) {
        return equipoRojo.contains(username) || equipoAzul.contains(username);
    }

    public boolean puedeEmpezar() {
        return equipoRojo.size() == 2 && equipoAzul.size() == 2;
    }

    public int getTotalJugadores() {
        return equipoRojo.size() + equipoAzul.size();
    }

    public String getEquipoDeJugador(String username) {
        if (equipoRojo.contains(username))
            return "rojo";
        if (equipoAzul.contains(username))
            return "azul";
        return null;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public List<String> getEquipoRojo() {
        return equipoRojo;
    }

    public List<String> getEquipoAzul() {
        return equipoAzul;
    }

    public String getCreador() {
        return creador;
    }

    public boolean isEnPartida() {
        return enPartida;
    }

    public void setEnPartida(boolean enPartida) {
        this.enPartida = enPartida;
    }
}
