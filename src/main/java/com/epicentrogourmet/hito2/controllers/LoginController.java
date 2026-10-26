package com.epicentrogourmet.hito2.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.epicentrogourmet.hito2.repositories.FestivalRepository;

@Controller
public class LoginController {

    @Autowired
    private FestivalRepository festivalRepository;

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout, Model model) {
        if (error != null) {
            model.addAttribute("mensajeError", "Credenciales inválidas o usuario inactivo. Intente nuevamente.");
        }
        if (logout != null) {
            model.addAttribute("mensajeLogout", "Ha cerrado sesión correctamente.");
        }
        return "login";
    }

    
    @GetMapping("/")
    public String inicioPublico(Model model) {
        model.addAttribute("festivales", festivalRepository.findAll());
        return "publico/festivales";
    }

    
    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("titulo", "Epicentro Gourmet - Panel de Control");
        return "index";
    }
}
