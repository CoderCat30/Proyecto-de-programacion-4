package com.tienda.app.controller;

import com.tienda.app.model.UserCredentialModel;
import com.tienda.app.service.UserCredentialService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/credenciales")
public class UserCredentialController {
    private final UserCredentialService userCredentialService;

    public UserCredentialController(UserCredentialService userCredentialService) {
        this.userCredentialService = userCredentialService;
    }


    @GetMapping("/ingresar")
    public String ingresar(Model model) {

        model.addAttribute("title", "Ingresar");
        model.addAttribute("userCredentialModel", new UserCredentialModel());
        return "pagina_ingresar";
    }

    @PostMapping("/ingresar")
    public String ingresar(@ModelAttribute UserCredentialModel userCredential, Model model){

        UserCredentialModel user = userCredentialService.ValidarCredenciales(userCredential.getEmail(), userCredential.getPasswordHash());
        //Se manda a buscar por login o email, si se encontro 1 usuario coincidiente se manda error
        if(user != null){
            model.addAttribute("error", "No existe usuario con esa contraseña");
            return "pagina_ingresar";
        }
        userCredentialService.registrarUser(userCredential.getEmail(), userCredential.getPasswordHash());
        model.addAttribute("exito", "Inicio exitoso");

        return "/";
    }

    @GetMapping("/registrar")
    public String getRegistrarP(Model model){
        model.addAttribute("title", "Ingresar");
        model.addAttribute("userCredentialModel", new UserCredentialModel());
        return "pagina_registrar";
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
