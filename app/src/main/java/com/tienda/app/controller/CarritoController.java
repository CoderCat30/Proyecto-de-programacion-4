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

/**
 * Controlador del flujo de carrito de compras:
 * - Visualizar el carrito
 * - Agregar/actualizar/eliminar/vaciar ítems
 * - Flujo de checkout (finalizar compra)
 *
 * Notas:
 * - El carrito se almacena en la sesión bajo la clave "carrito" como List<CarritoItem>.
 * - Se valida stock antes de confirmar la compra.
 * - Se prepara la info de checkout y se enmascara el número de tarjeta para la vista.
 */
@Controller
@RequestMapping("/carrito")
public class CarritoController {

    // Servicios usados para validar stock, obtener datos del usuario, métodos de pago, etc.
    private final UserInformationService userInformationService;
    private final UserAddressService userAddressService;
    private final BillingMethodService billingMethodService;
    private final BankService bankService;
    private final ArticuloService articuloService;

    /**
     * Constructor con inyección de dependencias (Spring las provee).
     */
    public CarritoController(UserInformationService userInformationService, UserAddressService userAddressService,
                             BillingMethodService billingMethodService, BankService bankService, ArticuloService articuloService) {
        this.userInformationService = userInformationService;
        this.userAddressService = userAddressService;
        this.billingMethodService = billingMethodService;
        this.bankService = bankService;
        this.articuloService = articuloService;
    }

