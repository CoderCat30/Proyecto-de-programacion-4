package com.tienda.app.repository;

import com.tienda.app.model.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositorio JPA para la entidad {@link UserAddress}.
 *
 * Extiende de {@link JpaRepository}, lo que provee automáticamente:
 * - Operaciones CRUD (save, findById, findAll, delete, etc.).
 *
 * Métodos personalizados:
 * - {@link #findByUser_Id(Integer)} → obtiene la dirección asociada a un usuario por su id.
 *
 * Tipo genérico:
 * - Entidad: UserAddress
 * - Clave primaria: Integer
 */
public interface UserAddressRepository extends JpaRepository<UserAddress, Integer> {

    /**
     * Busca la dirección asociada a un usuario por su id.
     *
     * @param id id del usuario.
     * @return Optional con la dirección del usuario, vacío si no existe.
     */
    Optional<UserAddress> findByUser_Id(Integer id);
}
