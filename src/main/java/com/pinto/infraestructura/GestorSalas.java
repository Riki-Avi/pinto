package com.pinto.infraestructura;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.pinto.dominio.Sala;

/**
 * Servicio que gestiona las salas de juego en memoria.
 * Usa ConcurrentHashMap para thread-safety.
 */
@Service
public class GestorSalas {

    // Mapa de código -> Sala
    private Map<String, Sala> salas = new ConcurrentHashMap<>();

    /**
     * Crea una nueva sala y agrega al creador al equipo rojo.
     */
    public Sala crearSala(String creador) {
        Sala sala = new Sala(creador);
        sala.agregarJugador(creador, "rojo"); // Creador empieza en equipo rojo
        salas.put(sala.getCodigo(), sala);
        return sala;
    }

    /**
     * Obtiene una sala por su código.
     */
    public Sala obtenerSala(String codigo) {
        return salas.get(codigo);
    }

    /**
     * Agrega un jugador a una sala.
     */
    public boolean unirseASala(String codigo, String username, String equipo) {
        Sala sala = salas.get(codigo);
        if (sala == null || sala.isEnPartida()) {
            return false;
        }
        return sala.agregarJugador(username, equipo);
    }

    /**
     * Cambia el equipo de un jugador.
     */
    public boolean cambiarEquipo(String codigo, String username, String nuevoEquipo) {
        Sala sala = salas.get(codigo);
        if (sala == null || sala.isEnPartida()) {
            return false;
        }
        return sala.cambiarEquipo(username, nuevoEquipo);
    }

    /**
     * Remueve un jugador de la sala.
     */
    public void salirDeSala(String codigo, String username) {
        Sala sala = salas.get(codigo);
        if (sala != null) {
            sala.removerJugador(username);
            // Si la sala queda vacía, eliminarla
            if (sala.getTotalJugadores() == 0) {
                salas.remove(codigo);
            }
        }
    }

    /**
     * Marca la sala como en partida.
     */
    public boolean iniciarPartida(String codigo) {
        Sala sala = salas.get(codigo);
        if (sala != null && sala.puedeEmpezar()) {
            sala.setEnPartida(true);
            return true;
        }
        return false;
    }

    /**
     * Obtiene lista de salas disponibles (no llenas y no en partida).
     */
    public List<Sala> obtenerSalasDisponibles() {
        List<Sala> disponibles = new ArrayList<>();
        for (Sala sala : salas.values()) {
            if (!sala.estaLlena() && !sala.isEnPartida()) {
                disponibles.add(sala);
            }
        }
        return disponibles;
    }

    /**
     * Elimina una sala (cuando termina la partida).
     */
    public void eliminarSala(String codigo) {
        salas.remove(codigo);
    }
}
