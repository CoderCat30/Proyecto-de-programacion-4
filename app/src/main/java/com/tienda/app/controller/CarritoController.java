package com.tienda.app.controller;

import com.tienda.app.model.*;
import com.tienda.app.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/carrito")
public class CarritoController {
    private final ArticuloService articuloService;

    public CarritoController(UserInformationService userInformationService, UserAddressService userAddressService,
                             BillingMethodService billingMethodService, BankService bankService, ArticuloService articuloService) {
        this.userInformationService = userInformationService;
        this.userAddressService = userAddressService;
        this.billingMethodService = billingMethodService;
        this.bankService = bankService;
        this.articuloService = articuloService;
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
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {

        // Verificar stock disponible
        Optional<Articulo> productoOpt = articuloService.buscarPorId(id);

        if (productoOpt.isEmpty()) {  // ← Cambio aquí
            redirectAttributes.addFlashAttribute("error", "Producto no encontrado");
            return "redirect:/productos";
        }

        Articulo producto = productoOpt.get();  // ← Extraer el objeto del Optional

        List<CarritoItem> carrito = (List<CarritoItem>) session.getAttribute("carrito");
        if (carrito == null) carrito = new ArrayList<>();

        boolean existe = false;
        for (CarritoItem item : carrito) {
            if (item.getProductoId().equals(id)) {
                // Verificar que no exceda el stock
                if (item.getCantidad() + 1 > producto.getStockQuantity()) {
                    redirectAttributes.addFlashAttribute("error",
                            "No hay suficiente stock disponible. Stock actual: " + producto.getStockQuantity());
                    return "redirect:/productos";
                }
                item.setCantidad(item.getCantidad() + 1);
                existe = true;
                break;
            }
        }

        if (!existe) {
            if (producto.getStockQuantity() < 1) {
                redirectAttributes.addFlashAttribute("error", "Producto sin stock disponible");
                return "redirect:/productos";
            }
            carrito.add(new CarritoItem(id, nombre, precio, 1));
        }

        session.setAttribute("carrito", carrito);
        redirectAttributes.addFlashAttribute("success", "Producto agregado al carrito");
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
        /*String errorDinero = (String) session.getAttribute("errorDinero");
        if (errorDinero != null) {
            model.addAttribute("errorDinero", errorDinero);
            session.removeAttribute("errorDinero"); // <- lo borramos para que no se repita
        }*/
        return "checkout"; // checkout.html
    }

    //Aqui verificar metodo de pago vs banco, regresar a /finalizar sino tiene dinero(aqui se escoge la tarjeta a utilizar)
    @Transactional
    @PostMapping("/finalizar")
    public String procesarCompra(@ModelAttribute("checkout") Checkout checkout,
                                 HttpSession session,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        List<CarritoItem> carrito = (List<CarritoItem>) session.getAttribute("carrito");

        if (carrito == null || carrito.isEmpty()) {
            model.addAttribute("error", "Tu carrito está vacío.");
            return "carrito";
        }

        // VALIDAR STOCK ANTES DE PROCESAR LA COMPRA
        for (CarritoItem item : carrito) {
            if (!articuloService.verificarStockDisponible(item.getProductoId(), item.getCantidad())) {
                Optional<Articulo> productoOpt = articuloService.buscarPorId(item.getProductoId());

                String stockDisponible = productoOpt.isPresent()
                        ? String.valueOf(productoOpt.get().getStockQuantity())
                        : "0";

                redirectAttributes.addFlashAttribute("error",
                        "Stock insuficiente para el producto: " + item.getNombre() +
                                ". Disponible: " + stockDisponible);
                return "redirect:/carrito/finalizar";
            }
        }

        BillingMethod billingMethod = billingMethodService.findById(checkout.getMetodoPago());
        BigDecimal total = carrito.stream()
                .map(CarritoItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Descomentar cuando implementes la conexión con el banco
    /*
    if(!bankService.ejecutarTransaccion(billingMethod, total)) {
        redirectAttributes.addFlashAttribute("errorDinero",
            "No se pudo realizar la compra: falta dinero o falló la conexión con el banco");
        return "redirect:/carrito/finalizar";
    }
    */

        // REBAJAR EL STOCK DE CADA PRODUCTO
        try {
            for (CarritoItem item : carrito) {
                articuloService.actualizarStock(item.getProductoId(), item.getCantidad());
            }
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error",
                    "Error al actualizar el inventario: " + e.getMessage());
            return "redirect:/carrito/finalizar";
        }

        // Preparar datos para la confirmación
        // GUARDAR el cardNumber ANTES de enmascararlo
        String cardNumberOriginal = billingMethod.getCardNumber();

        // Crear una copia para mostrar con máscara
        String cardNumberMasked = maskCardNumber(cardNumberOriginal);

        checkout.setCardnumber(cardNumberMasked);  // Enviar la versión enmascarada
        checkout.setCarrito(carrito);
        session.removeAttribute("carrito");

        model.addAttribute("checkout", checkout);
        model.addAttribute("total", total);

        // Aquí puedes crear la orden si lo necesitas
        // orderService.crearOrden(checkout, carrito, total);

        return "confirmacion";
    }

    // Método auxiliar para enmascarar el número de tarjeta
    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 6) {
            return cardNumber;
        }
        String first2 = cardNumber.substring(0, 2);
        String last4 = cardNumber.substring(cardNumber.length() - 4);
        String middle = "*".repeat(cardNumber.length() - 6);
        return first2 + middle + last4;
    }


}
