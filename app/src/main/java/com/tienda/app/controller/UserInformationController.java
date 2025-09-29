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

/**
 * Controlador para la página personal del usuario.
 *
 * Funcionalidad:
 * - Visualizar datos personales, dirección y métodos de pago.
 * - Crear/actualizar perfil (UserInformation + UserAddress).
 * - Agregar/eliminar métodos de pago (tarjetas).
 *
 * Notas:
 * - Se asume que el usuario logueado está en sesión como "usuarioLog" (UserCredentialModel).
 * - Las vistas principales: "pagina_personal", "pagina_datosPersonales", "agregar_tarjeta".
 */
@Controller
@RequestMapping("/pagina_personal")
public class UserInformationController {

    private final UserInformationService userInformationService;
    private final UserAddressService userAddressService;
    private final BillingMethodService billingMethodService;

    /**
     * Inyección de servicios para manejar información personal, direcciones y métodos de pago.
     */
    public UserInformationController(UserInformationService userInformationServices,
                                     UserAddressService userAddressService,
                                     BillingMethodService billingMethodService) {
        this.userInformationService = userInformationServices;
        this.userAddressService = userAddressService;
        this.billingMethodService = billingMethodService;
    }

    /**
     * GET /pagina_personal
     * Muestra el dashboard de la página personal con:
     * - Información personal (UserInformation)
     * - Dirección (UserAddress)
     * - Métodos de pago (BillingMethod)
     *
     * Requiere usuario logueado en sesión ("usuarioLog").
     * Si no hay información personal creada, redirige a /pagina_personal/pagina_datosPersonales.
     *
     * @param session sesión HTTP para obtener el usuario logueado.
     * @param model   modelo para la vista.
     * @return "pagina_personal" o redirect a formulario de datos personales.
     */
    @GetMapping
    public String inicio(HttpSession session, Model model){
        // Recuperar usuario logueado desde sesión
        UserCredentialModel userCredentialModel = (UserCredentialModel) session.getAttribute("usuarioLog");

        // Cargar información personal, dirección y tarjetas asociadas
        UserInformation userInformation = userInformationService.getUserInformationById(userCredentialModel.getId());
        UserAddress userAddress = userAddressService.getUserAddressByUserId(userCredentialModel.getId());
        List<BillingMethod> list = billingMethodService.findAllByUserId(userCredentialModel.getId());

        // Si no hay datos personales aún, forzar a completarlos
        if (userInformation == null){
            return "redirect:/pagina_personal/pagina_datosPersonales";
        }

        // Poblar modelo para la vista
        model.addAttribute("usuarioInfo", userInformation);
        model.addAttribute("usuarioAddr", userAddress);
        model.addAttribute("billingMs", list);

        return "pagina_personal";
    }

    /**
     * GET /pagina_personal/pagina_datosPersonales
     * Muestra el formulario para crear info personal (UserInformation) y dirección (UserAddress).
     *
     * @param session sesión HTTP (por si se requiere validar login).
     * @param model   modelo para la vista.
     * @return "pagina_datosPersonales"
     */
    @GetMapping("/pagina_datosPersonales")
    public String pagDatosPersonales(HttpSession session, Model model){
        // Vista con el formulario de datos personales/dirección
        return "pagina_datosPersonales";
    }

    /**
     * POST /pagina_personal/actualizaraddr
     * Actualiza la dirección del usuario actual.
     *
     * @param usuarioAddr DTO de dirección recibido del formulario (se asocia al usuario logueado).
     * @param session     sesión HTTP para obtener "usuarioLog".
     * @return redirect a /pagina_personal
     */
    @PostMapping("/actualizaraddr")
    public String actualizarDireccion(@ModelAttribute("usuarioAddr") UserAddress usuarioAddr, HttpSession session) {
        // Asociar dirección al usuario logueado
        UserCredentialModel user = (UserCredentialModel) session.getAttribute("usuarioLog");
        usuarioAddr.setUser(user);

        // Persistir actualización
        userAddressService.updateUserAddress(usuarioAddr);

        // Volver a la página personal
        return "redirect:/pagina_personal";
    }

