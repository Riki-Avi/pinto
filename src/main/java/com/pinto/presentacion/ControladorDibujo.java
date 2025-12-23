package com.pinto.presentacion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.pinto.dominio.MensajeTrazo;

/**
 * Controlador WebSocket para el juego de dibujo.
 * Los trazos se envían solo a los jugadores del mismo equipo.
 */
@Controller
public class ControladorDibujo {

    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    public ControladorDibujo(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Recibe un trazo y lo reenvía solo a los del mismo equipo.
     * 
     * Cliente envía a: /app/dibujar/{salaId}/{equipo}
     * Servidor reenvía a: /topic/trazos/{salaId}/{equipo}
     */
    @MessageMapping("/dibujar/{salaId}/{equipo}")
    public void procesarTrazo(
            @DestinationVariable String salaId,
            @DestinationVariable String equipo,
            MensajeTrazo trazo) {

        // Enviar solo a los suscriptores del mismo equipo en la misma sala
        String destino = "/topic/trazos/" + salaId + "/" + equipo;
        messagingTemplate.convertAndSend(destino, trazo);
    }

    /**
     * Cuando un jugador borra el canvas, notifica solo a su equipo.
     */
    @MessageMapping("/borrar/{salaId}/{equipo}")
    public void borrarCanvas(
            @DestinationVariable String salaId,
            @DestinationVariable String equipo,
            MensajeTrazo mensaje) {

        mensaje.setTipo("borrar");
        String destino = "/topic/trazos/" + salaId + "/" + equipo;
        messagingTemplate.convertAndSend(destino, mensaje);
    }
}
