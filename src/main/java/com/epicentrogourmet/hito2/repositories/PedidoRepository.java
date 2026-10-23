package com.epicentrogourmet.hito2.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.epicentrogourmet.hito2.entity.Pedido;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
	List<Pedido> findByUnidadVentaIdUnidadDeVentaAndUnidadVentaFestivalIdFestival(Long idUnidadVenta, Long idFestival);
    List<Pedido> findByUnidadVentaFestivalIdFestival(Long idFestival);
    List<Pedido> findByUnidadVentaIdUnidadDeVentaAndCierreCajaIsNull(Long idUnidad);
}
