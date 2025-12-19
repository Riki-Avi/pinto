package com.pinto.infraestructura;

import com.pinto.dominio.RepositorioUsuario;
import com.pinto.dominio.Usuario;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class RepositorioUsuarioImpl implements RepositorioUsuario {
    private SessionFactory sessionFactory;

    @Autowired
    public RepositorioUsuarioImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    @Transactional
    public Usuario crearUsuario(Usuario usuario) {
        sessionFactory.getCurrentSession().save(usuario);
        return usuario;
    }

    @SuppressWarnings("deprecation")
    @Override
    @Transactional
    public Usuario buscarUsuarioPorUsername(String username) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(Usuario.class);
        criteria.add(Restrictions.eq("username", username));
        return (Usuario) criteria.uniqueResult();
    }
}
