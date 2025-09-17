package com.tienda.app.controller;

import com.tienda.app.model.UserCredentialModel;
import com.tienda.app.service.UserCredentialService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserCredentialController {
    private final UserCredentialService userCredentialService;
    public UserCredentialController(UserCredentialService userCredentialService) {
        this.userCredentialService = userCredentialService;
    }



    @GetMapping("/registrar")
    public String getRegistrarP(){
        return "pagina_registrar";
        //va en static
    }

    @PostMapping("/registrar")
    public String registrarUsuario(@ModelAttribute UserCredentialModel userCredential, Model model){
        System.out.println("solicitud registrar: "  + userCredential);
        UserCredentialModel user = userCredentialService.buscarPorEmail(userCredential.getEmail());
        //Se manda a buscar por login o email, si se encontro 1 usuario coincidiente se manda error
        if(user != null){
            model.addAttribute("error", "Email ya registrado en el sistema");
            return "pagina_registrar";
        }
        userCredentialService.registrarUser(userCredential.getEmail(), userCredential.getPasswordHash());
        model.addAttribute("exito", "Usuario registrado en el sistema");
        return "pagina_registrar";
    }

}
