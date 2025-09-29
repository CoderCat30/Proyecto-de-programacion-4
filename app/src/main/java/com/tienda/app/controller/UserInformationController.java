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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    //Pagina para visualizar informacion personal y metodos de pagos
    @GetMapping
    public String inicio(HttpSession session, Model model){
        UserCredentialModel userCredentialModel = (UserCredentialModel) session.getAttribute("usuarioLog");

        UserInformation userInformation = userInformationService.getUserInformationById(userCredentialModel.getId());
        UserAddress userAddress = userAddressService.getUserAddressByUserId(userCredentialModel.getId());
        List<BillingMethod> list = billingMethodService.findAllByUserId(userCredentialModel.getId());
        if (userInformation == null){//si no se encontro informacion personal y direccion el usuario esta obligado a crearlos
            return "redirect:/pagina_personal/pagina_datosPersonales";
        }
        model.addAttribute("usuarioInfo", userInformation);
        model.addAttribute("usuarioAddr", userAddress);
        model.addAttribute("billingMs", list);
        return "pagina_personal";
    }


    //Pagina para crear informacion personal y direccion(address)
    @GetMapping("/pagina_datosPersonales")
    public String pagDatosPersonales(HttpSession session, Model model){
        return "pagina_datosPersonales";
    }



    @PostMapping("/actualizaraddr")
    public String actualizarDireccion(@ModelAttribute("usuarioAddr") UserAddress usuarioAddr, HttpSession session) {
        // Obtener el usuario logueado si necesita asociar la dirección
        UserCredentialModel user = (UserCredentialModel) session.getAttribute("usuarioLog");
        usuarioAddr.setUser(user);
        userAddressService.updateUserAddress(usuarioAddr);
        return "redirect:/pagina_personal"; // volver a la página del perfil
    }

    @PostMapping("/deletecard/{id}")
    public String deleteDireccion(@PathVariable("id") Integer id){
        billingMethodService.deleteById(id);
        return "redirect:/pagina_personal";
    }

    //metodo que procesa y crea informacion personal y direccion(address)
    @PostMapping("/perfil/crear")
    public String crearPerfil(@ModelAttribute UserInformation info,
                              @ModelAttribute UserAddress addr,
                              HttpSession session) {
        UserCredentialModel userCredentialModel = (UserCredentialModel) session.getAttribute("usuarioLog");

        // completar lo que no viene del formulario (id del user)
        info.setId(userCredentialModel.getId());
        userInformationService.create(info);

        addr.setUser(userCredentialModel);
        userAddressService.create(addr);
        return "redirect:/";
    }


    // Mostrar formulario para agregar tarjeta
    @GetMapping("/agregar-tarjeta")
    public String mostrarFormularioTarjeta(HttpSession session, Model model) {
        UserCredentialModel user = (UserCredentialModel) session.getAttribute("usuarioLog");
        if (user == null) {
            return "redirect:/login";
        }
        return "agregar_tarjeta";
    }

    @PostMapping("/agregar-tarjeta")
    public String agregarTarjeta(@RequestParam("cardNumber") String cardNumber,
                                 @RequestParam("brand") String brand,
                                 @RequestParam("expMonth") Integer expMonth,
                                 @RequestParam("expYear") Integer expYear,
                                 @RequestParam("nameOnCard") String nameOnCard,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        UserCredentialModel user = (UserCredentialModel) session.getAttribute("usuarioLog");

        if (user == null) {
            return "redirect:/login";
        }

        // Crear el objeto manualmente
        BillingMethod billingMethod = new BillingMethod();
        billingMethod.setCardNumber(cardNumber);
        billingMethod.setBrand(brand);
        billingMethod.setExpMonth(expMonth);  // Se convertirá automáticamente con el setter sobrecargado
        billingMethod.setExpYear(expYear);    // Se convertirá automáticamente con el setter sobrecargado
        billingMethod.setNameOnCard(nameOnCard);
        billingMethod.setUser(user);

        // Verificar que el usuario no tenga ya esta tarjeta registrada
        if (billingMethodService.usuarioTieneTarjeta(user.getId(), billingMethod.getCardNumber())) {
            redirectAttributes.addFlashAttribute("error",
                    "Esta tarjeta ya está registrada en su cuenta.");
            return "redirect:/pagina_personal/agregar-tarjeta";
        }

        // Guardar
        billingMethodService.create(billingMethod);

        redirectAttributes.addFlashAttribute("success",
                "Tarjeta agregada exitosamente.");
        return "redirect:/pagina_personal";
    }

}
