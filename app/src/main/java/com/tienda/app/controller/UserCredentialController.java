package com.tienda.app.controller;

import com.tienda.app.model.UserCredentialModel;
import com.tienda.app.service.UserCredentialService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controlador encargado de manejar las operaciones relacionadas con
 * las credenciales de usuario: ingresar (login), registrar y cerrar sesión.
 */
@Controller
@RequestMapping("/credenciales") // Ruta base: todas las URLs aquí empiezan con /credenciales
public class UserCredentialController {
    private final UserCredentialService userCredentialService;

    /**
     * Inyección de dependencias de UserCredentialService mediante constructor.
     * Esto permite usar los métodos del servicio para validar, registrar y buscar usuarios.
     */
    public UserCredentialController(UserCredentialService userCredentialService) {
        this.userCredentialService = userCredentialService;
    }

    /**
     * Método GET para mostrar la página de login.
     * - Crea un objeto vacío UserCredentialModel que se enlazará al formulario.
     * - Agrega un título a la vista.
     */
    @GetMapping("/ingresar")
    public String ingresar(Model model) {
        model.addAttribute("title", "Ingresar");
        model.addAttribute("userCredentialModel", new UserCredentialModel());
        return "pagina_ingresar"; // Devuelve la vista thymeleaf pagina_ingresar.html
    }

    /**
     * Método GET para mostrar la página de registro de usuario.
     * - Crea un objeto vacío UserCredentialModel para el formulario.
     * - Define un título para la vista.
     */
    @GetMapping("/registrar")
    public String getRegistrarP(Model model){
        model.addAttribute("title", "Registrar");
        model.addAttribute("userCredentialModel", new UserCredentialModel());
        return "pagina_registrar"; // Devuelve la vista pagina_registrar.html
    }

    /**
     * Método POST para procesar el login.
     * - Recibe las credenciales enviadas desde el formulario.
     * - Valida si existe un usuario con ese email y password en la BD.
     * - Si no existe, muestra error en la vista.
     * - Si existe, guarda al usuario en la sesión y redirige a la página principal.
     */
    @PostMapping("/ingresar")
    public String ingresar(@ModelAttribute UserCredentialModel userCredential, Model model, HttpSession session){

        UserCredentialModel user = userCredentialService.ValidarCredenciales(
                userCredential.getEmail(),
                userCredential.getPasswordHash()
        );

        // Si no se encontró un usuario, se envía mensaje de error a la vista
        if(user == null){
            model.addAttribute("error", "No existe usuario con esa contraseña");
            return "pagina_ingresar";
        }

        // Ocultamos el password antes de enviarlo al frontend por seguridad
        user.setPasswordHash("Oculto");

        // Solo para pruebas: imprime el rol del usuario en consola
        System.out.println(user.getRole());

        // Guardamos el usuario autenticado en la sesión (puede ser usado en el resto de la app)
        session.setAttribute("usuarioLog", user);

        // Redirige a la página principal
        return "redirect:/";
    }

    /**
     * Método POST para registrar un nuevo usuario.
     * - Recibe los datos del formulario de registro.
     * - Valida si el email ya está en uso.
     * - Si ya existe, muestra error.
     * - Si no existe, crea el usuario y muestra mensaje de éxito.
     */
    @PostMapping("/registrar")
    public String registrarUsuario(@ModelAttribute UserCredentialModel userCredential, Model model){

        System.out.println("Solicitud registrar: "  + userCredential);

        // Buscar usuario por email
        UserCredentialModel user = userCredentialService.buscarPorEmail(userCredential.getEmail());

        // Si el email ya existe, se retorna error
        if(user != null){
            model.addAttribute("error", "Email ya registrado en el sistema");
            return "pagina_registrar";
        }

        // Si no existe, se registra el nuevo usuario
        userCredentialService.registrarUser(userCredential.getEmail(), userCredential.getPasswordHash());
        model.addAttribute("exito", "Usuario registrado en el sistema");

        return "pagina_registrar"; // Se queda en la página de registro mostrando el mensaje
    }

    /**
     * Método POST para cerrar sesión.
     * - Invalida toda la sesión (se eliminan todos los atributos, incluido el carrito).
     * - Redirige a la página principal.
     */
    @PostMapping("/logout")
    public String logout(HttpSession session){
        session.invalidate(); // Elimina toda la sesión
        return "redirect:/"; // Vuelve al home
    }
}

