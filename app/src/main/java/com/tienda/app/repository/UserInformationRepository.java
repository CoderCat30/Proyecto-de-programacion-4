package com.tienda.app.repository;

import com.tienda.app.model.UserInformation;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio JPA para la entidad {@link UserInformation}.
 *
 * Extiende de {@link JpaRepository}, lo que provee automáticamente:
 * - Operaciones CRUD (save, findById, findAll, delete, etc.).
 *
 * Tipo genérico:
 * - Entidad: UserInformation
 * - Clave primaria: Integer (corresponde al mismo id que {@link com.tienda.app.model.UserCredentialModel}).
 */
public interface UserInformationRepository extends JpaRepository<UserInformation, Integer> {
    // Aquí se pueden definir consultas personalizadas en caso necesario,
    // por ejemplo: Optional<UserInformation> findByCedula(String cedula);
}
