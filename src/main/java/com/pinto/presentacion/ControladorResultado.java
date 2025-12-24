package com.pinto.presentacion;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.pinto.dominio.MensajeVoto;
import com.pinto.dominio.Sala;
import com.pinto.dominio.Usuario;
import com.pinto.infraestructura.GestorSalas;

/**
 * Controlador para la pantalla de resultados y votación.
 */
@Controller
public class ControladorResultado {

    private GestorSalas gestorSalas;
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    public ControladorResultado(GestorSalas gestorSalas, SimpMessagingTemplate messagingTemplate) {
        this.gestorSalas = gestorSalas;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Endpoint para guardar el dibujo cuando termina el timer.
     */
    @PostMapping("/guardar-dibujo")
    @ResponseBody
    public String guardarDibujo(@RequestBody java.util.Map<String, String> datos, HttpSession session) {
        String salaId = datos.get("salaId");
        String equipo = datos.get("equipo");
        String dibujo = datos.get("dibujo");

        Sala sala = gestorSalas.obtenerSala(salaId);
        if (sala == null) {
            return "{\"error\": \"Sala no encontrada\"}";
        }

        // Guardar el dibujo según el equipo
        if ("rojo".equals(equipo)) {
            sala.setDibujoEquipoRojo(dibujo);
        } else if ("azul".equals(equipo)) {
            sala.setDibujoEquipoAzul(dibujo);
        }

        // Si ambos equipos guardaron sus dibujos, iniciar votación
        if (sala.getDibujoEquipoRojo() != null && sala.getDibujoEquipoAzul() != null) {
            sala.setFaseVotacion(1); // Empezar votando al equipo rojo
        }

        return "{\"ok\": true}";
    }

    /**
     * Muestra la pantalla de resultados/votación.
     */
    @GetMapping("/resultado")
    public ModelAndView resultado(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return new ModelAndView("redirect:/");
        }

        String salaId = (String) session.getAttribute("salaActual");
        if (salaId == null) {
            return new ModelAndView("redirect:/lobby");
        }

        Sala sala = gestorSalas.obtenerSala(salaId);
        if (sala == null) {
            return new ModelAndView("redirect:/lobby");
        }

        ModelAndView mav = new ModelAndView("resultado");
        mav.addObject("username", usuario.getUsername());
        mav.addObject("salaId", salaId);
        mav.addObject("faseVotacion", sala.getFaseVotacion());
        mav.addObject("dibujoRojo", sala.getDibujoEquipoRojo());
        mav.addObject("dibujoAzul", sala.getDibujoEquipoAzul());
        mav.addObject("imagenOriginal", sala.getImagenEquipoRojo()); // La misma para ambos
        return mav;
    }

    /**
     * WebSocket: Recibe un voto de un jugador.
     */
    @MessageMapping("/votar/{salaId}")
    public void votar(@DestinationVariable String salaId, MensajeVoto mensaje) {
        Sala sala = gestorSalas.obtenerSala(salaId);
        if (sala == null)
            return;

        // Registrar voto
        sala.votar(mensaje.getUsername(), mensaje.getEquipoVotado(), mensaje.getPuntaje());

        // Crear mensaje de actualización
        MensajeVoto respuesta = new MensajeVoto();
        respuesta.setTipo("voto_recibido");
        respuesta.setSalaId(salaId);
        respuesta.setFaseActual(sala.getFaseVotacion());

        String equipoActual = sala.getFaseVotacion() == 1 ? "rojo" : "azul";
        java.util.Map<String, Integer> votos = sala.getFaseVotacion() == 1 ? sala.getVotosRojo() : sala.getVotosAzul();
        respuesta.setVotosRecibidos(votos.size());

        // Si todos votaron esta fase
        if (sala.todosVotaron(equipoActual)) {
            if (sala.getFaseVotacion() == 1) {
                // Pasar a votar equipo azul
                sala.setFaseVotacion(2);
                respuesta.setTipo("siguiente_fase");
                respuesta.setFaseActual(2);
                respuesta.setDibujoActual(sala.getDibujoEquipoAzul());
            } else {
                // Mostrar resultados finales
                sala.setFaseVotacion(3);
                respuesta.setTipo("resultado_final");
                respuesta.setFaseActual(3);
                respuesta.setPuntajeRojo(sala.getPuntajeTotal("rojo"));
                respuesta.setPuntajeAzul(sala.getPuntajeTotal("azul"));
                respuesta.setGanador(sala.getGanador());
            }
        }

        // Enviar a todos los jugadores de la sala
        messagingTemplate.convertAndSend("/topic/votacion/" + salaId, respuesta);
    }

    // ==================
    // MODO MEMORIA
    // ==================

