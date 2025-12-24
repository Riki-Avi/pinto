package com.pinto.presentacion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.pinto.dominio.MensajeSala;
import com.pinto.dominio.Sala;
import com.pinto.infraestructura.GestorSalas;

/**
 * Controlador WebSocket para las salas en tiempo real.
 */
@Controller
public class ControladorSalaWS {

    private GestorSalas gestorSalas;
    @SuppressWarnings("unused")
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    public ControladorSalaWS(GestorSalas gestorSalas, SimpMessagingTemplate messagingTemplate) {
        this.gestorSalas = gestorSalas;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Jugador se une a la sala.
     * Cliente envía a: /app/sala/{codigo}/unirse
     * Servidor responde a: /topic/sala/{codigo}
     */
    @MessageMapping("/sala/{codigo}/unirse")
    @SendTo("/topic/sala/{codigo}")
    public MensajeSala unirse(@DestinationVariable String codigo, MensajeSala mensaje) {
        Sala sala = gestorSalas.obtenerSala(codigo);
        if (sala == null) {
            return MensajeSala.error("Sala no encontrada");
        }

        // Si ya está en la sala, solo enviar estado actual
        if (sala.estaEnSala(mensaje.getUsername())) {
            return MensajeSala.actualizacion(sala);
        }

        // Intentar unirse con el equipo solicitado (o el que tenga espacio)
        String equipo = mensaje.getEquipo();
        if (equipo == null) {
            // Elegir equipo con espacio
            equipo = sala.getEquipoRojo().size() < 2 ? "rojo" : "azul";
        }

        boolean exito = gestorSalas.unirseASala(codigo, mensaje.getUsername(), equipo);
        if (!exito) {
            return MensajeSala.error("No se pudo unir a la sala");
        }

        return MensajeSala.actualizacion(sala);
    }

    /**
     * Jugador cambia de equipo.
     */
    @MessageMapping("/sala/{codigo}/equipo")
    @SendTo("/topic/sala/{codigo}")
    public MensajeSala cambiarEquipo(@DestinationVariable String codigo, MensajeSala mensaje) {
        Sala sala = gestorSalas.obtenerSala(codigo);
        if (sala == null) {
            return MensajeSala.error("Sala no encontrada");
        }

        boolean exito = gestorSalas.cambiarEquipo(codigo, mensaje.getUsername(), mensaje.getEquipo());
        if (!exito) {
            return MensajeSala.error("Equipo lleno");
        }

        return MensajeSala.actualizacion(sala);
    }

    /**
     * Jugador sale de la sala.
     */
    @MessageMapping("/sala/{codigo}/salir")
    @SendTo("/topic/sala/{codigo}")
    public MensajeSala salir(@DestinationVariable String codigo, MensajeSala mensaje) {
        gestorSalas.salirDeSala(codigo, mensaje.getUsername());

        Sala sala = gestorSalas.obtenerSala(codigo);
        if (sala == null) {
            // Sala eliminada porque quedó vacía
            MensajeSala respuesta = new MensajeSala();
            respuesta.setTipo("sala_eliminada");
            return respuesta;
        }

        return MensajeSala.actualizacion(sala);
    }

    /**
     * Iniciar la partida (todos van a home.html).
     */
    @MessageMapping("/sala/{codigo}/empezar")
    @SendTo("/topic/sala/{codigo}")
    public MensajeSala empezar(@DestinationVariable String codigo, MensajeSala mensaje) {
        Sala sala = gestorSalas.obtenerSala(codigo);
        if (sala == null) {
            return MensajeSala.error("Sala no encontrada");
        }

        if (!sala.puedeEmpezar()) {
            return MensajeSala.error("Se necesitan 4 jugadores (2 por equipo)");
        }

        gestorSalas.iniciarPartida(codigo);
        return MensajeSala.empezar(sala);
    }

    /**
     * Cambiar modo de juego (equipos vs memoria).
     */
    @MessageMapping("/sala/{codigo}/modo")
    @SendTo("/topic/sala/{codigo}")
    public MensajeSala cambiarModo(@DestinationVariable String codigo, MensajeSala mensaje) {
        Sala sala = gestorSalas.obtenerSala(codigo);
        if (sala == null) {
            return MensajeSala.error("Sala no encontrada");
        }

        // Guardar el modo en la sala
        sala.setModo(mensaje.getEquipo()); // Reutilizamos equipo para pasar el modo

        // Notificar a todos
        MensajeSala respuesta = MensajeSala.actualizacion(sala);
        respuesta.setTipo("modo_cambiado");
        respuesta.setEquipo(sala.getModo());
        return respuesta;
    }
}
