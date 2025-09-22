package com.tienda.app.controller;

import com.tienda.app.model.BillingMethod;
import com.tienda.app.model.UserAddress;
import com.tienda.app.model.UserCredentialModel;
import com.tienda.app.model.UserInformation;
import com.tienda.app.service.BillingMethodService;
import com.tienda.app.service.UserAddressService;
import com.tienda.app.service.UserInformationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/pagina_personal")
public class UserInformationController {
    private final UserInformationService userInformationService;
    private final UserAddressService userAddressService;
    private final BillingMethodService billingMethodService;

    public UserInformationController(UserInformationService userInformationServices, UserAddressService userAddressService, BillingMethodService billingMethodService) {
        this.userInformationService = userInformationServices;
        this.userAddressService = userAddressService;
        this.billingMethodService = billingMethodService;
    }

    @GetMapping
    public String inicio(HttpSession session, Model model){
        UserCredentialModel userCredentialModel = (UserCredentialModel) session.getAttribute("usuarioLog");

        UserInformation userInformation = userInformationService.getUserInformationById(userCredentialModel.getId());
        UserAddress userAddress = userAddressService.getUserAddressById(userCredentialModel.getId());
        List<BillingMethod> list = billingMethodService.findAllByUserId(userCredentialModel.getId());
        if (userInformation == null){
            return "redirect:/pagina_personal/pagina_datosPersonales";
        }
        model.addAttribute("usuarioInfo", userInformation);
        model.addAttribute("usuarioAddr", userAddress);
        model.addAttribute("billingMs", list);
        return "pagina_personal";
    }


    @GetMapping("/pagina_datosPersonales")
    public String pagDatosPersonales(HttpSession session, Model model){
        return "pagina_datosPersonales";
    }



    @PostMapping("/actualizaraddr")
    public String actualizarDireccion(@ModelAttribute("usuarioAddr") UserAddress usuarioAddr, HttpSession session) {
        // Obtener el usuario logueado si necesitas asociar la dirección
        UserCredentialModel user = (UserCredentialModel) session.getAttribute("usuarioLog");
        usuarioAddr.setUser(user);
        userAddressService.updateUserAddress(usuarioAddr.getId(), usuarioAddr);
        return "redirect:/pagina_personal"; // volver a la página del perfil
    }

    @PostMapping("/deletecard/{id}")
    public String deleteDireccion(@PathVariable("id") Integer id){
        billingMethodService.deleteById(id);
        return "redirect:/pagina_personal";
    }

}
