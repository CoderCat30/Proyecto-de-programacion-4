package com.tienda.app.service;

import com.tienda.app.model.Bank;
import com.tienda.app.model.BillingMethod;
import com.tienda.app.repository.BankRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class BankService {
    private final BankRepository bankRepository;
    public BankService(BankRepository bankRepository) {
        this.bankRepository = bankRepository;
    }

    //metodo para ejecutar transaccion de tarjeta de usuario vs cuenta en el banco
    public Boolean ejecutarTransaccion(BillingMethod billingMethod, BigDecimal monto) {
        Bank bank = bankRepository.findByCardNumber(billingMethod.getCardNumber()).orElse(null);
        if (bank == null) {
            return false;
        }
        //Verificar que sean los mismos brand(proveedor, numero de tarjeta, mes y anno de expiracion)
        if(bank.getBrand().equals(billingMethod.getBrand()) && bank.getCardNumber().equals(billingMethod.getCardNumber()) &&
        bank.getExpMonth().equals(billingMethod.getExpMonth()) && bank.getExpYear().equals(billingMethod.getExpYear())) {
            if(bank.getBalance().compareTo(monto) >= 0) {
                bank.setBalance(bank.getBalance().subtract(monto));
                bankRepository.save(bank);
                return true;
            }
        }
        return false;
    }
}
