package com.tienda.app.controller;

import com.tienda.app.service.ArticuloService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controlador principal que maneja la página de inicio de la tienda.
 * - Muestra el listado de artículos disponibles en la página principal.
 */
@Controller
@RequestMapping("/")
public class InicioController {

    // Servicio que maneja la lógica de negocio relacionada con los artículos
    private final ArticuloService articuloService;

    // Inyección del servicio mediante constructor
    public InicioController(ArticuloService articuloService) {
        this.articuloService = articuloService;
    }

    /**
     * Método GET para la ruta raíz "/".
     *
     * @param model Modelo usado para enviar datos a la vista Thymeleaf.
     * @return la vista "index.html".
     *
     * Flujo:
     * 1. Obtiene todos los artículos desde el servicio.
     * 2. Los agrega al modelo con el atributo "articulos".
     * 3. También agrega el título de la página.
     * 4. Retorna la plantilla "index".
     */
    @GetMapping
    public String home(Model model) {
        // Cargar todos los artículos disponibles
        model.addAttribute("articulos", articuloService.listarTodos());

        // Título que se usará en la vista
        model.addAttribute("title", "Home");

        // Devolver la vista index.html
        return "index";
    }
}

