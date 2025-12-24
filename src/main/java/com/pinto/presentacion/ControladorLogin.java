package com.pinto.presentacion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.pinto.dominio.Sala;
import com.pinto.dominio.Usuario;
import com.pinto.dominio.ServicioUsuario;
import com.pinto.infraestructura.GestorSalas;

import javax.servlet.http.HttpSession;

@Controller
public class ControladorLogin {

    private ServicioUsuario servicioUsuario;
    private GestorSalas gestorSalas;

    @Autowired
    public ControladorLogin(ServicioUsuario servicioUsuario, GestorSalas gestorSalas) {
        this.servicioUsuario = servicioUsuario;
        this.gestorSalas = gestorSalas;
    }

    @GetMapping("/registro")
    public ModelAndView registro() {
        return new ModelAndView("registro");
    }

    @PostMapping("/registro")
    public ModelAndView registro(
            @RequestParam(required = true) String username,
            @RequestParam(required = true) String password) {
        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setPassword(password);
        servicioUsuario.crearUsuario(usuario);
        return new ModelAndView("redirect:/");
    }

    @GetMapping("/")
    public ModelAndView login() {
        return new ModelAndView("login");
    }

    @PostMapping("/login")
    public ModelAndView login(
            @RequestParam(required = true) String username,
            @RequestParam(required = true) String password,
            HttpSession session) {

        Usuario usuarioEncontrado = servicioUsuario.buscarUsuarioPorUsername(username);

        if (usuarioEncontrado == null) {
            ModelAndView mav = new ModelAndView("login");
            mav.addObject("error", "Usuario no encontrado");
            return mav;
        }

        if (!usuarioEncontrado.getPassword().equals(password)) {
            ModelAndView mav = new ModelAndView("login");
            mav.addObject("error", "Contraseña incorrecta");
            return mav;
        }

        // Guardar usuario en sesión
        session.setAttribute("usuario", usuarioEncontrado);

        return new ModelAndView("redirect:/lobby");
    }

    @GetMapping("/home")
    public ModelAndView home(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (usuario == null) {
            return new ModelAndView("redirect:/");
        }

        // Obtener sala y equipo de la sesión
        String salaId = (String) session.getAttribute("salaActual");
        if (salaId == null) {
            return new ModelAndView("redirect:/lobby");
        }

        Sala sala = gestorSalas.obtenerSala(salaId);
        if (sala == null) {
            return new ModelAndView("redirect:/lobby");
        }

        String equipo = sala.getEquipoDeJugador(usuario.getUsername());
        if (equipo == null) {
            return new ModelAndView("redirect:/lobby");
        }

        // Obtener rol e imagen del jugador
        String rol = sala.getRolDeJugador(usuario.getUsername());
        String imagen = sala.getImagenParaJugador(usuario.getUsername());

        ModelAndView mav = new ModelAndView("home");
        mav.addObject("username", usuario.getUsername());
        mav.addObject("salaId", salaId);
        mav.addObject("equipo", equipo);
        mav.addObject("rol", rol); // "observador" o "dibujante"
        mav.addObject("imagen", imagen); // nombre del archivo de imagen
        return mav;
    }

    /**
     * Pantalla de juego modo memoria.
     */
    @GetMapping("/home-memoria")
    public ModelAndView homeMemoria(HttpSession session) {
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

        String equipo = sala.getEquipoDeJugador(usuario.getUsername());
        String imagen = sala.getImagenParaJugador(usuario.getUsername());

        ModelAndView mav = new ModelAndView("home_memoria");
        mav.addObject("username", usuario.getUsername());
        mav.addObject("salaId", salaId);
        mav.addObject("equipo", equipo);
        mav.addObject("imagen", imagen);
        return mav;
    }
}
