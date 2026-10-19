package com.epicentrogourmet.hito2.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.epicentrogourmet.hito2.entity.Usuario;
import com.epicentrogourmet.hito2.repositories.UsuarioRepository;
import java.util.Optional;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout,
                        Model model) {
        
        if (error != null) {
            model.addAttribute("mensajeError", "Credenciales inválidas o usuario inactivo. Intente nuevamente.");
        }

        if (logout != null) {
            model.addAttribute("mensajeLogout", "Ha cerrado sesión correctamente.");
        }

        return "login"; 
    }

    
    @PostMapping("/login")
    public String procesarLogin(@RequestParam("username") String username,
                                @RequestParam("password") String password,
                                HttpSession session,
                                Model model) {
        
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUserName(username);

        if (usuarioOpt.isPresent() && usuarioOpt.get().isActivo() && usuarioOpt.get().getPassword().equals(password)) {
            Usuario usuarioLogueado = usuarioOpt.get();
            
           
            session.setAttribute("usuarioLogueado", usuarioLogueado);
            
            return "redirect:/home"; 
        }

       
        return "redirect:/login?error=true";
    }

    
    @GetMapping({"/", "/home"})
    public String home(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        
        if (usuario == null) {
            return "redirect:/login";
        }

        model.addAttribute("titulo", "Epicentro Gourmet - Panel de Control");
        model.addAttribute("usuario", usuario); 
        
        return "index";
    }

    
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); 
        return "redirect:/login?logout=true";
    }
}