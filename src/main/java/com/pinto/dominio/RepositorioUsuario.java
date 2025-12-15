package com.pinto.dominio;

public interface RepositorioUsuario {
    Usuario crearUsuario(Usuario usuario);

    Usuario buscarUsuarioPorUsername(String username);
}
