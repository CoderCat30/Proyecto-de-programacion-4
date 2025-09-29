package com.tienda.app.repository;

import com.tienda.app.model.Articulo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio JPA para la entidad {@link Articulo}.
 *
 * Extiende de {@link JpaRepository}, lo que provee automáticamente:
 * - Métodos CRUD (save, findById, findAll, delete, etc.).
 * - Soporte para paginación y ordenamiento.
 *
 * Al estar anotado con {@link Repository}, Spring lo detecta como un
 * componente de persistencia y permite inyección en servicios.
 *
 * Tipo genérico:
 * - Entidad: Articulo
 * - Clave primaria: Integer
 */
@Repository
public interface ArticuloRepository extends JpaRepository<Articulo, Integer> {
    // Aquí se pueden definir consultas personalizadas si se necesitan,
    // por ejemplo: List<Articulo> findByIsActiveTrue();
}
