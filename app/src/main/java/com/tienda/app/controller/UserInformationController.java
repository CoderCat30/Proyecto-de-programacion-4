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

/**
 * Controlador encargado de manejar la página personal del usuario.
 * Aquí se gestionan los datos personales, direcciones y métodos de pago.
 */
@Controller
@RequestMapping("/pagina_personal") // Todas las rutas relacionadas con el perfil de usuario
public class UserInformationController {

    // Servicios que proporcionan la lógica de negocio
    private final UserInformationService userInformationService;
    private final UserAddressService userAddressService;
    private final BillingMethodService billingMethodService;

    /**
     * Constructor con inyección de dependencias.
     * Se reciben los servicios que gestionan información personal, direcciones y métodos de pago.
     */
    public UserInformationController(UserInformationService userInformationServices,
                                     UserAddressService userAddressService,
                                     BillingMethodService billingMethodService) {
        this.userInformationService = userInformationServices;
        this.userAddressService = userAddressService;
        this.billingMethodService = billingMethodService;
    }

    /**
     * Método GET para mostrar la página personal del usuario.
     *
     * Flujo:
     * 1. Recupera el usuario logueado desde la sesión.
     * 2. Busca la información personal, dirección y métodos de pago asociados al usuario.
     * 3. Si no existe información personal, redirige a la página para completarla.
     * 4. Si existe, envía los datos al modelo para mostrarlos en la vista "pagina_personal.html".
     */
    @GetMapping
    public String inicio(HttpSession session, Model model){
        // Usuario logueado recuperado de la sesión
        UserCredentialModel userCredentialModel = (UserCredentialModel) session.getAttribute("usuarioLog");

        // Cargar información personal, dirección y métodos de pago
        UserInformation userInformation = userInformationService.getUserInformationById(userCredentialModel.getId());
        UserAddress userAddress = userAddressService.getUserAddressById(userCredentialModel.getId());
        List<BillingMethod> list = billingMethodService.findAllByUserId(userCredentialModel.getId());

        // Si no existe información personal registrada, redirigir a formulario de datos
        if (userInformation == null){
            return "redirect:/pagina_personal/pagina_datosPersonales";
        }

        // Enviar datos al modelo para renderizar en la vista
        model.addAttribute("usuarioInfo", userInformation);
        model.addAttribute("usuarioAddr", userAddress);
        model.addAttribute("billingMs", list);

        return "pagina_personal"; // Vista principal del perfil
    }

    /**
     * Método GET que carga la vista para ingresar o actualizar los datos personales.
     */
    @GetMapping("/pagina_datosPersonales")
    public String pagDatosPersonales(HttpSession session, Model model){
        return "pagina_datosPersonales";
    }

    /**
     * Método POST para actualizar la dirección del usuario.
     *
     * Flujo:
     * 1. Recupera al usuario logueado desde la sesión.
     * 2. Asocia la dirección al usuario.
     * 3. Llama al servicio para actualizar en la BD.
     * 4. Redirige a la página personal.
     */
    @PostMapping("/actualizaraddr")
    public String actualizarDireccion(@ModelAttribute("usuarioAddr") UserAddress usuarioAddr,
                                      HttpSession session) {
        // Obtener el usuario logueado
        UserCredentialModel user = (UserCredentialModel) session.getAttribute("usuarioLog");

        // Asociar la dirección al usuario
        usuarioAddr.setUser(user);

        // Actualizar en BD
        userAddressService.updateUserAddress(usuarioAddr.getId(), usuarioAddr);

        return "redirect:/pagina_personal"; // volver a la página del perfil
    }

    /**
     * Método POST para eliminar un método de pago del usuario.
     *
     * @param id identificador del método de pago a eliminar
     */
    @PostMapping("/deletecard/{id}")
    public String deleteDireccion(@PathVariable("id") Integer id){
        billingMethodService.deleteById(id);
        return "redirect:/pagina_personal"; // vuelve al perfil
    }
}
