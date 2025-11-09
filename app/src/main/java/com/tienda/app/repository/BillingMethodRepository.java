package com.tienda.app.repository;

import com.tienda.app.model.BillingMethod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para la entidad {@link BillingMethod}.
 *
 * Extiende de {@link JpaRepository}, lo que provee automáticamente:
 * - Operaciones CRUD (save, findById, findAll, delete, etc.).
 * - Soporte para paginación y ordenamiento.
 *
 * Métodos personalizados:
 * - {@link #findByUser_Id(Integer)} → busca un método de pago por id de usuario (uno solo).
 * - {@link #findAllByUser_Id(Integer)} → lista todos los métodos de pago de un usuario.
 * - {@link #existsByUserIdAndCardNumber(Integer, String)} → verifica si un usuario ya tiene registrada una tarjeta.
 *
 * Tipo genérico:
 * - Entidad: BillingMethod
 * - Clave primaria: Integer
 */
public interface BillingMethodRepository extends JpaRepository<BillingMethod, Integer> {

    /**
     * Busca un método de pago asociado a un usuario específico.
     *
     * @param id id del usuario.
     * @return Optional con el {@link BillingMethod}, vacío si no existe.
     */
    Optional<BillingMethod> findByUser_Id(Integer id);

    /**
     * Obtiene todos los métodos de pago de un usuario.
     *
     * @param id id del usuario.
     * @return lista de {@link BillingMethod}.
     */
    List<BillingMethod> findAllByUser_Id(Integer id);

    /**
     * Verifica si un usuario ya tiene registrada una tarjeta con ese número.
     *
     * @param userId id del usuario.
     * @param cardNumber número de tarjeta.
     * @return true si existe, false si no.
     */
    boolean existsByUserIdAndCardNumber(Integer userId, String cardNumber);

    void deleteByUser_Id(Integer userId);
}
