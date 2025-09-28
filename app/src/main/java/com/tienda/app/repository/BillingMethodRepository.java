package com.tienda.app.repository;

import com.tienda.app.model.BillingMethod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BillingMethodRepository extends JpaRepository<BillingMethod,Integer> {
    Optional<BillingMethod> findByUser_Id(Integer id);
    List<BillingMethod> findAllByUser_Id(Integer id);
}
