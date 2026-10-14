package com.epicentrogourmet.hito2.services.impl;

import java.util.List;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epicentrogourmet.hito2.entity.UnidadDeVenta;
import com.epicentrogourmet.hito2.dtos.PlatoDTO;
import com.epicentrogourmet.hito2.entity.Costo;
import com.epicentrogourmet.hito2.entity.FoodTruck;
import com.epicentrogourmet.hito2.entity.ItemPedido;
import com.epicentrogourmet.hito2.entity.Pedido;
import com.epicentrogourmet.hito2.entity.PuestoDesarmable;
import com.epicentrogourmet.hito2.dtos.ReporteMayoresCanonDTO;
import com.epicentrogourmet.hito2.repositories.CostoRepository;
import com.epicentrogourmet.hito2.repositories.PedidoRepository;
import com.epicentrogourmet.hito2.repositories.UnidadDeVentaRepository;
import com.epicentrogourmet.hito2.services.IUnidadDeVentaService;

@Service
public class UnidadDeVentaServiceImpl implements IUnidadDeVentaService {

	@Autowired
	private CostoRepository costoRepository;

	@Autowired
	private PedidoRepository pedidoRepository;

	@Autowired
	private UnidadDeVentaRepository unidadDeVentaRepository;

	@Autowired
	private com.epicentrogourmet.hito2.services.IEmpleadoService empleadoService;

	
	@Override
	public List<UnidadDeVenta> listarActivas() {
		return unidadDeVentaRepository.findByEliminadoFalse();
	}

	
	@Override
	public UnidadDeVenta guardar(UnidadDeVenta unidad) {
		unidad.setEliminado(false);
		return unidadDeVentaRepository.save(unidad);
	}

	@Override
	public double calcularCanon(Long idUnidad, Long idFestival) {
		UnidadDeVenta unidad = unidadDeVentaRepository.findById(idUnidad)
				.orElseThrow(() -> new RuntimeException("Unidad de venta no encontrada"));

		Costo costosFestival = costoRepository.findByFestivalIdFestival(idFestival)
				.orElseThrow(() -> new RuntimeException("No se encontraron costos configurados para este festival"));

		if (unidad instanceof FoodTruck) {
			FoodTruck ft = (FoodTruck) unidad;
			return (ft.getSuperficie() * costosFestival.getCostoSuperficie()) 
					+ (ft.isRequiereConexionElectrica() ? costosFestival.getPlusElectricidad() : 0.0);
		} 
		
		if (unidad instanceof PuestoDesarmable) {
			PuestoDesarmable pd = (PuestoDesarmable) unidad;
			return (pd.getSuperficie() * costosFestival.getCostoSuperficie()) 
					- (pd.getTiempoMontaje() * costosFestival.getCostoMontaje());
		}

		return 0.0;
	}

	@Override
	public List<ReporteMayoresCanonDTO> obtenerUnidadesMayorCanon(Long idFestival, int x) {
		List<UnidadDeVenta> unidades = unidadDeVentaRepository.findByFestivalIdFestivalAndEliminadoFalse(idFestival);
		List<ReporteMayoresCanonDTO> reporteList = new ArrayList<>();

		for (UnidadDeVenta u : unidades) {
			double canon = calcularCanon(u.getIdUnidadDeVenta(), idFestival);
			String tipo = (u instanceof FoodTruck) ? "Food Truck" : "Puesto Desarmable";
			
			reporteList.add(new ReporteMayoresCanonDTO(
				u.getNombreComercial(),
				u.getCodigo(),
				tipo,
				canon
			));
		}

		return reporteList.stream()
				.sorted((r1, r2) -> Double.compare(r2.getCanonCalculado(), r1.getCanonCalculado()))
				.limit(x)
				.toList();
	}

	@Override
	public double calcularRentabilidadNeta(Long idUnidad, Long idFestival) {
		double rentabilidadFinal = 0.0;

		UnidadDeVenta unidad = unidadDeVentaRepository.findById(idUnidad)
				.orElseThrow(() -> new RuntimeException("Unidad de venta no encontrada"));

		List<Pedido> pedidos = pedidoRepository.findByUnidadVentaIdUnidadDeVentaAndUnidadVentaFestivalIdFestival(idUnidad, idFestival);

		double totalVentas = 0.0;
		double totalCostosProduccion = 0.0;

		if (pedidos != null) {
			for (Pedido p : pedidos) {
				totalVentas += p.getTotal();
				if (p.getItems() != null) {
					for (ItemPedido item : p.getItems()) {
						if (item.getPlato() != null) {
							totalCostosProduccion += item.getCantidad() * item.getPlato().getCostoProduccion(); 
						}
					}
				}
			}
		}

		double canonUnidad = calcularCanon(idUnidad, idFestival);
		double totalSueldosStaff = 0.0;
		
		
		int diasDuracionFestival = (int) java.time.temporal.ChronoUnit.DAYS.between(
				unidad.getFestival().getFechaDeInicio(), 
				unidad.getFestival().getFechaDeFin()
		);

		if (unidad.getResponsable() != null) {
			totalSueldosStaff += empleadoService.calcularSueldoTotal(unidad.getResponsable().getIdEmpleado(), diasDuracionFestival, idFestival);
		}

		rentabilidadFinal = totalVentas - totalCostosProduccion - totalSueldosStaff - canonUnidad;
		return rentabilidadFinal;
	}
	@Override
	public com.epicentrogourmet.hito2.dtos.PlatoDTO obtenerPlatoEstrella(Long idUnidad, Long idFestival) {
		List<Pedido> pedidos = pedidoRepository.findByUnidadVentaIdUnidadDeVentaAndUnidadVentaFestivalIdFestival(idUnidad, idFestival);
		
		java.util.Map<com.epicentrogourmet.hito2.entity.Plato, Integer> conteoPlatos = new java.util.HashMap<>();
		if (pedidos != null) {
			for (Pedido p : pedidos) {
				if (p.getItems() != null) {
					for (ItemPedido item : p.getItems()) {
						if (item.getPlato() != null) {
							com.epicentrogourmet.hito2.entity.Plato plato = item.getPlato();
							int cantidadPedida = item.getCantidad();
							
							conteoPlatos.put(plato, conteoPlatos.getOrDefault(plato, 0) + cantidadPedida);
						}
					}
				}
			}
		}

		com.epicentrogourmet.hito2.entity.Plato platoEstrella = null;
		int maxCantidad = 0;

		
		for (java.util.Map.Entry<com.epicentrogourmet.hito2.entity.Plato, Integer> entry : conteoPlatos.entrySet()) { 
			if (entry.getValue() > maxCantidad) {
				maxCantidad = entry.getValue();
				platoEstrella = entry.getKey();
			}
		}

		if (platoEstrella != null) {
			return new com.epicentrogourmet.hito2.dtos.PlatoDTO(
				platoEstrella.getIdPlato(),
				platoEstrella.getNombre(),
				maxCantidad,
				platoEstrella.getPrecioVenta()
			);
		}

		return null;
	}
}