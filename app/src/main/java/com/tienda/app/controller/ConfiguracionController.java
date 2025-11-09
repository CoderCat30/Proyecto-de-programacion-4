package com.tienda.app.controller;

import com.tienda.app.model.Configuracion;
import com.tienda.app.model.UserCredentialModel;
import com.tienda.app.service.ConfiguracionService;
import com.tienda.app.service.UserCredentialService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/config")
public class ConfiguracionController {

    private final ConfiguracionService configService;
    private final UserCredentialService userCredentialService;


    public ConfiguracionController(ConfiguracionService configService, UserCredentialService userCredentialService) {
        this.configService = configService;
        this.userCredentialService = userCredentialService;
    }

    @GetMapping("/")
    public String principal(Model model) {
        Configuracion configuracion = configService.obtenerConfiguracion();
        List<UserCredentialModel> users = userCredentialService.findAll();
        model.addAttribute("users", users);
        model.addAttribute("configuracion", configuracion);
        return "pagina_configuracion";
    }

    @GetMapping("/tiempo-sesion")
    @ResponseBody
    public int getTiempoSesion(HttpSession session) {
        Configuracion configuracion = configService.obtenerConfiguracion();
        return configuracion.getValorSegundos();
    }

    @PostMapping("/tiempo-sesion")
    public String cambiarTiempoSesion(
            @RequestParam int minutos,
            @RequestParam int segundos) {

        configService.actualizarTiempoSesion(minutos, segundos);
        return "redirect:/admin/config/";
    }

    @PostMapping("/eliminaruser/{id}")
    public String eliminar(@PathVariable int id) {
        userCredentialService.eliminarUsuario(id);
        return "redirect:/admin/config/";
    }

    @PostMapping("/cambiarrol/{id}")
    public String cambiarRol(@PathVariable int id, @RequestParam String rol) {
        userCredentialService.actualizarRol(id, rol);
        return "redirect:/admin/config/";
    }
}
