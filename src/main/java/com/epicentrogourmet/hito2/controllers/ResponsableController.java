package com.epicentrogourmet.hito2.controllers;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.epicentrogourmet.hito2.dtos.PlatoDTO;
import com.epicentrogourmet.hito2.entity.Empleado;
import com.epicentrogourmet.hito2.entity.UnidadDeVenta;
import com.epicentrogourmet.hito2.entity.Usuario;
import com.epicentrogourmet.hito2.repositories.EmpleadoRepository;
import com.epicentrogourmet.hito2.repositories.UsuarioRepository;
import com.epicentrogourmet.hito2.services.IUnidadDeVentaService;

@Controller
@RequestMapping("/responsable")
public class ResponsableController {

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private IUnidadDeVentaService unidadDeVentaService;

    @GetMapping("/plato-estrella")
    public String verPlatoEstrella(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        // 1. Buscamos al usuario logueado
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUserName(userDetails.getUsername());
        if (usuarioOpt.isEmpty()) return "redirect:/home?error=true";

        // 2. Buscamos su perfil de empleado y su unidad
        Optional<Empleado> empOpt = empleadoRepository.findByUsuario_IdUsuario(usuarioOpt.get().getIdUsuario());
        if (empOpt.isEmpty() || empOpt.get().getUnidadAsignada() == null) {
            return "redirect:/home?errorSinUnidad=true";
        }

        Empleado responsable = empOpt.get();
        UnidadDeVenta unidad = responsable.getUnidadAsignada();
        Long idFestival = unidad.getFestival().getIdFestival();

        // 3. Usamos el servicio que ya existe en el proyecto para calcular el plato estrella
        PlatoDTO platoEstrella = unidadDeVentaService.obtenerPlatoEstrella(unidad.getIdUnidadDeVenta(), idFestival);

        model.addAttribute("titulo", "Plato Estrella");
        model.addAttribute("unidad", unidad);
        model.addAttribute("plato", platoEstrella);
        
        return "responsable/plato-estrella";
    }
}