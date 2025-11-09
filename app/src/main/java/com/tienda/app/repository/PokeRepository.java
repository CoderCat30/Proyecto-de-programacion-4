package com.tienda.app.repository;

import com.tienda.app.model.Pokemon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PokeRepository extends JpaRepository<Pokemon, Integer> {
    Optional<Pokemon> findByName(String name);
    boolean existsByName(String name);
}
