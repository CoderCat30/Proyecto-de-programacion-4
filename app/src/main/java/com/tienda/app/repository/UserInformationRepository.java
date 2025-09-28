package com.tienda.app.repository;

import com.tienda.app.model.UserInformation;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio JPA para la entidad UserInformation (información personal del usuario).
 *
 * - Extiende JpaRepository<UserInformation, Integer>, lo que le da acceso directo
 *   a todas las operaciones CRUD sin necesidad de implementarlas manualmente:
 *     - save(entity) → guardar o actualizar información personal.
 *     - findById(id) → buscar información personal por id de usuario.
 *     - findAll() → listar toda la información registrada.
 *     - deleteById(id) → eliminar información personal por id.
 *
 * - El segundo parámetro <Integer> corresponde al tipo de dato de la clave primaria
 *   de la entidad (user_id, que también es PK y FK hacia UserCredentialModel).
 *
 * - Por ahora no define métodos personalizados, pero puedes agregarlos
 *   con la convención de Spring Data JPA.
 *
 * Ejemplo de extensión:
 *   Optional<UserInformation> findByUserCredentials_Email(String email);
 */
public interface UserInformationRepository extends JpaRepository<UserInformation, Integer> {
    // Aquí podrías definir consultas específicas si necesitas buscar por email o cédula, por ejemplo.
}
