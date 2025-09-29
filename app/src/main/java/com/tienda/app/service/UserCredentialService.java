package com.tienda.app.service;

import com.tienda.app.model.UserCredentialModel;
import com.tienda.app.repository.UserCredentialRepository;
import org.springframework.stereotype.Service;

/**
 * Servicio que gestiona la lógica de negocio relacionada con las credenciales de usuario.
 *
 * Provee métodos para:
 * - Registrar nuevos usuarios.
 * - Validar que un email no esté repetido.
 * - Autenticar credenciales durante el login.
 */
@Service
public class UserCredentialService {

    private final UserCredentialRepository userCredentialRepository;

    /**
     * Inyección de dependencias mediante constructor.
     * @param userCredentialRepository repositorio JPA de credenciales de usuario.
     */
    public UserCredentialService(UserCredentialRepository userCredentialRepository) {
        this.userCredentialRepository = userCredentialRepository;
    }

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * @param email correo electrónico del usuario.
     * @param passwordHash contraseña en formato hash (nunca en texto plano).
     * @return el usuario registrado o null si los parámetros son inválidos.
     */
    public UserCredentialModel registrarUser(String email, String passwordHash) {
        if (email == null || email.isEmpty() || passwordHash == null || passwordHash.isEmpty()) return null;

        UserCredentialModel userCredential = new UserCredentialModel();
        userCredential.setEmail(email);
        userCredential.setPasswordHash(passwordHash);

        return userCredentialRepository.save(userCredential);
    }

    /**
     * Busca un usuario por su email.
     *
     * Se utiliza principalmente para verificar que no se registren emails duplicados.
     *
     * @param email correo electrónico a buscar.
     * @return el usuario si existe, null en caso contrario.
     */
    public UserCredentialModel buscarPorEmail(String email) {
        return userCredentialRepository.findByEmail(email).orElse(null);
    }

    /**
     * Valida las credenciales de login de un usuario.
     *
     * @param email correo electrónico ingresado.
     * @param password_hash contraseña en formato hash.
     * @return el usuario autenticado si existe, null si las credenciales son incorrectas.
     */
    public UserCredentialModel ValidarCredenciales(String email, String password_hash) {
        return userCredentialRepository.findByEmailAndPasswordHashEquals(email, password_hash).orElse(null);
    }
}
