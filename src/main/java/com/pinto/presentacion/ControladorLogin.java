package com.pinto.presentacion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.pinto.dominio.Usuario;
import com.pinto.dominio.ServicioUsuario;

import javax.servlet.http.HttpSession;

@Controller
public class ControladorLogin {

    private ServicioUsuario servicioUsuario;

    @Autowired
    public ControladorLogin(ServicioUsuario servicioUsuario) {
        this.servicioUsuario = servicioUsuario;
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

        return new ModelAndView("redirect:/home");
    }

    @GetMapping("/home")
    public ModelAndView home(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (usuario == null) {
            // Si no hay usuario en sesión, redirigir al login
            return new ModelAndView("redirect:/");
        }

        ModelAndView mav = new ModelAndView("home");
        mav.addObject("username", usuario.getUsername());
        return mav;
    }
}
