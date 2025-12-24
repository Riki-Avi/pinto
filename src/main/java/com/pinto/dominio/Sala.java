package com.pinto.dominio;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Representa una sala de juego con dos equipos.
 * Cada equipo tiene un OBSERVADOR y un DIBUJANTE.
 */
public class Sala {

    // Constantes de rol
    public static final String ROL_OBSERVADOR = "observador";
    public static final String ROL_DIBUJANTE = "dibujante";

    // Lista de imágenes disponibles
    private static final String[] IMAGENES = {
            "gato1.png", "perro1.png", "vaca1.png"
    };

    private String id;
    private String codigo;
    private List<String> equipoRojo;
    private List<String> equipoAzul;
    private String creador;
    private boolean enPartida;

    // Imagen asignada a cada equipo para dibujar
    private String imagenEquipoRojo;
    private String imagenEquipoAzul;

    // Dibujos capturados (base64)
    private String dibujoEquipoRojo;
    private String dibujoEquipoAzul;

    // Modo de juego: "equipos" o "memoria"
    private String modo = "equipos";

    // Dibujos individuales para modo memoria (username -> base64)
    private java.util.Map<String, String> dibujosIndividuales = new java.util.HashMap<>();

    // Votos individuales para modo memoria (dibujanteVotado -> Map<votante,
    // puntaje>)
    private java.util.Map<String, java.util.Map<String, Integer>> votosIndividuales = new java.util.HashMap<>();

    // Sistema de votación
    private java.util.Map<String, Integer> votosRojo = new java.util.HashMap<>(); // usuario -> voto
    private java.util.Map<String, Integer> votosAzul = new java.util.HashMap<>();
    private int faseVotacion = 0; // 0=no empezó, 1-4=votando jugador N, 5=terminado

    public Sala(String creador) {
        this.id = UUID.randomUUID().toString();
        this.codigo = generarCodigo();
        this.equipoRojo = new ArrayList<>();
        this.equipoAzul = new ArrayList<>();
        this.creador = creador;
        this.enPartida = false;

        // Asignar la MISMA imagen aleatoria a ambos equipos
        String imagenComun = IMAGENES[(int) (Math.random() * IMAGENES.length)];
        this.imagenEquipoRojo = imagenComun;
        this.imagenEquipoAzul = imagenComun;
    }

