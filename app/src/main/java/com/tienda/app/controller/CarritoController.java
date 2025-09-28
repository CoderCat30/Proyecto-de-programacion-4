package com.tienda.app.controller;

import com.tienda.app.model.*;
import com.tienda.app.repository.BankRepository;
import com.tienda.app.service.BankService;
import com.tienda.app.service.BillingMethodService;
import com.tienda.app.service.UserAddressService;
import com.tienda.app.service.UserInformationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/carrito")
public class CarritoController {
    public CarritoController(UserInformationService userInformationService, UserAddressService userAddressService,
                             BillingMethodService billingMethodService, BankService bankService) {
        this.userInformationService = userInformationService;
        this.userAddressService = userAddressService;
        this.billingMethodService = billingMethodService;
        this.bankService = bankService;
    }

    private final UserInformationService userInformationService;
    private final UserAddressService userAddressService;
    private final BillingMethodService  billingMethodService;
    private final BankService bankService;



    @GetMapping
    public String verCarrito(HttpSession session, Model model) {
        model.addAttribute("tittle","Carrito");

        List<CarritoItem> carrito = (List<CarritoItem>) session.getAttribute("carrito");
        if (carrito == null) carrito = new ArrayList<>();

        BigDecimal total = carrito.stream().map(CarritoItem::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("title", "Carrito");
        model.addAttribute("carrito", carrito);
        model.addAttribute("total", total);
        return "carrito"; // carrito.html
    }

    @PostMapping("/agregar")
    public String agregarAlCarrito(@RequestParam Integer id,
                                   @RequestParam String nombre,
                                   @RequestParam BigDecimal precio,
                                   HttpSession session) {
        List<CarritoItem> carrito = (List<CarritoItem>) session.getAttribute("carrito");
        if (carrito == null) carrito = new ArrayList<>();

        boolean existe = false;
        for (CarritoItem item : carrito) {
            if (item.getProductoId().equals(id)) {
                item.setCantidad(item.getCantidad() + 1);
                existe = true;
                break;
            }
        }

        if (!existe) {
            carrito.add(new CarritoItem(id, nombre, precio, 1));
        }

        session.setAttribute("carrito", carrito);
        return "redirect:/carrito";
    }

    @PostMapping("/actualizar")
    public String actualizarCantidad(@RequestParam Integer id,
                                     @RequestParam int cantidad,
                                     HttpSession session) {
        List<CarritoItem> carrito = (List<CarritoItem>) session.getAttribute("carrito");
        if (carrito != null) {
            carrito.removeIf(item -> {
                if (item.getProductoId().equals(id)) {
                    if (cantidad > 0) {
                        item.setCantidad(cantidad);
                        return false;
                    } else {
                        return true;
                    }
                }
                return false;
            });
            session.setAttribute("carrito", carrito);
        }
        return "redirect:/carrito";
    }

    @PostMapping("/eliminar")
    public String eliminarDelCarrito(@RequestParam Integer id, HttpSession session) {
        List<CarritoItem> carrito = (List<CarritoItem>) session.getAttribute("carrito");
        if (carrito != null) {
            carrito.removeIf(item -> item.getProductoId().equals(id));
            session.setAttribute("carrito", carrito);
        }
        return "redirect:/carrito";
    }

    @PostMapping("/vaciar")
    public String vaciarCarrito(HttpSession session) {
        session.removeAttribute("carrito");
        return "redirect:/carrito";
    }

    //Aqui Beto!!¡¡
    @GetMapping("/finalizar")
    public String mostrarFormularioFinalizar(HttpSession session, Model model) {
        List<CarritoItem> carrito = (List<CarritoItem>) session.getAttribute("carrito");

        if (carrito == null || carrito.isEmpty()) {
            model.addAttribute("error", "Tu carrito está vacío.");
            return "carrito"; // vuelve a carrito si está vacío
        }

        //Caso no haya iniciado sesion, no se puede comprar!!¡¡
        if(session.getAttribute("usuarioLog") == null){
            return "redirect:/credenciales/ingresar";
        }
        UserCredentialModel userCredentialModel = (UserCredentialModel) session.getAttribute("usuarioLog");
        UserInformation userInformation = userInformationService.getUserInformationById(userCredentialModel.getId());
        UserAddress userAddress = userAddressService.getUserAddressByUserId(userCredentialModel.getId());
        if (userInformation == null){//si no se encontro informacion personal y direccion el usuario esta obligado a crearlos
            return "redirect:/pagina_personal/pagina_datosPersonales";
        }
        List<BillingMethod> billingMethodList = billingMethodService.findAllByUserId(userInformation.getId());

        model.addAttribute("usuarioInfo", userInformation);
        model.addAttribute("usuarioAddr", userAddress);

        model.addAttribute("carrito", carrito);
        model.addAttribute("checkout", new Checkout());
        model.addAttribute("billingMethodList", billingMethodList);

        //Verificando si hubo error en una transaccion pasada(falta de dinero)
        String errorDinero = (String) session.getAttribute("errorDinero");
        if (errorDinero != null) {
            model.addAttribute("errorDinero", errorDinero);
            session.removeAttribute("errorDinero"); // <- lo borramos para que no se repita
        }
        return "checkout"; // checkout.html
    }

    //Aqui verificar metodo de pago vs banco, regresar a /finalizar sino tiene dinero(aqui se escoge la tarjeta a utilizar)
    @PostMapping("/finalizar")
    public String procesarCompra(@ModelAttribute("checkout") Checkout checkout,
                                 HttpSession session,
                                 Model model) {
        List<CarritoItem> carrito = (List<CarritoItem>) session.getAttribute("carrito");

        if (carrito == null || carrito.isEmpty()) {
            model.addAttribute("error", "Tu carrito está vacío.");
            return "carrito";
        }

        BillingMethod billingMethod = billingMethodService.findById(checkout.getMetodoPago());
        BigDecimal total = carrito.stream().map(CarritoItem::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        if(!bankService.ejecutarTransaccion(billingMethod, total)){ //Caso la operacion no fue exitosa
            session.setAttribute("errorDinero", "No se pudo realizar la compra falta dinero o fallo conexion banco");
            return "redirect:/carrito/finalizar";
        }

        //caso la operacion de cobro con el banco fue exitosa prosigue abajo
        billingMethod.clearFields();
        checkout.setCardnumber(billingMethod.getCardNumber());
        checkout.setCarrito(carrito);
        session.removeAttribute("carrito");

        model.addAttribute("checkout", checkout);
        model.addAttribute("total", total);

        //Aqui se puede crear la order, checkout tiene todo lo necesario
        return "confirmacion";
    }


}
