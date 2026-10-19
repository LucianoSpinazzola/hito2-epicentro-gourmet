package com.epicentrogourmet.hito2.services;

import com.epicentrogourmet.hito2.dtos.ReporteVentaDTO;

public interface IFestivalService {
    
    
    ReporteVentaDTO obtenerReporteRecaudacion(Long idFestival);
    int calcularDuracionEnDias(Long idFestival);
}
