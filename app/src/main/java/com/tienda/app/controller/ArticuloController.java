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

    // Constructor: inyecta el servicio de Articulos en el controlador
    public ArticuloController(ArticuloService articuloService) {
        this.articuloService = articuloService;
    }

    /**
     * Método GET para mostrar el listado de artículos.
     * - Recupera todos los artículos disponibles usando el servicio.
     * - Agrega la lista al modelo para que se pueda mostrar en la vista.
     * - Retorna la vista "articulos.html".
     */
    @GetMapping
    public String listarArticulos(Model model) {
        model.addAttribute("articulos", articuloService.listarTodos());
        return "pokedex";
    }

    /**
     * Método POST para agregar un artículo al carrito de compras.
     * @param id        identificador del artículo que se quiere agregar.
     * @param session   sesión HTTP donde se guarda la lista de artículos del carrito.
     *
     * Flujo:
     * 1. Busca el artículo por su ID en la base de datos.
     * 2. Si no existe, redirige nuevamente a la lista de artículos.
     * 3. Recupera (o crea) la lista de productos del carrito desde la sesión.
     * 4. Recorre el carrito para verificar si el artículo ya existe:
     *    - Si ya está, simplemente incrementa la cantidad.
     *    - Si no está, crea un nuevo CarritoItem con cantidad 1 y lo agrega.
     * 5. Guarda nuevamente el carrito en la sesión.
     * 6. Redirige a la vista del carrito.
     */
    @PostMapping("/agregar")
    public String agregarAlCarrito(@RequestParam Integer id,
                                   HttpSession session) {
        // Buscar artículo por ID
        Articulo articulo = articuloService.buscarPorId(id).orElse(null);
        if (articulo == null) return "redirect:/articulos";

        // Recuperar el carrito de la sesión, si no existe se crea uno nuevo
        List<CarritoItem> carrito = (List<CarritoItem>) session.getAttribute("carrito");
        if (carrito == null) carrito = new ArrayList<>();

        boolean existe = false;
        // Verificar si el artículo ya está en el carrito
        for (CarritoItem item : carrito) {
            if (item.getProductoId().equals(articulo.getId())) {
                // Si ya existe, aumentar cantidad
                item.setCantidad(item.getCantidad() + 1);
                existe = true;
                break;
            }
        }

        // Si no estaba en el carrito, se agrega como nuevo
        if (!existe) {
            carrito.add(new CarritoItem(
                    articulo.getId(),
                    articulo.getName(),
                    articulo.getPrice(),
                    1
            ));
        }

        // Guardar el carrito actualizado en la sesión
        session.setAttribute("carrito", carrito);

        // Redirigir a la vista del carrito
        return "redirect:/carrito";
    }
}
