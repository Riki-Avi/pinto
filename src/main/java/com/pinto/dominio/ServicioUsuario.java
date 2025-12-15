package com.pinto.dominio;

public interface ServicioUsuario {
    Usuario crearUsuario(Usuario usuario);

    Usuario buscarUsuarioPorUsername(String username);
}