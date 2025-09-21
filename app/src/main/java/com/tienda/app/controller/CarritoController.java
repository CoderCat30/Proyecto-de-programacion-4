package com.tienda.app.controller;

import com.tienda.app.model.CarritoItem;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/carrito")
public class CarritoController {

    @GetMapping
    public String verCarrito(HttpSession session, Model model) {
        List<CarritoItem> carrito = (List<CarritoItem>) session.getAttribute("carrito");
        if (carrito == null) carrito = new ArrayList<>();

        double total = carrito.stream().mapToDouble(CarritoItem::getSubtotal).sum();

        model.addAttribute("carrito", carrito);
        model.addAttribute("total", total);
        return "carrito"; // carrito.html en templates
    }

    @PostMapping("/agregar")
    public String agregarAlCarrito(@RequestParam Integer id,
                                   @RequestParam String nombre,
                                   @RequestParam double precio,
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
}

