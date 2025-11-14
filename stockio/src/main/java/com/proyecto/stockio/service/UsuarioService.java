package com.proyecto.stockio.service;

import com.proyecto.stockio.model.Usuario;
import com.proyecto.stockio.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    @SuppressWarnings("null")
    public void guardarUsuario(Usuario usuario) {
        usuarioRepository.save(usuario);
    }

    @SuppressWarnings("null")
    public void eliminarUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }

    @SuppressWarnings("null")
    public Usuario obtenerPorId(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }
}