    /**
     * GET /carrito
     * Muestra el contenido actual del carrito y calcula el total.
     *
     * @param session Sesión HTTP desde donde se recupera la lista "carrito".
     * @param model   Modelo para pasar datos a la vista (lista y total).
     * @return nombre de la vista "carrito".
     */
    @GetMapping
    public String verCarrito(HttpSession session, Model model) {
        // Título para la vista (ten en cuenta que abajo también usas "title")
        model.addAttribute("tittle","Carrito");

        // Recuperar carrito de la sesión; si no existe, inicializar lista vacía
        @SuppressWarnings("unchecked")
        List<CarritoItem> carrito = (List<CarritoItem>) session.getAttribute("carrito");
        if (carrito == null) carrito = new ArrayList<>();

        // Calcular el total sumando subtotales con BigDecimal para precisión monetaria
        BigDecimal total = carrito.stream()
                .map(CarritoItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("title", "Carrito");
        model.addAttribute("carrito", carrito);
        model.addAttribute("total", total);

        // Renderiza carrito.html
        return "carrito";
    }

    /**
     * POST /carrito/agregar
     * Agrega un producto al carrito (o incrementa su cantidad si ya existe),
     * validando existencia y stock.
     *
     * @param id                 ID del producto a agregar.
     * @param nombre             Nombre del producto (usado para construir CarritoItem si es nuevo en el carrito).
     * @param precio             Precio unitario (BigDecimal) para el ítem agregado.
     * @param session            Sesión HTTP donde se almacena el carrito.
     * @param redirectAttributes Mensajes flash (éxito/error) tras el redirect.
     * @return redirect al carrito o a /productos si hay errores.
     */
    @PostMapping("/agregar")
    public String agregarAlCarrito(@RequestParam Integer id,
                                   @RequestParam String nombre,
                                   @RequestParam BigDecimal precio,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {

        // 1) Verificar que el producto exista
        Optional<Articulo> productoOpt = articuloService.buscarPorId(id);
        if (productoOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Producto no encontrado");
            return "redirect:/productos";
        }

        Articulo producto = productoOpt.get();

        // 2) Recuperar carrito de sesión o crear uno nuevo
        @SuppressWarnings("unchecked")
        List<CarritoItem> carrito = (List<CarritoItem>) session.getAttribute("carrito");
        if (carrito == null) carrito = new ArrayList<>();

        // 3) Si ya existe en el carrito, incrementar cantidad validando stock
        boolean existe = false;
        for (CarritoItem item : carrito) {
            if (item.getProductoId().equals(id)) {
                // Validación: no superar el stock disponible
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

        // 4) Si no existía, agregarlo con cantidad 1 (validando stock mínimo)
        if (!existe) {
            if (producto.getStockQuantity() < 1) {
                redirectAttributes.addFlashAttribute("error", "Producto sin stock disponible");
                return "redirect:/productos";
            }
            carrito.add(new CarritoItem(id, nombre, precio, 1));
        }

        // 5) Persistir el carrito actualizado en sesión y notificar éxito
        session.setAttribute("carrito", carrito);
        redirectAttributes.addFlashAttribute("success", "Producto agregado al carrito");
        return "redirect:/carrito";
    }

    /**
     * POST /carrito/actualizar
     * Actualiza la cantidad de un ítem específico del carrito. Si la cantidad es 0 o negativa,
     * se elimina el ítem.
     *
     * @param id       ID del producto a modificar.
     * @param cantidad Nueva cantidad (si <= 0, se elimina).
     * @param session  Sesión HTTP donde reside el carrito.
     * @return redirect al carrito.
     */
    @PostMapping("/actualizar")
    public String actualizarCantidad(@RequestParam Integer id,
                                     @RequestParam int cantidad,
                                     HttpSession session) {
        @SuppressWarnings("unchecked")
        List<CarritoItem> carrito = (List<CarritoItem>) session.getAttribute("carrito");
        if (carrito != null) {
            // removeIf con lógica: si coincide el producto, actualiza o elimina según cantidad
            carrito.removeIf(item -> {
                if (item.getProductoId().equals(id)) {
                    if (cantidad > 0) {
                        item.setCantidad(cantidad);
                        return false; // no eliminar
                    } else {
                        return true;  // eliminar
                    }
                }
                return false;
            });
            session.setAttribute("carrito", carrito);
        }
        return "redirect:/carrito";
    }

    /**
     * POST /carrito/eliminar
     * Elimina un producto específico del carrito (independiente de su cantidad).
     *
     * @param id      ID del producto a eliminar.
     * @param session Sesión HTTP con la lista del carrito.
     * @return redirect al carrito.
     */
    @PostMapping("/eliminar")
    public String eliminarDelCarrito(@RequestParam Integer id, HttpSession session) {
        @SuppressWarnings("unchecked")
        List<CarritoItem> carrito = (List<CarritoItem>) session.getAttribute("carrito");
        if (carrito != null) {
            carrito.removeIf(item -> item.getProductoId().equals(id));
            session.setAttribute("carrito", carrito);
        }
        return "redirect:/carrito";
    }

    /**
     * POST /carrito/vaciar
     * Vacía por completo el carrito del usuario eliminando el atributo de sesión.
     *
     * @param session Sesión HTTP del usuario.
     * @return redirect al carrito (mostrará vacío).
     */
    @PostMapping("/vaciar")
    public String vaciarCarrito(HttpSession session) {
        session.removeAttribute("carrito");
        return "redirect:/carrito";
    }

    /**
     * GET /carrito/finalizar
     * Muestra el formulario de checkout (datos de envío/facturación/pago) si:
     * - Hay items en el carrito
     * - El usuario ha iniciado sesión
     * - El usuario tiene información personal (y opcionalmente dirección) creada
     *
     * @param session Sesión HTTP para obtener carrito y usuario logueado.
     * @param model   Modelo con datos del usuario, carrito y lista de métodos de pago.
     * @return vista "checkout" o redirect si faltan precondiciones.
     */
    @GetMapping("/finalizar")
    public String mostrarFormularioFinalizar(HttpSession session, Model model) {
        @SuppressWarnings("unchecked")
        List<CarritoItem> carrito = (List<CarritoItem>) session.getAttribute("carrito");

        // Validación: carrito no vacío
        if (carrito == null || carrito.isEmpty()) {
            model.addAttribute("error", "Tu carrito está vacío.");
            return "carrito"; // vuelve a carrito si está vacío
        }

        // Validación: usuario debe haber iniciado sesión
        if (session.getAttribute("usuarioLog") == null) {
            return "redirect:/credenciales/ingresar";
        }

        // Cargar información del usuario para prefilling del formulario de checkout
        UserCredentialModel userCredentialModel = (UserCredentialModel) session.getAttribute("usuarioLog");
        UserInformation userInformation = userInformationService.getUserInformationById(userCredentialModel.getId());
        UserAddress userAddress = userAddressService.getUserAddressByUserId(userCredentialModel.getId());

        // Si no existe información personal, obligar a completarla antes de comprar
        if (userInformation == null) {
            return "redirect:/pagina_personal/pagina_datosPersonales";
        }

        // Métodos de pago disponibles para el usuario
        List<BillingMethod> billingMethodList = billingMethodService.findAllByUserId(userInformation.getId());

        // Poblamos el modelo para la vista de checkout
        model.addAttribute("usuarioInfo", userInformation);
        model.addAttribute("usuarioAddr", userAddress);
        model.addAttribute("carrito", carrito);
        model.addAttribute("checkout", new Checkout());
        model.addAttribute("billingMethodList", billingMethodList);

        // Si quisieras mostrar errores de intentos previos, aquí iría la lógica con flash attrs / sesión
        return "checkout"; // checkout.html
    }

    /**
     * POST /carrito/finalizar
     * Procesa la compra:
     * - Revalida que haya carrito
     * - Verifica stock para cada ítem (pre-commit)
     * - (Opcional) Ejecuta la transacción bancaria
     * - Descuenta stock de los artículos
     * - Prepara los datos para la vista de confirmación
     *
     * @param checkout            DTO con datos del checkout (método de pago seleccionado, etc.).
     * @param session             Sesión HTTP con el carrito.
     * @param model               Modelo para pasar datos a la vista final.
     * @param redirectAttributes  Mensajes flash para errores y redirects.
     * @return vista "confirmacion" o redirect si falla alguna validación/proceso.
     */
    @Transactional
    @PostMapping("/finalizar")
    public String procesarCompra(@ModelAttribute("checkout") Checkout checkout,
                                 HttpSession session,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        @SuppressWarnings("unchecked")
        List<CarritoItem> carrito = (List<CarritoItem>) session.getAttribute("carrito");

        // 1) Validar que exista carrito
        if (carrito == null || carrito.isEmpty()) {
            model.addAttribute("error", "Tu carrito está vacío.");
            return "carrito";
        }

        // 2) VALIDAR STOCK ANTES DE PROCESAR LA COMPRA (fail-fast por producto)
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

        // 3) Obtener método de pago seleccionado y calcular total
        BillingMethod billingMethod = billingMethodService.findById(checkout.getMetodoPago());
        BigDecimal total = carrito.stream()
                .map(CarritoItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 4) (Opcional) Ejecutar transacción bancaria real
        /*
        if(!bankService.ejecutarTransaccion(billingMethod, total)) {
            redirectAttributes.addFlashAttribute("errorDinero",
                "No se pudo realizar la compra: falta dinero o falló la conexión con el banco");
            return "redirect:/carrito/finalizar";
        }
        */

        // 5) Descontar stock de cada producto (la anotación @Transactional garantiza atomicidad)
        try {
            for (CarritoItem item : carrito) {
                articuloService.actualizarStock(item.getProductoId(), item.getCantidad());
            }
        } catch (RuntimeException e) {
            // Si hay error, la transacción se revierte y redirigimos con mensaje
            redirectAttributes.addFlashAttribute("error",
                    "Error al actualizar el inventario: " + e.getMessage());
            return "redirect:/carrito/finalizar";
        }

        // 6) Preparar datos para la confirmación
        // Guardar número original para enmascararlo (nunca mostrar completo)
        String cardNumberOriginal = billingMethod.getCardNumber();
        String cardNumberMasked = maskCardNumber(cardNumberOriginal);

        checkout.setCardnumber(cardNumberMasked);     // en vista solo se mostrará enmascarado
        checkout.setCarrito(carrito);                 // adjuntar carrito comprado
        session.removeAttribute("carrito");           // limpiar carrito tras compra

        model.addAttribute("checkout", checkout);
        model.addAttribute("total", total);

        // Aquí podría invocarse un servicio de órdenes/pedidos si existiera (persistencia de la compra)
        // orderService.crearOrden(checkout, carrito, total);

        return "confirmacion";
    }

    /**
     * Enmascara un número de tarjeta dejando visibles solo los 2 primeros y 4 últimos dígitos.
     * Ejemplo: 411111******1111
     *
     * @param cardNumber número de tarjeta sin enmascarar.
     * @return número de tarjeta enmascarado o el original si es nulo o demasiado corto.
     */
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
