package com.tienda.app.controller;

import com.tienda.app.model.UserCredentialModel;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Clase de configuración global de controladores.
 *
 * Usando la anotación @ControllerAdvice, esta clase permite:
 * - Inyectar atributos comunes en todos los controladores y vistas Thymeleaf.
 * - En este caso, se agrega automáticamente el atributo "usuarioLog" a los modelos,
 *   permitiendo que todas las páginas tengan acceso a la sesión del usuario autenticado.
 */
@ControllerAdvice
public class GlobalControllerAdvice {

    /**
     * Método que añade el usuario logueado (si existe en sesión) como atributo de modelo global.
     *
     * @param session La sesión HTTP actual del usuario.
     * @return El objeto UserCredentialModel del usuario autenticado,
     *         o null si no hay nadie logueado.
     *
     * Este atributo ("usuarioLog") podrá ser accedido en cualquier vista
     * de Thymeleaf sin necesidad de añadirlo manualmente en cada controlador.
     */
    @ModelAttribute("usuarioLog")
    public UserCredentialModel addUserToModel(HttpSession session) {
        return (UserCredentialModel) session.getAttribute("usuarioLog");
    }
}
