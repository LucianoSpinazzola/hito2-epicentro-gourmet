package com.epicentrogourmet.hito2.services.impl;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.epicentrogourmet.hito2.entity.CierreCaja;
import com.epicentrogourmet.hito2.entity.Empleado;
import com.epicentrogourmet.hito2.entity.Pedido;
import com.epicentrogourmet.hito2.entity.UnidadDeVenta;
import com.epicentrogourmet.hito2.repositories.CierreCajaRepository;
import com.epicentrogourmet.hito2.repositories.EmpleadoRepository;
import com.epicentrogourmet.hito2.repositories.PedidoRepository;
import com.epicentrogourmet.hito2.repositories.UnidadDeVentaRepository;
import com.epicentrogourmet.hito2.services.ICierreCajaService;

@Service
public class CierreCajaServiceImpl implements ICierreCajaService {

    @Autowired private PedidoRepository pedidoRepository;
    @Autowired private UnidadDeVentaRepository unidadDeVentaRepository;
    @Autowired private EmpleadoRepository empleadoRepository;
    @Autowired private CierreCajaRepository cierreCajaRepository;

    @Override
    public double calcularTotalPendiente(Long idUnidad) {
        List<Pedido> pendientes = pedidoRepository.findByUnidadVentaIdUnidadDeVentaAndCierreCajaIsNull(idUnidad);
        double total = 0.0;
        for (Pedido p : pendientes) total += p.getTotal();
        return total;
    }

    @Override
    public int contarPedidosPendientes(Long idUnidad) {
        return pedidoRepository.findByUnidadVentaIdUnidadDeVentaAndCierreCajaIsNull(idUnidad).size();
    }

    @Override
    @Transactional // Asegura que se guarde el cierre y se actualicen los pedidos juntos
    public CierreCaja realizarCierre(Long idUnidad, Long idCajero) {
        UnidadDeVenta unidad = unidadDeVentaRepository.findById(idUnidad).orElseThrow();
        Empleado cajero = empleadoRepository.findById(idCajero).orElseThrow();

        List<Pedido> pendientes = pedidoRepository.findByUnidadVentaIdUnidadDeVentaAndCierreCajaIsNull(idUnidad);

        double total = 0.0;
        for (Pedido p : pendientes) total += p.getTotal();

        // 1. Creamos la "foto" del cierre
        CierreCaja cierre = new CierreCaja();
        cierre.setFechaHora(LocalDateTime.now());
        cierre.setTotalVentas(total);
        cierre.setCantidadPedidos(pendientes.size());
        cierre.setUnidadDeVenta(unidad);
        cierre.setCajero(cajero);
        cierreCajaRepository.save(cierre);

        // 2. Le decimos a los pedidos que pertenecen a este cierre
        for (Pedido p : pendientes) {
            p.setCierreCaja(cierre);
            pedidoRepository.save(p);
        }
        return cierre;
    }

    @Override
    public List<CierreCaja> historialPorUnidad(Long idUnidad) {
        return cierreCajaRepository.findByUnidadDeVentaIdUnidadDeVentaOrderByFechaHoraDesc(idUnidad);
    }
}
