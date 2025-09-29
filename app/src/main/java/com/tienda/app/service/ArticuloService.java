package com.tienda.app.service;

import com.tienda.app.model.Articulo;
import com.tienda.app.repository.ArticuloRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Servicio que gestiona la lógica de negocio relacionada con los artículos/productos.
 *
 * Provee métodos para:
 * - Listar todos los artículos.
 * - Buscar un artículo por su ID.
 * - Actualizar el stock después de una compra.
 * - Verificar si hay suficiente stock disponible para un pedido.
 */
@Service
public class ArticuloService {

    private final ArticuloRepository articuloRepository;

    /**
     * Inyección de dependencias mediante constructor.
     * @param articuloRepository repositorio JPA de artículos.
     */
    public ArticuloService(ArticuloRepository articuloRepository) {
        this.articuloRepository = articuloRepository;
    }

    /**
     * Lista todos los artículos almacenados en la base de datos.
     *
     * @return lista de {@link Articulo}.
     */
    public List<Articulo> listarTodos() {
        return articuloRepository.findAll();
    }

    /**
     * Busca un artículo por su identificador.
     *
     * @param id id del artículo.
     * @return Optional con el artículo si existe, vacío si no.
     */
    public Optional<Articulo> buscarPorId(Integer id) {
        return articuloRepository.findById(id);
    }

    /**
     * Actualiza el stock de un producto después de una venta.
     *
     * @param productoId id del producto vendido.
     * @param cantidadVendida cantidad a descontar del stock.
     * @throws RuntimeException si el producto no existe o si no hay stock suficiente.
     */
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

    /**
     * Verifica si un producto tiene suficiente stock para atender una solicitud.
     *
     * @param productoId id del producto.
     * @param cantidadSolicitada cantidad que se desea comprar.
     * @return true si hay stock suficiente, false en caso contrario.
     */
    public boolean verificarStockDisponible(Integer productoId, int cantidadSolicitada) {
        return articuloRepository.findById(productoId)
                .map(producto -> producto.getStockQuantity() >= cantidadSolicitada)
                .orElse(false);
    }
}
