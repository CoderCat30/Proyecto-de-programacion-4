package com.tienda.app.service;

import com.tienda.app.model.BillingMethod;
import com.tienda.app.repository.BankRepository;
import com.tienda.app.repository.BillingMethodRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BillingMethodService {
    public BillingMethodService(BillingMethodRepository billingMethodRepository, BankRepository bankRepository) {
        this.billingMethodRepository = billingMethodRepository;
        this.bankRepository = bankRepository;
    }

    private final BillingMethodRepository billingMethodRepository;
    private final BankRepository bankRepository;


    public List<BillingMethod> findAllByUserId(Integer id){
        List<BillingMethod> l1 = billingMethodRepository.findAllByUser_Id(id);
        l1.forEach(BillingMethod::clearFields);
        return l1;
    }

    public BillingMethod findById(Integer id){
        return billingMethodRepository.findById(id).orElse(null);
    }

    public void deleteById(Integer id){
        billingMethodRepository.deleteById(id);
    }


    //Metodo para la cencura del numero de tarjeta
    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 6) {
            return cardNumber; // si es inválido, lo devuelvo como está
        }
        String first2 = cardNumber.substring(0, 2);
        String last4 = cardNumber.substring(cardNumber.length() - 4);
        String middle = "*".repeat(cardNumber.length() - 6);
        return first2 + middle + last4;
    }

   /* public List<BillingMethod> findAllByUserId(Integer userId) {
        return billingMethodRepository.findByUserId(userId);
    }*/

    public void create(BillingMethod billingMethod) {
        billingMethodRepository.save(billingMethod);
    }

   /* public void deleteById(Integer id) {
        billingMethodRepository.deleteById(id);
    }*/

    // Validar que la tarjeta exista en la tabla bank
    public boolean validarTarjetaEnBanco(String cardNumber, String brand,
                                         Integer expMonth, Integer expYear,
                                         String nameOnCard) {
        return bankRepository.existsByCardNumberAndBrandAndExpMonthAndExpYearAndNameOnCard(
                cardNumber, brand, expMonth, expYear, nameOnCard
        );
    }

    // Verificar si el usuario ya tiene registrada esta tarjeta
    public boolean usuarioTieneTarjeta(Integer userId, String cardNumber) {
        return billingMethodRepository.existsByUserIdAndCardNumber(userId, cardNumber);
    }

}
