package com.tienda.app.service;

import com.tienda.app.model.Articulo;
import com.tienda.app.repository.ArticuloRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ArticuloService {

    private final ArticuloRepository articuloRepository;

    public ArticuloService(ArticuloRepository articuloRepository) {
        this.articuloRepository = articuloRepository;
    }

    public List<Articulo> listarTodos() {
        return articuloRepository.findAll();
    }

    public Optional<Articulo> buscarPorId(Integer id) {
        return articuloRepository.findById(id);
    }
}
