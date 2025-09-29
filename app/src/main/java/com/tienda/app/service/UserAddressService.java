package com.tienda.app.service;

import com.tienda.app.model.UserAddress;
import com.tienda.app.repository.UserAddressRepository;
import org.springframework.stereotype.Service;

/**
 * Servicio que gestiona la lógica de negocio relacionada con las direcciones de usuario.
 *
 * Permite:
 * - Crear nuevas direcciones.
 * - Consultar la dirección de un usuario.
 * - Actualizar una dirección existente.
 *
 * Funciona como capa intermedia entre el controlador y el repositorio.
 */
@Service
public class UserAddressService {

    private final UserAddressRepository userAddressRepository;

    /**
     * Inyección de dependencias mediante constructor.
     * @param userAddressRepository repositorio JPA de direcciones de usuario.
     */
    public UserAddressService(UserAddressRepository userAddressRepository) {
        this.userAddressRepository = userAddressRepository;
    }

    /**
     * Crea y guarda una nueva dirección en la base de datos.
     *
     * @param userAddress entidad {@link UserAddress} con los datos de la dirección.
     * @return la dirección guardada.
     */
    public UserAddress create(UserAddress userAddress) {
        return userAddressRepository.save(userAddress);
    }

    /**
     * Obtiene la dirección asociada a un usuario.
     *
     * ⚠️ Nota: este método asume que cada usuario solo puede tener **una dirección**.
     * Si se permitieran múltiples direcciones, debería usarse un `List<UserAddress>`.
     *
     * @param id id del usuario.
     * @return la dirección encontrada o null si no existe.
     */
    public UserAddress getUserAddressByUserId(Integer id) {
        return userAddressRepository.findByUser_Id(id).orElse(null);
    }

    /**
     * Actualiza una dirección existente en la base de datos.
     *
     * @param userAddress entidad {@link UserAddress} con los nuevos datos.
     * @return la dirección actualizada.
     */
    public UserAddress updateUserAddress(UserAddress userAddress) {
        return userAddressRepository.save(userAddress);
    }
}
