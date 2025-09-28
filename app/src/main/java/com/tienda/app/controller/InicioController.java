package com.tienda.app.controller;


import com.tienda.app.service.ArticuloService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class InicioController {

    private final ArticuloService articuloService;

    public InicioController(ArticuloService articuloService) {
        this.articuloService = articuloService;
    }

    @GetMapping
    public String home(Model model) {
        model.addAttribute("articulos", articuloService.listarTodos());
        model.addAttribute("title", "Home");
        return "index";
    }
}
