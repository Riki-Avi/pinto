package com.pinto.presentacion;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import com.pinto.dominio.Sala;
import com.pinto.dominio.Usuario;
import com.pinto.infraestructura.GestorSalas;

/**
 * Controlador HTTP para el lobby y las salas.
 */
@Controller
public class ControladorLobby {

    private GestorSalas gestorSalas;

    @Autowired
    public ControladorLobby(GestorSalas gestorSalas) {
        this.gestorSalas = gestorSalas;
    }

    /**
     * Muestra el lobby con la lista de salas disponibles.
     */
    @GetMapping("/lobby")
    public ModelAndView lobby(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return new ModelAndView("redirect:/");
        }

        List<Sala> salasDisponibles = gestorSalas.obtenerSalasDisponibles();

        ModelAndView mav = new ModelAndView("lobby");
        mav.addObject("username", usuario.getUsername());
        mav.addObject("salas", salasDisponibles);
        return mav;
    }

    /**
     * Crea una nueva sala y redirige al creador.
     */
    @PostMapping("/crear-sala")
    public ModelAndView crearSala(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return new ModelAndView("redirect:/");
        }

        Sala sala = gestorSalas.crearSala(usuario.getUsername());
        session.setAttribute("salaActual", sala.getCodigo());

        return new ModelAndView("redirect:/sala/" + sala.getCodigo());
    }

    /**
     * Muestra la vista de una sala específica.
     */
    @GetMapping("/sala/{codigo}")
    public ModelAndView sala(@PathVariable String codigo, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return new ModelAndView("redirect:/");
        }

        Sala sala = gestorSalas.obtenerSala(codigo);
        if (sala == null) {
            return new ModelAndView("redirect:/lobby");
        }

        // Guardar sala actual en sesión
        session.setAttribute("salaActual", codigo);

        ModelAndView mav = new ModelAndView("sala");
        mav.addObject("username", usuario.getUsername());
        mav.addObject("codigo", codigo);
        mav.addObject("sala", sala);
        return mav;
    }
}
