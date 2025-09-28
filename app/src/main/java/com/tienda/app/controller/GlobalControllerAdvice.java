package com.tienda.app.controller;

import com.tienda.app.model.UserCredentialModel;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute("usuarioLog")
    public UserCredentialModel addUserToModel(HttpSession session) {
        return (UserCredentialModel) session.getAttribute("usuarioLog");
    }
}
