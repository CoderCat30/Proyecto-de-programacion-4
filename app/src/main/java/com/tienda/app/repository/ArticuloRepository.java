package com.tienda.app.repository;

import com.tienda.app.model.Articulo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio JPA para la entidad Articulo.
 *
 * - Extiende JpaRepository, lo que le da acceso inmediato
 *   a métodos CRUD (create, read, update, delete) y consultas básicas,
 *   sin necesidad de implementarlas manualmente.
 *
 * - JpaRepository<Articulo, Integer>:
 *      - Articulo → la entidad que maneja este repositorio.
 *      - Integer  → el tipo de dato de la clave primaria (id).
 *
 * - Gracias a Spring Data JPA, se generan automáticamente métodos como:
 *      - findAll() → devuelve todos los artículos.
 *      - findById(Integer id) → busca un artículo por id.
 *      - save(Articulo entity) → inserta o actualiza un artículo.
 *      - deleteById(Integer id) → elimina un artículo por id.
 *
 * También se pueden definir consultas personalizadas si necesitas lógica extra.
 */
@Repository
public interface ArticuloRepository extends JpaRepository<Articulo, Integer> {
    // Ejemplo de consulta personalizada si se necesitara:
    // List<Articulo> findByIsActiveTrue();
}
