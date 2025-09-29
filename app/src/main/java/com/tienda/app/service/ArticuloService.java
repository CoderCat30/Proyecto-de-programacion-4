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

    public void actualizarStock(Integer productoId, int cantidadVendida) {
        Articulo producto = articuloRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        int nuevoStock = producto.getStockQuantity() - cantidadVendida;

        if (nuevoStock < 0) {
            throw new RuntimeException("Stock insuficiente para el producto: " + producto.getName());
        }

        producto.setStockQuantity(nuevoStock);
        articuloRepository.save(producto);
    }

    public boolean verificarStockDisponible(Integer productoId, int cantidadSolicitada) {
        return articuloRepository.findById(productoId)
                .map(producto -> producto.getStockQuantity() >= cantidadSolicitada)
                .orElse(false);
    }
}
