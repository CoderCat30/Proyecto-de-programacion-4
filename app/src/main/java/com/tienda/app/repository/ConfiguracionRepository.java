package com.tienda.app.repository;

import com.tienda.app.model.Configuracion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfiguracionRepository extends JpaRepository<Configuracion, Integer> {
    public Configuracion findByClave(String clave);
}
