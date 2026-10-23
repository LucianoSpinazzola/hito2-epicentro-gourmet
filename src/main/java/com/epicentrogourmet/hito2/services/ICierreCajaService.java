package com.epicentrogourmet.hito2.services;

import java.util.List;
import com.epicentrogourmet.hito2.entity.CierreCaja;

public interface ICierreCajaService {
    double calcularTotalPendiente(Long idUnidad);
    int contarPedidosPendientes(Long idUnidad);
    CierreCaja realizarCierre(Long idUnidad, Long idCajero);
    List<CierreCaja> historialPorUnidad(Long idUnidad);
}