    /**
     * Guardar dibujo individual (modo memoria).
     */
    @PostMapping("/guardar-dibujo-memoria")
    @ResponseBody
    public String guardarDibujoMemoria(@RequestBody java.util.Map<String, String> datos) {
        String salaId = datos.get("salaId");
        String username = datos.get("username");
        String dibujo = datos.get("dibujo");

        Sala sala = gestorSalas.obtenerSala(salaId);
        if (sala == null) {
            return "{\"error\": \"Sala no encontrada\"}";
        }

        sala.guardarDibujoIndividual(username, dibujo);

        // Si todos guardaron, iniciar votación
        if (sala.getDibujosIndividuales().size() >= 4) {
            sala.setFaseVotacion(1);
        }

        return "{\"ok\": true, \"total\": " + sala.getDibujosIndividuales().size() + "}";
    }

    /**
     * Pantalla de resultados modo memoria.
     */
    @GetMapping("/resultado-memoria")
    public ModelAndView resultadoMemoria(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return new ModelAndView("redirect:/");
        }

        String salaId = (String) session.getAttribute("salaActual");
        if (salaId == null) {
            return new ModelAndView("redirect:/lobby");
        }

        Sala sala = gestorSalas.obtenerSala(salaId);
        if (sala == null) {
            return new ModelAndView("redirect:/lobby");
        }

        java.util.List<String> jugadores = sala.getTodosLosJugadores();
        java.util.Map<String, String> dibujos = sala.getDibujosIndividuales();

        ModelAndView mav = new ModelAndView("resultado_memoria");
        mav.addObject("username", usuario.getUsername());
        mav.addObject("salaId", salaId);
        mav.addObject("jugadores", jugadores);
        mav.addObject("dibujos", dibujos);
        mav.addObject("equipoRojo", sala.getEquipoRojo());
        mav.addObject("equipoAzul", sala.getEquipoAzul());
        mav.addObject("imagenOriginal", sala.getImagenEquipoRojo());
        return mav;
    }

    /**
     * API para obtener dibujos actualizados (polling).
     */
    @GetMapping("/api/dibujos/{salaId}")
    @ResponseBody
    public java.util.Map<String, Object> obtenerDibujos(
            @org.springframework.web.bind.annotation.PathVariable("salaId") String salaId) {
        java.util.Map<String, Object> resultado = new java.util.HashMap<>();

        Sala sala = gestorSalas.obtenerSala(salaId);
        if (sala == null) {
            resultado.put("error", "Sala no encontrada");
            return resultado;
        }

        resultado.put("dibujos", sala.getDibujosIndividuales());
        resultado.put("total", sala.getDibujosIndividuales().size());
        resultado.put("jugadores", sala.getTodosLosJugadores());
        return resultado;
    }

    /**
     * WebSocket: Voto individual (modo memoria).
     */
    @MessageMapping("/votar-memoria/{salaId}")
    public void votarMemoria(@DestinationVariable String salaId, MensajeVoto mensaje) {
        Sala sala = gestorSalas.obtenerSala(salaId);
        if (sala == null)
            return;

        String votante = mensaje.getUsername();
        String dibujante = mensaje.getEquipoVotado(); // nombre del jugador votado
        int puntaje = mensaje.getPuntaje();

        System.out.println("Voto recibido: " + votante + " -> " + dibujante + " = " + puntaje);

        sala.votarIndividual(votante, dibujante, puntaje);

        MensajeVoto respuesta = new MensajeVoto();
        respuesta.setSalaId(salaId);

        // Contar votos para este dibujante específico
        java.util.List<String> jugadores = sala.getTodosLosJugadores();
        int votosParaDibujante = 0;
        for (String j : jugadores) {
            // Verificar si jugador j votó a este dibujante
            java.util.Map<String, Integer> votosDibujante = sala.getVotosIndividualesParaDibujante(dibujante);
            if (votosDibujante != null && votosDibujante.containsKey(j)) {
                votosParaDibujante++;
            }
        }

        System.out.println("Votos para " + dibujante + ": " + votosParaDibujante + "/4");

        // Encontrar índice actual del dibujante
        int indiceDibujante = jugadores.indexOf(dibujante);

        // Si todos votaron a este dibujante, pasar al siguiente
        if (votosParaDibujante >= 4) {
            int siguienteIndice = indiceDibujante + 1;

            if (siguienteIndice >= jugadores.size()) {
                // Todos los jugadores fueron votados - mostrar resultado final
                respuesta.setTipo("resultado_memoria_final");
                respuesta.setPuntajeRojo(sala.getPuntajeTotalEquipoMemoria("rojo"));
                respuesta.setPuntajeAzul(sala.getPuntajeTotalEquipoMemoria("azul"));
                respuesta.setGanador(sala.getGanadorMemoria());
                System.out.println("Resultado final - Rojo: " + respuesta.getPuntajeRojo() + " Azul: "
                        + respuesta.getPuntajeAzul());
            } else {
                // Pasar al siguiente jugador
                respuesta.setTipo("siguiente_fase_memoria");
                respuesta.setSiguienteFase(siguienteIndice);
                System.out.println("Pasando a siguiente: " + jugadores.get(siguienteIndice));
            }
        } else {
            // Todavía faltan votos
            respuesta.setTipo("voto_memoria_recibido");
            respuesta.setVotosRecibidos(votosParaDibujante);
        }

        messagingTemplate.convertAndSend("/topic/votacion/" + salaId, respuesta);
    }
}
