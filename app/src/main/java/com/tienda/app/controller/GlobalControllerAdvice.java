package com.tienda.app.controller;

import com.tienda.app.model.UserCredentialModel;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Clase de configuración global con @ControllerAdvice.
 *
 * Sirve para aplicar lógica de manera transversal a todos los controladores.
 * En este caso, se asegura de que el atributo "usuarioLog" (usuario autenticado)
 * esté disponible automáticamente en el modelo de todas las vistas.
 */
@ControllerAdvice
public class GlobalControllerAdvice {

    /**
     * Método que agrega automáticamente el atributo "usuarioLog" al modelo.
     *
     * - Se ejecuta antes de llamar a cualquier controlador.
     * - Recupera el objeto "usuarioLog" desde la sesión (si existe).
     * - Hace que el atributo esté disponible en todas las vistas Thymeleaf sin tener que
     *   agregarlo manualmente en cada controlador.
     *
     * Ejemplo de uso en Thymeleaf:
     *   <span th:text="${usuarioLog.email}"></span>
     *
     * @param session Sesión HTTP actual del usuario
     * @return objeto UserCredentialModel almacenado en sesión, o null si no hay usuario logueado
     */
    @ModelAttribute("usuarioLog")
    public UserCredentialModel addUserToModel(HttpSession session) {
        return (UserCredentialModel) session.getAttribute("usuarioLog");
    }
}
