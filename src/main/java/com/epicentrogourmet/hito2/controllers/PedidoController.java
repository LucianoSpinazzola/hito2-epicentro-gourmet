package com.epicentrogourmet.hito2.controllers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.epicentrogourmet.hito2.dtos.ItemFormDTO;
import com.epicentrogourmet.hito2.dtos.PedidoFormDTO;
import com.epicentrogourmet.hito2.entity.Cajero;
import com.epicentrogourmet.hito2.entity.Empleado;
import com.epicentrogourmet.hito2.entity.ItemPedido;
import com.epicentrogourmet.hito2.entity.Pedido;
import com.epicentrogourmet.hito2.entity.Plato;
import com.epicentrogourmet.hito2.entity.UnidadDeVenta;
import com.epicentrogourmet.hito2.entity.Usuario;
import com.epicentrogourmet.hito2.repositories.EmpleadoRepository;
import com.epicentrogourmet.hito2.repositories.PedidoRepository;
import com.epicentrogourmet.hito2.repositories.PlatoRepository;
import com.epicentrogourmet.hito2.repositories.UnidadDeVentaRepository;
import com.epicentrogourmet.hito2.repositories.UsuarioRepository;

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
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private com.epicentrogourmet.hito2.services.ICierreCajaService cierreCajaService;

	@Autowired
	private EmpleadoRepository empleadoRepository;

	@Autowired
	private ModelMapper modelMapper;

	// ---------------------------------------------------------------------
	// Helper: obtiene el Cajero logueado o null si el usuario no lo es.
	// Devuelve null si:
	//   - no hay sesión
	//   - el username no existe
	//   - el usuario no tiene perfil de empleado
	//   - el empleado no es del subtipo Cajero
	// ---------------------------------------------------------------------
	private Cajero obtenerCajeroAutenticado(UserDetails userDetails) {
		if (userDetails == null) {
			return null;
		}
		Optional<Usuario> usuarioOpt = usuarioRepository.findByUserName(userDetails.getUsername());
		if (usuarioOpt.isEmpty()) {
			return null;
		}
		Optional<Empleado> empleadoOpt = empleadoRepository
				.findByUsuario_IdUsuario(usuarioOpt.get().getIdUsuario());
		if (empleadoOpt.isEmpty()) {
			return null;
		}
		Empleado empleado = empleadoOpt.get();
		if (!(empleado instanceof Cajero)) {
			return null;
		}
		return (Cajero) empleado;
	}

	// ---------------------------------------------------------------------
	// GET /pedidos/nuevo
	// El cajero NO elige la unidad: se fuerza desde su perfil.
	// Los platos se filtran por los asociados a su unidad (con fallback).
	// @Transactional(readOnly=true) asegura que la colección LAZY
	// UnidadDeVenta.platos se pueda leer fuera de la sesión (OSIV).
	// ---------------------------------------------------------------------
	@GetMapping("/nuevo")
	@Transactional(readOnly = true)
	public String mostrarFormularioPedido(@AuthenticationPrincipal UserDetails userDetails, Model model) {

		Cajero cajero = obtenerCajeroAutenticado(userDetails);
		if (cajero == null) {
			return "redirect:/home?errorNoCajero=true";
		}

		UnidadDeVenta unidadAsignada = cajero.getUnidadAsignada();
		if (unidadAsignada == null) {
			return "redirect:/home?errorSinUnidad=true";
		}

		// Platos: solo los de la unidad. Si la unidad no tiene platos
		// cargados, se cae a todos para no dejar la pantalla vacía.
		List<Plato> listaPlatos;
		if (unidadAsignada.getPlatos() != null && !unidadAsignada.getPlatos().isEmpty()) {
			listaPlatos = new ArrayList<>(unidadAsignada.getPlatos());
		} else {
			listaPlatos = platoRepository.findAll();
		}

		PedidoFormDTO pedidoForm = new PedidoFormDTO();
		pedidoForm.setIdUnidadVenta(unidadAsignada.getIdUnidadDeVenta());
		for (Plato p : listaPlatos) {
			pedidoForm.getItems().add(new ItemFormDTO(p.getIdPlato(), p.getNombre(), p.getPrecioVenta(), 0));
		}

		model.addAttribute("unidadAsignada", unidadAsignada);
		model.addAttribute("pedidoForm", pedidoForm);
		model.addAttribute("titulo", "Registrar Nuevo Pedido");
		return "pedidos/formulario";
	}

	// ---------------------------------------------------------------------
	// POST /pedidos/guardar
	// La unidad SIEMPRE se toma del cajero logueado.
	// Se ignora cualquier idUnidadVenta que venga en el request.
	// ---------------------------------------------------------------------
	@PostMapping("/guardar")
	public String guardarPedido(@ModelAttribute("pedidoForm") PedidoFormDTO pedidoForm,
			@AuthenticationPrincipal UserDetails userDetails) {

		Cajero cajero = obtenerCajeroAutenticado(userDetails);
		if (cajero == null) {
			return "redirect:/home?errorNoCajero=true";
		}
		UnidadDeVenta unidadCajero = cajero.getUnidadAsignada();
		if (unidadCajero == null) {
			return "redirect:/home?errorSinUnidad=true";
		}

		Pedido pedido = modelMapper.map(pedidoForm, Pedido.class);
		pedido.setIdPedido(null);
		pedido.setFecha(LocalDate.now());
		pedido.setEstado("ENTREGADO");
		pedido.setItems(new HashSet<>());

		// SEGURIDAD: la unidad del pedido es la del cajero.
		// No se usa pedidoForm.getIdUnidadVenta() a propósito.
		pedido.setUnidadVenta(unidadCajero);

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
	 @GetMapping("/cierre-caja")
	    public String verCierreCaja(@AuthenticationPrincipal UserDetails userDetails, Model model) {
	        // Usamos el helper que creaste en la tarea 4
	        Cajero cajero = obtenerCajeroAutenticado(userDetails);
	        if (cajero == null || cajero.getUnidadAsignada() == null) {
	            return "redirect:/home?errorSinUnidad=true";
	        }
	        UnidadDeVenta unidad = cajero.getUnidadAsignada();
	        
	        model.addAttribute("unidad", unidad);
	        model.addAttribute("totalPendiente", cierreCajaService.calcularTotalPendiente(unidad.getIdUnidadDeVenta()));
	        model.addAttribute("cantidadPendiente", cierreCajaService.contarPedidosPendientes(unidad.getIdUnidadDeVenta()));
	        model.addAttribute("historial", cierreCajaService.historialPorUnidad(unidad.getIdUnidadDeVenta()));
	        model.addAttribute("titulo", "Cierre de Caja");
	        
	        return "pedidos/cierre-caja";
	    }

	    @PostMapping("/cierre-caja")
	    public String ejecutarCierreCaja(@AuthenticationPrincipal UserDetails userDetails) {
	        Cajero cajero = obtenerCajeroAutenticado(userDetails);
	        if (cajero == null || cajero.getUnidadAsignada() == null) {
	            return "redirect:/home?errorSinUnidad=true";
	        }
	        
	        // Si no hay pedidos, avisamos con un parámetro en la URL
	        if (cierreCajaService.contarPedidosPendientes(cajero.getUnidadAsignada().getIdUnidadDeVenta()) == 0) {
	            return "redirect:/pedidos/cierre-caja?sinPendientes=true";
	        }
	        
	        // Ejecutamos el cierre
	        cierreCajaService.realizarCierre(cajero.getUnidadAsignada().getIdUnidadDeVenta(), cajero.getIdEmpleado());
	        
	        return "redirect:/pedidos/cierre-caja?cierreExitoso=true";
	    }
}