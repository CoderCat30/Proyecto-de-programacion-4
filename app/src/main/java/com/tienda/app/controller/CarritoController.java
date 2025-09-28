package com.tienda.app.controller;

import com.tienda.app.model.CarritoItem;
import com.tienda.app.model.Checkout;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador encargado de manejar todas las operaciones del carrito de compras:
 * ver, agregar, actualizar, eliminar, vaciar y finalizar la compra.
 */
@Controller
@RequestMapping("/carrito") // Todas las rutas inician con /carrito
public class CarritoController {

    /**
     * Método GET para visualizar el carrito de compras.
     * - Obtiene el carrito desde la sesión (si no existe, crea uno vacío).
     * - Calcula el total sumando los subtotales de cada item.
     * - Envía carrito y total a la vista "carrito.html".
     */
    @GetMapping
    public String verCarrito(HttpSession session, Model model) {
        model.addAttribute("tittle","Carrito"); // Pequeño error tipográfico (tittle en lugar de title)

        List<CarritoItem> carrito = (List<CarritoItem>) session.getAttribute("carrito");
        if (carrito == null) carrito = new ArrayList<>();

        // Calcula el total del carrito sumando cada subtotal
        BigDecimal total = carrito.stream()
                .map(CarritoItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("title", "Carrito");
        model.addAttribute("carrito", carrito);
        model.addAttribute("total", total);
        return "carrito"; // Muestra carrito.html
    }

    /**
     * Método POST para agregar un producto al carrito.
     * - Recibe datos del producto desde el formulario: id, nombre y precio.
     * - Si el carrito no existe en sesión, lo crea.
     * - Si el producto ya está en el carrito, aumenta su cantidad.
     * - Si no está, lo agrega como nuevo item.
     */
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
                // Si ya existe, solo aumentar la cantidad
                item.setCantidad(item.getCantidad() + 1);
                existe = true;
                break;
            }
        }

        // Si no existe en el carrito, lo agrega como nuevo
        if (!existe) {
            carrito.add(new CarritoItem(id, nombre, precio, 1));
        }

        // Actualiza carrito en sesión
        session.setAttribute("carrito", carrito);
        return "redirect:/carrito";
    }

    /**
     * Método POST para actualizar la cantidad de un producto en el carrito.
     * - Si la cantidad es mayor a 0, actualiza el valor.
     * - Si la cantidad es 0 o negativa, elimina el producto del carrito.
     */
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
                        return false; // mantener el item
                    } else {
                        return true; // eliminarlo
                    }
                }
                return false;
            });
            session.setAttribute("carrito", carrito);
        }
        return "redirect:/carrito";
    }

    /**
     * Método POST para eliminar un producto del carrito.
     * - Busca por id y elimina el producto si existe.
     */
    @PostMapping("/eliminar")
    public String eliminarDelCarrito(@RequestParam Integer id, HttpSession session) {
        List<CarritoItem> carrito = (List<CarritoItem>) session.getAttribute("carrito");
        if (carrito != null) {
            carrito.removeIf(item -> item.getProductoId().equals(id));
            session.setAttribute("carrito", carrito);
        }
        return "redirect:/carrito";
    }

    /**
     * Método POST para vaciar completamente el carrito.
     * - Elimina el atributo "carrito" de la sesión.
     */
    @PostMapping("/vaciar")
    public String vaciarCarrito(HttpSession session) {
        session.removeAttribute("carrito");
        return "redirect:/carrito";
    }

    /**
     * Método GET que muestra el formulario de checkout (finalizar compra).
     * - Si el carrito está vacío, devuelve error y vuelve a la vista carrito.
     * - Si hay productos, carga el formulario de checkout con los datos.
     */
    @GetMapping("/finalizar")
    public String mostrarFormularioFinalizar(HttpSession session, Model model) {
        List<CarritoItem> carrito = (List<CarritoItem>) session.getAttribute("carrito");

        if (carrito == null || carrito.isEmpty()) {
            model.addAttribute("error", "Tu carrito está vacío.");
            return "carrito"; // vuelve a carrito si está vacío
        }

        model.addAttribute("carrito", carrito);
        model.addAttribute("checkout", new Checkout()); // objeto vacío para enlazar con el form
        return "checkout"; // checkout.html
    }

    /**
     * Método POST que procesa la compra en el checkout.
     * - Verifica que el carrito no esté vacío.
     * - Calcula el total de la compra.
     * - Limpia el carrito de la sesión (simulando que ya se compró).
     * - Devuelve la vista de confirmación con los datos del checkout y el total.
     */
    @PostMapping("/finalizar")
    public String procesarCompra(@ModelAttribute("checkout") Checkout checkout,
                                 HttpSession session,
                                 Model model) {
        List<CarritoItem> carrito = (List<CarritoItem>) session.getAttribute("carrito");

        if (carrito == null || carrito.isEmpty()) {
            model.addAttribute("error", "Tu carrito está vacío.");
            return "carrito";
        }

        // Calcula el total de la compra
        BigDecimal total = carrito.stream()
                .map(CarritoItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Vacía el carrito después de la compra
        session.removeAttribute("carrito");

        // Envía datos a la vista de confirmación
        model.addAttribute("checkout", checkout);
        model.addAttribute("total", total);

        return "confirmacion"; // confirmacion.html
    }
}
