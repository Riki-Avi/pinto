package com.pinto.dominio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ServicioUsuarioImpl implements ServicioUsuario {

    private RepositorioUsuario repositorioUsuario;

    @Autowired
    public ServicioUsuarioImpl(RepositorioUsuario repositorioUsuario) {
        this.repositorioUsuario = repositorioUsuario;
    }

    @Override
    public Usuario crearUsuario(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario no puede ser nulo");
        }

        if (repositorioUsuario.buscarUsuarioPorUsername(usuario.getUsername()) != null) {
            throw new IllegalArgumentException("Username ya existe");
        }

        return repositorioUsuario.crearUsuario(usuario);
    }

    @Override
    public Usuario buscarUsuarioPorUsername(String username) {
        return repositorioUsuario.buscarUsuarioPorUsername(username);
    }
}
