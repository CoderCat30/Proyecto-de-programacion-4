package com.tienda.app.repository;

import com.tienda.app.model.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio JPA para la entidad UserAddress (direcciones de usuario).
 *
 * - Extiende JpaRepository<UserAddress, Integer>, lo que permite:
 *    - CRUD completo sin necesidad de implementar nada:
 *        - save(UserAddress entity) → guardar/actualizar direcciones.
 *        - findById(Integer id) → buscar dirección por id.
 *        - findAll() → listar todas las direcciones.
 *        - deleteById(Integer id) → eliminar una dirección.
 *
 * - El segundo parámetro <Integer> indica el tipo de dato de la clave primaria (id).
 *
 * - Por ahora no define métodos personalizados, pero podrías agregarlos
 *   fácilmente usando la convención de nombres de Spring Data JPA.
 *
 * Ejemplo de extensión:
 *   List<UserAddress> findAllByUser_Id(Integer userId);
 */
public interface UserAddressRepository extends JpaRepository<UserAddress,Integer> {
    // Aquí puedes añadir métodos de consulta específicos si lo requieres
}
