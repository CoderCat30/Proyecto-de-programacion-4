package com.tienda.app.controller;

import com.tienda.app.model.Articulo;
import com.tienda.app.model.CarritoItem;
import com.tienda.app.service.ArticuloService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/articulos")
public class ArticuloController {

    private final ArticuloService articuloService;

    public ArticuloController(ArticuloService articuloService) {
        this.articuloService = articuloService;
    }


    @GetMapping
    public String listarArticulos(Model model) {
        model.addAttribute("articulos", articuloService.listarTodos());
        return "articulos";
    }

    // Agregar al carrito
    @PostMapping("/agregar")
    public String agregarAlCarrito(@RequestParam Integer id,
                                   HttpSession session) {
        Articulo articulo = articuloService.buscarPorId(id).orElse(null);
        if (articulo == null) return "redirect:/articulos";

        List<CarritoItem> carrito = (List<CarritoItem>) session.getAttribute("carrito");
        if (carrito == null) carrito = new ArrayList<>();

        boolean existe = false;
        for (CarritoItem item : carrito) {
            if (item.getProductoId().equals(articulo.getId())) {
                item.setCantidad(item.getCantidad() + 1);
                existe = true;
                break;
            }
        }

        if (!existe) {
            carrito.add(new CarritoItem(
                    articulo.getId(),
                    articulo.getName(),
                    articulo.getPrice(),
                    1
            ));
        }

        session.setAttribute("carrito", carrito);
        return "redirect:/carrito";
    }
}
