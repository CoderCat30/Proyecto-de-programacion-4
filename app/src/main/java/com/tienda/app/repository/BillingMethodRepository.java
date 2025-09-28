package com.tienda.app.repository;

import com.tienda.app.model.BillingMethod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para la entidad BillingMethod (métodos de pago de usuario).
 *
 * - Extiende JpaRepository<BillingMethod, Integer>, lo que proporciona
 *   automáticamente todas las operaciones CRUD básicas:
 *     - findAll(), findById(), save(), deleteById(), etc.
 *
 * - Además, define métodos personalizados basados en la convención
 *   de nombres de Spring Data JPA para trabajar con el usuario asociado.
 */
public interface BillingMethodRepository extends JpaRepository<BillingMethod,Integer> {

    /**
     * Busca un método de pago asociado a un usuario por su ID.
     * - Retorna un Optional<BillingMethod>, ya que puede o no existir.
     * - Usa la relación con el campo "user" de BillingMethod.
     *
     * Ejemplo de uso:
     *   billingMethodRepository.findByUser_Id(5);
     */
    Optional<BillingMethod> findByUser_Id(Integer id);

    /**
     * Busca todos los métodos de pago asociados a un usuario por su ID.
     * - Retorna una lista de BillingMethod.
     * - Útil cuando un usuario puede tener varias tarjetas registradas.
     *
     * Ejemplo de uso:
     *   billingMethodRepository.findAllByUser_Id(5);
     */
    List<BillingMethod> findAllByUser_Id(Integer id);
}
