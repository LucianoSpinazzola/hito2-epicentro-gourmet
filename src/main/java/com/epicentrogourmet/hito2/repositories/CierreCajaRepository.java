package com.epicentrogourmet.hito2.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.epicentrogourmet.hito2.entity.CierreCaja;

@Repository
public interface CierreCajaRepository extends JpaRepository<CierreCaja, Long> {
    // Busca el historial de cierres de una unidad, ordenados del más nuevo al más viejo
    List<CierreCaja> findByUnidadDeVentaIdUnidadDeVentaOrderByFechaHoraDesc(Long idUnidad);
}