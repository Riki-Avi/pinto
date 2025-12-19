package com.pinto.presentacion;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.pinto.dominio.MensajeTrazo;

/**
 * Controlador WebSocket para el juego de dibujo.
 * Recibe trazos de los jugadores y los reenvía a todos.
 */
@Controller
public class ControladorDibujo {

    /**
     * Recibe un trazo de un jugador y lo reenvía a todos los suscriptores.
     * 
     * Cliente envía a: /app/dibujar
     * Servidor reenvía a: /topic/trazos
     */
    @MessageMapping("/dibujar")
    @SendTo("/topic/trazos")
    public MensajeTrazo procesarTrazo(MensajeTrazo trazo) {
        // Simplemente reenviamos el trazo a todos los clientes
        // En el futuro podríamos agregar validaciones o lógica de salas
        return trazo;
    }

    /**
     * Cuando un jugador borra el canvas, notifica a todos.
     */
    @MessageMapping("/borrar")
    @SendTo("/topic/trazos")
    public MensajeTrazo borrarCanvas(MensajeTrazo mensaje) {
        mensaje.setTipo("borrar");
        return mensaje;
    }
}
