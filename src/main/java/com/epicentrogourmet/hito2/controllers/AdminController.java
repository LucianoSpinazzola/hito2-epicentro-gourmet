package com.epicentrogourmet.hito2.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.epicentrogourmet.hito2.dtos.RegistroUnidadDTO;
import com.epicentrogourmet.hito2.entity.*;
import com.epicentrogourmet.hito2.repositories.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired private FestivalRepository festivalRepository;
    @Autowired private UnidadDeVentaRepository unidadDeVentaRepository;
    @Autowired private EmpleadoRepository empleadoRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private RolRepository rolRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    // --- FESTIVALES ---
    @GetMapping("/festivales/nuevo")
    public String mostrarFormularioFestival(Model model) {
        model.addAttribute("festival", new Festival());
        return "admin/festivales/formulario";
    }

    @PostMapping("/festivales/nuevo")
    public String guardarFestival(@ModelAttribute Festival festival) {
        festivalRepository.save(festival);
        return "redirect:/home?festivalCreado=true";
    }

    // --- UNIDADES Y RESPONSABLES ---
    @GetMapping("/unidades/nuevo")
    public String mostrarFormularioUnidad(Model model) {
        model.addAttribute("registroForm", new RegistroUnidadDTO());
        model.addAttribute("festivales", festivalRepository.findAll());
        return "admin/unidades/formulario";
    }

    @PostMapping("/unidades/nuevo")
    public String guardarUnidadYResponsable(@ModelAttribute("registroForm") RegistroUnidadDTO form, Model model) {
        try {
            if (usuarioRepository.findByUserName(form.getEmailResponsable()).isPresent()) {
                model.addAttribute("mensajeError", "El email del responsable ya está registrado.");
                model.addAttribute("festivales", festivalRepository.findAll());
                return "admin/unidades/formulario";
            }

            // 1. Crear Usuario (Rol RESPONSABLE)
            Rol rolResp = rolRepository.findByNombreRol("RESPONSABLE")
                    .orElseThrow(() -> new RuntimeException("Rol RESPONSABLE no encontrado"));
            Usuario usuario = new Usuario();
            usuario.setUserName(form.getEmailResponsable());
            usuario.setPassword(passwordEncoder.encode(form.getPasswordResponsable()));
            usuario.setActivo(true);
            usuario.setRol(rolResp);
            usuarioRepository.save(usuario);

            // 2. Crear Empleado (como Cajero)
            Cajero responsable = new Cajero();
            responsable.setNombre(form.getNombreResponsable());
            responsable.setApellido(form.getApellidoResponsable());
            responsable.setDni(form.getDniResponsable());
            responsable.setFechaNacimiento(java.time.LocalDate.of(1990, 1, 1)); // Por defecto
            responsable.setFechaIngreso(java.time.LocalDate.now());
            responsable.setSueldoBase(100000.0);
            responsable.setEliminado(false);
            responsable.setUsuario(usuario);
            responsable.setTurno(Turno.MANIANA);
            responsable.setPlusAntiguedad(0.0);
            empleadoRepository.save(responsable);

            // 3. Crear Unidad de Venta
            Festival festival = festivalRepository.findById(form.getIdFestival()).orElseThrow();
            UnidadDeVenta unidad;
            
            if ("FOOD_TRUCK".equals(form.getTipoUnidad())) {
                FoodTruck ft = new FoodTruck();
                ft.setPatente(form.getPatente());
                ft.setRequiereConexionElectrica(form.isRequiereConexionElectrica());
                unidad = ft;
            } else {
                PuestoDesarmable pd = new PuestoDesarmable();
                pd.setCantidadCarpas(form.getCantidadCarpas());
                pd.setTiempoMontaje(form.getTiempoMontaje());
                unidad = pd;
            }
            
            unidad.setNombreComercial(form.getNombreComercial());
            unidad.setSuperficie(form.getSuperficie());
            unidad.setCodigo(form.getCodigo());
            unidad.setEliminado(false);
            unidad.setFestival(festival);
            unidad.setResponsable(responsable);
            unidadDeVentaRepository.save(unidad);

            // 4. Asignar la unidad al empleado creado
            responsable.setUnidadAsignada(unidad);
            empleadoRepository.save(responsable);

            return "redirect:/home?unidadCreada=true";

        } catch (Exception e) {
            model.addAttribute("mensajeError", "Error: " + e.getMessage());
            model.addAttribute("festivales", festivalRepository.findAll());
            return "admin/unidades/formulario";
        }
    }
}