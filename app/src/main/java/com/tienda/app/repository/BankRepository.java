package com.tienda.app.repository;

import com.tienda.app.model.Bank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BankRepository extends JpaRepository<Bank, Integer> {
    Optional<Bank> findByCardNumber(String cardNumber);
    boolean existsByCardNumberAndBrandAndExpMonthAndExpYearAndNameOnCard(String cardNumber, String brand,
                                                                         Integer expMonth, Integer expYear,
                                                                         String nameOnCard);
}
