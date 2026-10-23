package com.epicentrogourmet.hito2.services;

import java.util.List;
import com.epicentrogourmet.hito2.entity.UnidadDeVenta;
import com.epicentrogourmet.hito2.dtos.PlatoDTO;
import com.epicentrogourmet.hito2.dtos.ReporteMayoresCanonDTO;

public interface IUnidadDeVentaService {
	List<UnidadDeVenta> listarActivas();
    UnidadDeVenta guardar(UnidadDeVenta unidad);
    double calcularCanon(Long idUnidad, Long idFestival);
    
    
    double calcularRentabilidadNeta(Long idUnidad, Long idFestival);
    
    List<ReporteMayoresCanonDTO> obtenerUnidadesMayorCanon(Long idFestival, int x);
    PlatoDTO obtenerPlatoEstrella(Long idUnidad, Long idFestival);
    List<com.epicentrogourmet.hito2.dtos.ReporteRecaudacionUnidadDTO> obtenerRankingRecaudacion(Long idFestival, int limite);
}