    private String generarCodigo() {
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
        equipoRojo.remove(username);
        equipoAzul.remove(username);

        if ("rojo".equals(nuevoEquipo) && equipoRojo.size() < 2) {
            equipoRojo.add(nuevoEquipo);
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

    /**
     * Obtiene el rol del jugador dentro de su equipo.
     * El primero en unirse al equipo es OBSERVADOR.
     * El segundo es DIBUJANTE.
     */
    public String getRolDeJugador(String username) {
        // En equipo rojo
        int indexRojo = equipoRojo.indexOf(username);
        if (indexRojo == 0)
            return ROL_OBSERVADOR;
        if (indexRojo == 1)
            return ROL_DIBUJANTE;

        // En equipo azul
        int indexAzul = equipoAzul.indexOf(username);
        if (indexAzul == 0)
            return ROL_OBSERVADOR;
        if (indexAzul == 1)
            return ROL_DIBUJANTE;

        return null;
    }

    /**
     * Obtiene la imagen que debe dibujar el equipo del jugador.
     */
    public String getImagenParaJugador(String username) {
        String equipo = getEquipoDeJugador(username);
        if ("rojo".equals(equipo))
            return imagenEquipoRojo;
        if ("azul".equals(equipo))
            return imagenEquipoAzul;
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

    public String getImagenEquipoRojo() {
        return imagenEquipoRojo;
    }

    public String getImagenEquipoAzul() {
        return imagenEquipoAzul;
    }

    // Getters y Setters para dibujos
    public String getDibujoEquipoRojo() {
        return dibujoEquipoRojo;
    }

    public void setDibujoEquipoRojo(String dibujo) {
        this.dibujoEquipoRojo = dibujo;
    }

    public String getDibujoEquipoAzul() {
        return dibujoEquipoAzul;
    }

    public void setDibujoEquipoAzul(String dibujo) {
        this.dibujoEquipoAzul = dibujo;
    }

    // Sistema de votación
    public int getFaseVotacion() {
        return faseVotacion;
    }

    public void setFaseVotacion(int fase) {
        this.faseVotacion = fase;
    }

    public void votar(String username, String equipoVotado, int puntaje) {
        if ("rojo".equals(equipoVotado)) {
            votosRojo.put(username, puntaje);
        } else if ("azul".equals(equipoVotado)) {
            votosAzul.put(username, puntaje);
        }
    }

    public boolean todosVotaron(String equipoVotado) {
        java.util.Map<String, Integer> votos = "rojo".equals(equipoVotado) ? votosRojo : votosAzul;
        // Necesitamos votos de los 4 jugadores
        return votos.size() >= 4;
    }

    public int getPuntajeTotal(String equipo) {
        java.util.Map<String, Integer> votos = "rojo".equals(equipo) ? votosRojo : votosAzul;
        return votos.values().stream().mapToInt(Integer::intValue).sum();
    }

    public java.util.Map<String, Integer> getVotosRojo() {
        return votosRojo;
    }

    public java.util.Map<String, Integer> getVotosAzul() {
        return votosAzul;
    }

    public String getGanador() {
        int puntajeRojo = getPuntajeTotal("rojo");
        int puntajeAzul = getPuntajeTotal("azul");
        if (puntajeRojo > puntajeAzul)
            return "rojo";
        if (puntajeAzul > puntajeRojo)
            return "azul";
        return "empate";
    }

    // ==================
    // MODO MEMORIA
    // ==================
    public String getModo() {
        return modo;
    }

    public void setModo(String modo) {
        this.modo = modo;
    }

    public void guardarDibujoIndividual(String username, String dibujo) {
        dibujosIndividuales.put(username, dibujo);
    }

    public String getDibujoIndividual(String username) {
        return dibujosIndividuales.get(username);
    }

    public java.util.Map<String, String> getDibujosIndividuales() {
        return dibujosIndividuales;
    }

    public java.util.List<String> getTodosLosJugadores() {
        java.util.List<String> todos = new java.util.ArrayList<>();
        todos.addAll(equipoRojo);
        todos.addAll(equipoAzul);
        return todos;
    }

    public void votarIndividual(String votante, String dibujante, int puntaje) {
        votosIndividuales.computeIfAbsent(dibujante, k -> new java.util.HashMap<>());
        votosIndividuales.get(dibujante).put(votante, puntaje);
    }

    public int getPuntajeIndividual(String dibujante) {
        java.util.Map<String, Integer> votos = votosIndividuales.get(dibujante);
        if (votos == null)
            return 0;
        return votos.values().stream().mapToInt(Integer::intValue).sum();
    }

    public boolean todosVotaronIndividual(String dibujante) {
        java.util.Map<String, Integer> votos = votosIndividuales.get(dibujante);
        if (votos == null)
            return false;
        return votos.size() >= 4;
    }

    public String getGanadorMemoria() {
        int puntajeRojo = 0;
        int puntajeAzul = 0;

        for (String jugador : equipoRojo) {
            puntajeRojo += getPuntajeIndividual(jugador);
        }
        for (String jugador : equipoAzul) {
            puntajeAzul += getPuntajeIndividual(jugador);
        }

        if (puntajeRojo > puntajeAzul)
            return "rojo";
        if (puntajeAzul > puntajeRojo)
            return "azul";
        return "empate";
    }

    public int getPuntajeTotalEquipoMemoria(String equipo) {
        int total = 0;
        java.util.List<String> jugadores = "rojo".equals(equipo) ? equipoRojo : equipoAzul;
        for (String jugador : jugadores) {
            total += getPuntajeIndividual(jugador);
        }
        return total;
    }

    public java.util.Map<String, Integer> getVotosIndividualesParaDibujante(String dibujante) {
        return votosIndividuales.get(dibujante);
    }
}
