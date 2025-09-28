package com.tienda.app.controller;

import com.tienda.app.model.Articulo;
import com.tienda.app.model.CarritoItem;
import com.tienda.app.service.ArticuloService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Controlador encargado de gestionar las operaciones relacionadas
 * con los artículos y su interacción con el carrito de compras.
 */
@Controller
@RequestMapping("/articulos") // Todas las rutas que empiecen con /articulos serán manejadas aquí
public class ArticuloController {

    private final ArticuloService articuloService;

    /**
     * Inyección de dependencias del servicio de artículos.
     * El servicio se encarga de la lógica de negocio (listar, buscar, etc.).
     */
    public ArticuloController(ArticuloService articuloService) {
        this.articuloService = articuloService;
    }

    /**
     * Método GET que muestra la lista de artículos disponibles.
     * - Llama al servicio para obtener todos los artículos.
     * - Los envía al modelo para ser mostrados en la vista "articulos.html".
     */
    @GetMapping
    public String listarArticulos(Model model) {
        model.addAttribute("articulos", articuloService.listarTodos());
        return "articulos"; // Página thymeleaf con la lista de artículos
    }

    /**
     * Método POST para agregar un artículo al carrito de compras.
     *
     * @param id      Id del artículo que se quiere agregar.
     * @param session Objeto de sesión HTTP, donde se guarda el carrito.
     *
     * Flujo:
     * 1. Busca el artículo en la BD usando su id.
     * 2. Si no existe, redirige a la lista de artículos.
     * 3. Recupera el carrito desde la sesión; si no existe, crea uno nuevo.
     * 4. Recorre el carrito para ver si el artículo ya está agregado:
     *      - Si está, aumenta la cantidad en 1.
     *      - Si no está, lo agrega como nuevo item.
     * 5. Actualiza el carrito en la sesión.
     * 6. Redirige al usuario a la vista del carrito.
     */
    @PostMapping("/agregar")
    public String agregarAlCarrito(@RequestParam Integer id,
                                   HttpSession session) {
        // Buscar artículo por ID, si no se encuentra redirige a la lista
        Articulo articulo = articuloService.buscarPorId(id).orElse(null);
        if (articulo == null) return "redirect:/articulos";

        // Recuperar carrito de la sesión, si no existe se inicializa vacío
        List<CarritoItem> carrito = (List<CarritoItem>) session.getAttribute("carrito");
        if (carrito == null) carrito = new ArrayList<>();

        // Bandera para verificar si el artículo ya está en el carrito
        boolean existe = false;

        // Buscar en el carrito si ya está el artículo
        for (CarritoItem item : carrito) {
            if (item.getProductoId().equals(articulo.getId())) {
                // Si ya está, incrementar la cantidad
                item.setCantidad(item.getCantidad() + 1);
                existe = true;
                break;
            }
        }

        // Si el artículo no existía en el carrito, se agrega como nuevo
        if (!existe) {
            carrito.add(new CarritoItem(
                    articulo.getId(),
                    articulo.getName(),
                    articulo.getPrice(),
                    1 // Cantidad inicial en 1
            ));
        }

        // Guardar carrito actualizado en la sesión
        session.setAttribute("carrito", carrito);

        // Redirigir al carrito de compras
        return "redirect:/carrito";
    }
}
