package com.tienda.app.service;

import com.tienda.app.model.BillingMethod;
import com.tienda.app.repository.BankRepository;
import com.tienda.app.repository.BillingMethodRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio que gestiona la lógica de negocio relacionada con los métodos de pago
 * (tarjetas de crédito/débito) registrados por los usuarios.
 *
 * Permite:
 * - Listar, crear y eliminar métodos de pago.
 * - Validar la existencia de la tarjeta en la tabla {@link com.tienda.app.model.Bank}.
 * - Verificar duplicados en las tarjetas de un usuario.
 * - Enmascarar información sensible de las tarjetas.
 */
@Service
public class BillingMethodService {

    private final BillingMethodRepository billingMethodRepository;
    private final BankRepository bankRepository;

    /**
     * Inyección de dependencias mediante constructor.
     */
    public BillingMethodService(BillingMethodRepository billingMethodRepository, BankRepository bankRepository) {
        this.billingMethodRepository = billingMethodRepository;
        this.bankRepository = bankRepository;
    }

    /**
     * Obtiene todos los métodos de pago de un usuario.
     *
     * Además, aplica {@link BillingMethod#clearFields()} para enmascarar datos sensibles.
     *
     * @param id id del usuario.
     * @return lista de {@link BillingMethod} con datos enmascarados.
     */
    public List<BillingMethod> findAllByUserId(Integer id) {
        List<BillingMethod> l1 = billingMethodRepository.findAllByUser_Id(id);
        l1.forEach(BillingMethod::clearFields); // Enmascarar tarjetas antes de mostrarlas
        return l1;
    }

    /**
     * Busca un método de pago por su id.
     *
     * @param id id del método de pago.
     * @return el objeto {@link BillingMethod}, o null si no existe.
     */
    public BillingMethod findById(Integer id) {
        return billingMethodRepository.findById(id).orElse(null);
    }

    /**
     * Elimina un método de pago por su id.
     *
     * @param id id del método de pago.
     */
    public void deleteById(Integer id) {
        billingMethodRepository.deleteById(id);
    }

    /**
     * Enmascara el número de tarjeta mostrando solo los 2 primeros
     * y los 4 últimos dígitos.
     *
     * Ejemplo: 411111111111 → 41******1111
     *
     * @param cardNumber número de tarjeta original.
     * @return número enmascarado.
     */
    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 6) {
            return cardNumber; // Si es inválido, se devuelve como está
        }
        String first2 = cardNumber.substring(0, 2);
        String last4 = cardNumber.substring(cardNumber.length() - 4);
        String middle = "*".repeat(cardNumber.length() - 6);
        return first2 + middle + last4;
    }

    /**
     * Crea un nuevo método de pago para un usuario.
     *
     * @param billingMethod objeto {@link BillingMethod} a guardar.
     */
    public void create(BillingMethod billingMethod) {
        billingMethodRepository.save(billingMethod);
    }

    /**
     * Verifica si una tarjeta existe en la tabla {@link com.tienda.app.model.Bank}.
     *
     * Esto asegura que el método de pago ingresado es válido.
     *
     * @return true si la tarjeta existe en el banco, false si no.
     */
    /*public boolean validarTarjetaEnBanco(String cardNumber, String brand,
                                         Integer expMonth, Integer expYear,
                                         String nameOnCard) {
        return bankRepository.existsByCardNumberAndBrandAndExpMonthAndExpYearAndNameOnCard(
                cardNumber, brand, expMonth, expYear, nameOnCard
        );
    }*/

    /**
     * Verifica si un usuario ya tiene registrada una tarjeta específica.
     *
     * @param userId id del usuario.
     * @param cardNumber número de tarjeta.
     * @return true si ya la tiene registrada, false si no.
     */
    public boolean usuarioTieneTarjeta(Integer userId, String cardNumber) {
        return billingMethodRepository.existsByUserIdAndCardNumber(userId, cardNumber);
    }
}
