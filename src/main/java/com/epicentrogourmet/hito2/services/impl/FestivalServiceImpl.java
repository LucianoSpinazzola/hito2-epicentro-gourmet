package com.epicentrogourmet.hito2.services.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epicentrogourmet.hito2.entity.Pedido;
import com.epicentrogourmet.hito2.dtos.ReporteVentaDTO;
import com.epicentrogourmet.hito2.repositories.PedidoRepository;
import com.epicentrogourmet.hito2.repositories.FestivalRepository; // Asegúrate de tener este repo creado
import com.epicentrogourmet.hito2.entity.Festival;
import com.epicentrogourmet.hito2.services.IFestivalService;

@Service
public class FestivalServiceImpl implements IFestivalService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private FestivalRepository festivalRepository; 

    @Override
    public ReporteVentaDTO obtenerReporteRecaudacion(Long idFestival) {
        
        Festival festival = festivalRepository.findById(idFestival)
                .orElseThrow(() -> new RuntimeException("Festival no encontrado"));

        
        List<Pedido> pedidos = pedidoRepository.findByUnidadVentaFestivalIdFestival(idFestival);

        
        double recaudacionTotal = 0.0;
        if (pedidos != null) {
            for (Pedido p : pedidos) {
                recaudacionTotal += p.getTotal();
            }
        }

       
        return new ReporteVentaDTO(
                festival.getIdFestival(),
                festival.getNombre(), 
                recaudacionTotal
        );
    }
}
