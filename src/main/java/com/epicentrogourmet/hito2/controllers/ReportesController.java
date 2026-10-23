package com.epicentrogourmet.hito2.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.epicentrogourmet.hito2.services.IFestivalService;
import com.epicentrogourmet.hito2.services.IUnidadDeVentaService;

@Controller
@RequestMapping("/reportes")
public class ReportesController {

    @Autowired
    private IFestivalService festivalService;

    @Autowired
    private IUnidadDeVentaService unidadDeVentaService;

    @GetMapping
    public String verReportes(@RequestParam(defaultValue = "1") Long idFestival,
                              @RequestParam(defaultValue = "5") int limite,
                              Model model) {
        model.addAttribute("titulo", "Reportes Gerenciales");
        model.addAttribute("recaudacion", festivalService.obtenerReporteRecaudacion(idFestival));
        model.addAttribute("rankingRecaudacion", unidadDeVentaService.obtenerRankingRecaudacion(idFestival, limite));
        model.addAttribute("mayoresCanon", unidadDeVentaService.obtenerUnidadesMayorCanon(idFestival, limite));
        return "reportes/index";
    }
}