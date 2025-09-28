package com.tienda.app.controller;

import com.tienda.app.service.ArticuloService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controlador encargado de manejar la página de inicio de la aplicación.
 *
 * Su objetivo principal es mostrar la página principal ("/") cargando
 * los artículos disponibles desde el servicio.
 */
@Controller
@RequestMapping("/") // Todas las peticiones que apunten a la raíz "/" entran por aquí
public class InicioController {

    private final ArticuloService articuloService;

    /**
     * Constructor con inyección de dependencias.
     * Recibe el servicio de artículos, que se encargará de la lógica de negocio
     * (obtener todos los artículos de la base de datos).
     */
    public InicioController(ArticuloService articuloService) {
        this.articuloService = articuloService;
    }

    /**
     * Método GET que se ejecuta cuando un usuario accede al inicio de la página.
     *
     * Flujo:
     * 1. Obtiene la lista de artículos llamando a articuloService.listarTodos().
     * 2. Agrega esa lista al modelo bajo el atributo "articulos".
     * 3. Define un atributo "title" con el valor "Home" (útil para encabezados dinámicos).
     * 4. Retorna la vista "index.html", que será renderizada por Thymeleaf.
     *
     * @param model Objeto usado para pasar datos a la vista.
     * @return la página index.html (vista principal del sistema).
     */
    @GetMapping
    public String home(Model model) {
        // Se envía la lista de artículos para mostrarlos en la página principal
        model.addAttribute("articulos", articuloService.listarTodos());

        // Se agrega un título a la página
        model.addAttribute("title", "Home");

        // Se retorna el nombre de la vista index.html
        return "index";
    }
}

