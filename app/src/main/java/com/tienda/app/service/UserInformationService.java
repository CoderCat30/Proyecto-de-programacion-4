package com.tienda.app.service;

import com.tienda.app.model.UserInformation;
import com.tienda.app.repository.UserInformationRepository;
import org.springframework.stereotype.Service;

/**
 * Servicio que gestiona la lógica de negocio relacionada con la información personal del usuario.
 *
 * Permite:
 * - Crear o actualizar registros de información personal.
 * - Consultar información personal por ID de usuario.
 *
 * Funciona como capa intermedia entre el controlador y el repositorio.
 */
@Service
public class UserInformationService {

    private final UserInformationRepository userInformationRepository;

    /**
     * Inyección de dependencias mediante constructor.
     * @param userInformationRepository repositorio JPA de información de usuario.
     */
    public UserInformationService(UserInformationRepository userInformationRepository) {
        this.userInformationRepository = userInformationRepository;
    }

    /**
     * Crea o actualiza un registro de información personal.
     *
     * @param userInformation entidad {@link UserInformation} con los datos del usuario.
     * @return la entidad persistida en la base de datos.
     */
    public UserInformation create(UserInformation userInformation) {
        return userInformationRepository.save(userInformation);
    }

    /**
     * Obtiene la información personal de un usuario a partir de su ID.
     *
     * @param id id del usuario (coincide con el id de {@link com.tienda.app.model.UserCredentialModel}).
     * @return objeto {@link UserInformation} si existe, null si no.
     */
    public UserInformation getUserInformationById(Integer id) {
        return userInformationRepository.findById(id).orElse(null);
    }
}
