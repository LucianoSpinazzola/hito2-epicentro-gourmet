package com.epicentrogourmet.hito2.controllers;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.epicentrogourmet.hito2.dtos.PedidoFormDTO;
import com.epicentrogourmet.hito2.dtos.ItemFormDTO;
import com.epicentrogourmet.hito2.entity.ItemPedido;
import com.epicentrogourmet.hito2.entity.Pedido;
import com.epicentrogourmet.hito2.entity.Plato;
import com.epicentrogourmet.hito2.entity.UnidadDeVenta;
import com.epicentrogourmet.hito2.repositories.PedidoRepository;
import com.epicentrogourmet.hito2.repositories.PlatoRepository;
import com.epicentrogourmet.hito2.repositories.UnidadDeVentaRepository;

@Controller
@RequestMapping("/pedidos")
public class PedidoController {

	@Autowired
	private PedidoRepository pedidoRepository;

	@Autowired
	private PlatoRepository platoRepository;

	@Autowired
	private UnidadDeVentaRepository unidadDeVentaRepository;

	@Autowired
	private ModelMapper modelMapper;

	@GetMapping("/nuevo")
	public String mostrarFormularioPedido(@AuthenticationPrincipal UserDetails userDetails, Model model) {
		List<Plato> listaPlatos = platoRepository.findAll();
		List<UnidadDeVenta> listaUnidades = unidadDeVentaRepository.findByEliminadoFalse();

		PedidoFormDTO pedidoForm = new PedidoFormDTO();
		for (Plato p : listaPlatos) {
			pedidoForm.getItems().add(new ItemFormDTO(p.getIdPlato(), p.getNombre(), p.getPrecioVenta(), 0));
		}

		model.addAttribute("unidadesVenta", listaUnidades);
		model.addAttribute("pedidoForm", pedidoForm);
		model.addAttribute("titulo", "Registrar Nuevo Pedido");
		return "pedidos/formulario";
	}

	@PostMapping("/guardar")
	public String guardarPedido(@ModelAttribute("pedidoForm") PedidoFormDTO pedidoForm,
			@AuthenticationPrincipal UserDetails userDetails) {
		Pedido pedido = modelMapper.map(pedidoForm, Pedido.class);

		pedido.setFecha(LocalDate.now());
		pedido.setEstado("ENTREGADO");
		pedido.setItems(new HashSet<>());

		UnidadDeVenta unidad = unidadDeVentaRepository.findById(pedidoForm.getIdUnidadVenta())
				.orElseThrow(() -> new RuntimeException("Unidad de venta no encontrada"));
		pedido.setUnidadVenta(unidad);

		double totalAcumulado = 0.0;

		for (ItemFormDTO itemDto : pedidoForm.getItems()) {
			if (itemDto.getCantidad() > 0) {
				Plato plato = platoRepository.findById(itemDto.getIdPlato())
						.orElseThrow(() -> new RuntimeException("Plato no encontrado"));

				ItemPedido itemEntidad = modelMapper.map(itemDto, ItemPedido.class);

				itemEntidad.setPrecioUnitario(plato.getPrecioVenta());
				itemEntidad.setPlato(plato);
				itemEntidad.setPedido(pedido);

				totalAcumulado += (plato.getPrecioVenta() * itemDto.getCantidad());
				pedido.getItems().add(itemEntidad);
			}
		}

		if (pedido.getItems().isEmpty()) {
			return "redirect:/pedidos/nuevo?errorVacio=true";
		}

		pedido.setTotal(totalAcumulado);
		pedidoRepository.save(pedido);

		return "redirect:/home?pedidoExito=true";
	}
}
