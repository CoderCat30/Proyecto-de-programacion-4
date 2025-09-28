package com.tienda.app.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "user_credentials")
public class UserCredentialModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "email", nullable = false, length = 254)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "role", nullable = false, length = 10)
    private String role;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    @Override
    public String toString() {
        return "UserCredentialModel{" +
                "email='" + email + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof UserCredentialModel that)) return false;
        return Objects.equals(id, that.id) && Objects.equals(email, that.email) && Objects.equals(passwordHash, that.passwordHash);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, passwordHash);
    }
}