package com.tienda.app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "configuracion")
public class Configuracion {
    @Id
    @Column(name = "clave", nullable = false, length = 100)
    private String clave;

    @Column(name = "valor_segundos", nullable = false)
    private Integer valorSegundos;

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public Integer getValorSegundos() {
        return valorSegundos;
    }

    public void setValorSegundos(Integer valorSegundos) {
        this.valorSegundos = valorSegundos;
    }

}