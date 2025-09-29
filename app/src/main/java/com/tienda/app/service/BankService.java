package com.tienda.app.service;

import com.tienda.app.model.Bank;
import com.tienda.app.model.BillingMethod;
import com.tienda.app.repository.BankRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Servicio que gestiona la lógica de negocio relacionada con el banco simulado.
 *
 * Se encarga de validar y ejecutar transacciones con base en los datos de la tarjeta
 * del usuario y la cuenta registrada en la tabla {@link Bank}.
 */
@Service
public class BankService {

    private final BankRepository bankRepository;

    /**
     * Inyección de dependencias mediante constructor.
     * @param bankRepository repositorio JPA de cuentas bancarias/tarjetas.
     */
    public BankService(BankRepository bankRepository) {
        this.bankRepository = bankRepository;
    }

    /**
     * Ejecuta una transacción simulada entre un método de pago del usuario
     * y la cuenta en el banco.
     *
     * Flujo:
     * 1. Busca la tarjeta en el banco por su número.
     * 2. Valida que los datos coincidan (marca, número, mes y año de expiración).
     * 3. Verifica que el saldo sea suficiente para cubrir el monto.
     * 4. Si es válido, descuenta el monto y actualiza el saldo en la base de datos.
     *
     * @param billingMethod método de pago registrado por el usuario.
     * @param monto monto de la transacción.
     * @return true si la transacción fue exitosa, false si falló.
     */
    public Boolean ejecutarTransaccion(BillingMethod billingMethod, BigDecimal monto) {
        // Buscar la tarjeta en el banco
        Bank bank = bankRepository.findByCardNumber(billingMethod.getCardNumber()).orElse(null);
        if (bank == null) {
            return false; // No existe la tarjeta
        }

        // Validar coincidencia de datos críticos
        if (bank.getBrand().equals(billingMethod.getBrand()) &&
                bank.getCardNumber().equals(billingMethod.getCardNumber()) &&
                bank.getExpMonth().equals(billingMethod.getExpMonth()) &&
                bank.getExpYear().equals(billingMethod.getExpYear())) {

            // Validar saldo suficiente
            if (bank.getBalance().compareTo(monto) >= 0) {
                bank.setBalance(bank.getBalance().subtract(monto)); // Descontar
                bankRepository.save(bank); // Persistir cambios
                return true;
            }
        }

        return false; // Falla por datos incorrectos o falta de saldo
    }
}
