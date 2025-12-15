package com.pinto.presentacion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.pinto.dominio.Usuario;
import com.pinto.dominio.ServicioUsuario;

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
            @RequestParam(required = true) String password) {

        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setPassword(password);

        Usuario usuarioName = servicioUsuario.buscarUsuarioPorUsername(username);

        if (usuarioName == null) {
            ModelAndView mav = new ModelAndView("login");
            mav.addObject("error", "Usuario no encontrado");
            return mav;
        }

        if (!usuarioName.getPassword().equals(password)) {
            ModelAndView mav = new ModelAndView("login");
            mav.addObject("error", "Contraseña incorrecta");
            return mav;
        }

        return new ModelAndView("redirect:/home");
    }

    @GetMapping("/home")
    public ModelAndView home() {
        return new ModelAndView("home");
    }
}
