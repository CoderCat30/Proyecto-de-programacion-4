package com.tienda.app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_information")
public class UserInformation {
    @Id
    @Column(name = "user_id", nullable = false)
    private Integer id;


    @Column(name = "cedula", nullable = false, length = 20)
    private String cedula;


    @Column(name = "full_name", nullable = false, length = 120)
    private String fullName;



    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

}