    /**
     * POST /pagina_personal/deletecard/{id}
     * Elimina un método de pago (tarjeta) por su ID.
     *
     * @param id ID del BillingMethod a eliminar.
     * @return redirect a /pagina_personal
     */
    @PostMapping("/deletecard/{id}")
    public String deleteDireccion(@PathVariable("id") Integer id){
        billingMethodService.deleteById(id);
        return "redirect:/pagina_personal";
    }

    /**
     * POST /pagina_personal/perfil/crear
     * Crea el perfil inicial del usuario (UserInformation + UserAddress).
     *
     * - Setea el id del UserInformation con el id del usuario logueado.
     * - Asocia la dirección al usuario.
     *
     * @param info    DTO con info personal.
     * @param addr    DTO con dirección.
     * @param session sesión HTTP para obtener usuario logueado.
     * @return redirect a la home ("/") tras crear el perfil.
     */
    @PostMapping("/perfil/crear")
    public String crearPerfil(@ModelAttribute UserInformation info,
                              @ModelAttribute UserAddress addr,
                              HttpSession session) {
        // Usuario logueado
        UserCredentialModel userCredentialModel = (UserCredentialModel) session.getAttribute("usuarioLog");

        // Completar campos dependientes del usuario
        info.setId(userCredentialModel.getId());
        userInformationService.create(info);

        addr.setUser(userCredentialModel);
        userAddressService.create(addr);

        return "redirect:/";
    }

    /**
     * GET /pagina_personal/agregar-tarjeta
     * Muestra formulario para agregar un nuevo método de pago (tarjeta).
     *
     * @param session sesión HTTP para validar usuario logueado.
     * @param model   modelo para la vista.
     * @return "agregar_tarjeta" o redirect a login si no hay sesión.
     */
    @GetMapping("/agregar-tarjeta")
    public String mostrarFormularioTarjeta(HttpSession session, Model model) {
        UserCredentialModel user = (UserCredentialModel) session.getAttribute("usuarioLog");
        if (user == null) {
            return "redirect:/login";
        }
        return "agregar_tarjeta";
    }

    /**
     * POST /pagina_personal/agregar-tarjeta
     * Agrega una nueva tarjeta al usuario logueado:
     * - Valida que el usuario esté autenticado.
     * - Evita duplicados por número de tarjeta.
     * - Persiste el BillingMethod.
     *
     * @param cardNumber  número de tarjeta.
     * @param brand       marca (Visa/Mastercard/…).
     * @param expMonth    mes de expiración.
     * @param expYear     año de expiración.
     * @param nameOnCard  nombre impreso en la tarjeta.
     * @param session     sesión HTTP con "usuarioLog".
     * @param redirectAttributes flash attributes para mensajes de éxito/error.
     * @return redirect a /pagina_personal o recarga del formulario si hay error de duplicado.
     */
    @PostMapping("/agregar-tarjeta")
    public String agregarTarjeta(@RequestParam("cardNumber") String cardNumber,
                                 @RequestParam("brand") String brand,
                                 @RequestParam("expMonth") Integer expMonth,
                                 @RequestParam("expYear") Integer expYear,
                                 @RequestParam("nameOnCard") String nameOnCard,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        // Validar login
        UserCredentialModel user = (UserCredentialModel) session.getAttribute("usuarioLog");
        if (user == null) {
            return "redirect:/login";
        }

        // Crear objeto de tarjeta
        BillingMethod billingMethod = new BillingMethod();
        billingMethod.setCardNumber(cardNumber);
        billingMethod.setBrand(brand);
        billingMethod.setExpMonth(expMonth);
        billingMethod.setExpYear(expYear);
        billingMethod.setNameOnCard(nameOnCard);
        billingMethod.setUser(user);

        // Validar duplicado por número de tarjeta
        if (billingMethodService.usuarioTieneTarjeta(user.getId(), billingMethod.getCardNumber())) {
            redirectAttributes.addFlashAttribute("error",
                    "Esta tarjeta ya está registrada en su cuenta.");
            return "redirect:/pagina_personal/agregar-tarjeta";
        }

        // Persistir tarjeta
        billingMethodService.create(billingMethod);

        redirectAttributes.addFlashAttribute("success", "Tarjeta agregada exitosamente.");
        return "redirect:/pagina_personal";
    }
}
