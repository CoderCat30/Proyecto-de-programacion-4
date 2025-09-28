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

@Controller
@RequestMapping("/carrito")
public class CarritoController {

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

    @GetMapping("/finalizar")
    public String mostrarFormularioFinalizar(HttpSession session, Model model) {
        List<CarritoItem> carrito = (List<CarritoItem>) session.getAttribute("carrito");

        if (carrito == null || carrito.isEmpty()) {
            model.addAttribute("error", "Tu carrito está vacío.");
            return "carrito"; // vuelve a carrito si está vacío
        }

        model.addAttribute("carrito", carrito);
        model.addAttribute("checkout", new Checkout());
        return "checkout"; // checkout.html
    }
    @PostMapping("/finalizar")
    public String procesarCompra(@ModelAttribute("checkout") Checkout checkout,
                                 HttpSession session,
                                 Model model) {
        List<CarritoItem> carrito = (List<CarritoItem>) session.getAttribute("carrito");

        if (carrito == null || carrito.isEmpty()) {
            model.addAttribute("error", "Tu carrito está vacío.");
            return "carrito";
        }

        BigDecimal total = carrito.stream().map(CarritoItem::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);

        session.removeAttribute("carrito");

        model.addAttribute("checkout", checkout);
        model.addAttribute("total", total);

        return "confirmacion";
    }


}
