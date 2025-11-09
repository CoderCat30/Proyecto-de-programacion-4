package com.tienda.app.controller;

import com.tienda.app.model.UserCredentialModel;
import com.tienda.app.service.UserCredentialService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Controlador encargado de gestionar el login, registro y logout de usuarios.
 *
 * Flujo principal:
 * - GET /credenciales/ingresar → muestra formulario de login
 * - POST /credenciales/ingresar → valida credenciales e inicia sesión
 * - GET /credenciales/registrar → muestra formulario de registro
 * - POST /credenciales/registrar → registra nuevo usuario
 * - POST /credenciales/logout → cierra sesión
 */
@Controller
@RequestMapping("/credenciales")
public class UserCredentialController {

    private final UserCredentialService userCredentialService;
    private final BCryptPasswordEncoder passwordEncoder;

    // Inyección del servicio que maneja la lógica de usuarios
    public UserCredentialController(UserCredentialService userCredentialService,
                                    BCryptPasswordEncoder passwordEncoder) {
        this.userCredentialService = userCredentialService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * GET /credenciales/ingresar
     * Muestra la página de login con un formulario vacío.
     *
     * @param model modelo usado para pasar datos a la vista
     * @return la vista "pagina_ingresar"
     */
    @GetMapping("/ingresar")
    public String ingresar(Model model) {
        model.addAttribute("title", "Ingresar");
        model.addAttribute("userCredentialModel", new UserCredentialModel());
        return "pagina_ingresar";
    }

    /**
     * GET /credenciales/registrar
     * Muestra la página de registro con un formulario vacío.
     *
     * @param model modelo usado para pasar datos a la vista
     * @return la vista "pagina_registrar"
     */
    @GetMapping("/registrar")
    public String getRegistrarP(Model model) {
        model.addAttribute("title", "Registrar");
        model.addAttribute("userCredentialModel", new UserCredentialModel());
        return "pagina_registrar";
    }

    /**
     * POST /credenciales/ingresar
     * Procesa el login del usuario validando sus credenciales.
     *
     * @param userCredential objeto con email y password recibidos del formulario
     * @param model modelo para pasar mensajes de error en caso de credenciales inválidas
     * @param session sesión HTTP donde se guarda el usuario logueado
     * @return redirige a "/" si el login fue exitoso, o vuelve a "pagina_ingresar" con error
     */
    @PostMapping("/ingresar")
    public String ingresar(@ModelAttribute UserCredentialModel userCredential,
                           Model model,
                           HttpSession session) {

        // Validar credenciales en la base de datos
        UserCredentialModel user = userCredentialService.ValidarCredenciales(
                userCredential.getEmail(),
                userCredential.getPasswordHash()
        );

        // Si no existe el usuario con esa combinación → error
        if (user == null) {
            model.addAttribute("error", "No existe usuario con esa contraseña");
            return "pagina_ingresar";
        }

        // Por seguridad, no exponer el password en sesión
        user.setPasswordHash("Oculto");

        // Debug en consola: rol del usuario (puedes quitarlo en producción)
        System.out.println(user.getRole());

        // Guardar usuario logueado en la sesión
        session.setAttribute("usuarioLog", user);

        // Redirigir a la página principal
        return "redirect:/";
    }

    /**
     * POST /credenciales/registrar
     * Procesa el registro de un nuevo usuario.
     *
     * @param userCredential objeto con email y password ingresados
     * @param model modelo para pasar mensajes de error o éxito
     * @return la vista "pagina_registrar" (misma página con mensaje correspondiente)
     */
    @PostMapping("/registrar")
    public String registrarUsuario(@ModelAttribute UserCredentialModel userCredential,
                                   Model model) {

        System.out.println("solicitud registrar: " + userCredential);

        // Verificar si ya existe un usuario con ese email
        UserCredentialModel user = userCredentialService.buscarPorEmail(userCredential.getEmail());
        if (user != null) {
            model.addAttribute("error", "Email ya registrado en el sistema");
            return "pagina_registrar";
        }

        String hashedPassword = passwordEncoder.encode(userCredential.getPasswordHash());
        userCredential.setPasswordHash(hashedPassword);
        // Registrar nuevo usuario
        userCredentialService.registrarUser(
                userCredential.getEmail(),
                userCredential.getPasswordHash()
        );

        model.addAttribute("exito", "Usuario registrado en el sistema");
        return "pagina_registrar";
    }

    /**
     * POST /credenciales/logout
     * Cierra la sesión actual del usuario.
     *
     * @param session sesión HTTP que se invalidará (se borran todos los atributos, incluido el carrito)
     * @return redirige a la página principal "/"
     */
    @PostMapping("/logout")
    public String logout(HttpSession session) {
        // Elimina todos los atributos de la sesión (incluye carrito, usuario, etc.)
        session.invalidate();
        return "redirect:/";
    }

    //metodo para la renovacion de la sesion
    @GetMapping("/refresh")
    @ResponseBody
    public void refreshSession(HttpSession session) {
        session.setMaxInactiveInterval(session.getMaxInactiveInterval());
    }
}
