package com.tienda.app.repository;

import com.tienda.app.model.Bank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositorio JPA para la entidad {@link Bank}.
 *
 * Extiende de {@link JpaRepository}, lo que provee automáticamente:
 * - Operaciones CRUD (save, findById, findAll, delete, etc.).
 * - Soporte para paginación y ordenamiento.
 *
 * Métodos personalizados:
 * - {@link #findByCardNumber(String)} → busca un registro por número de tarjeta.
 * - {@link #existsByCardNumberAndBrandAndExpMonthAndExpYearAndNameOnCard(String, String, Integer, Integer, String)}
 *   → verifica si existe una tarjeta con los datos completos (para evitar duplicados).
 *
 * Tipo genérico:
 * - Entidad: Bank
 * - Clave primaria: Integer
 */
public interface BankRepository extends JpaRepository<Bank, Integer> {

    /**
     * Busca un banco/tarjeta por su número de tarjeta.
     *
     * @param cardNumber número de tarjeta a buscar.
     * @return un Optional con el objeto {@link Bank} si existe, vacío si no.
     */
    Optional<Bank> findByCardNumber(String cardNumber);

    /**
     * Verifica si ya existe una tarjeta registrada con todos los datos especificados.
     *
     * @param cardNumber número de tarjeta
     * @param brand marca (Visa, Mastercard, etc.)
     * @param expMonth mes de expiración
     * @param expYear año de expiración
     * @param nameOnCard nombre impreso en la tarjeta
     * @return true si existe un registro con estos datos, false si no
     */
    boolean existsByCardNumberAndBrandAndExpMonthAndExpYearAndNameOnCard(
            String cardNumber,
            String brand,
            Integer expMonth,
            Integer expYear,
            String nameOnCard
    );
}
