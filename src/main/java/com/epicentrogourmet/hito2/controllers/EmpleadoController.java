package com.epicentrogourmet.hito2.controllers;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.epicentrogourmet.hito2.dtos.EmpleadoFormDTO;
import com.epicentrogourmet.hito2.entity.Cajero;
import com.epicentrogourmet.hito2.entity.Cocinero;
import com.epicentrogourmet.hito2.entity.Rol;
import com.epicentrogourmet.hito2.entity.RolCocina;
import com.epicentrogourmet.hito2.entity.Turno;
import com.epicentrogourmet.hito2.entity.UnidadDeVenta;
import com.epicentrogourmet.hito2.entity.Usuario;
import com.epicentrogourmet.hito2.repositories.EmpleadoRepository;
import com.epicentrogourmet.hito2.repositories.RolRepository;
import com.epicentrogourmet.hito2.repositories.UnidadDeVentaRepository;
import com.epicentrogourmet.hito2.repositories.UsuarioRepository;
import com.epicentrogourmet.hito2.services.IEmpleadoService;
import com.epicentrogourmet.hito2.services.IFestivalService;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import jakarta.servlet.http.HttpServletResponse;
import java.awt.Color;

@Controller
@RequestMapping("/empleados")
public class EmpleadoController {

	@Autowired
	private IFestivalService festivalService;

	@Autowired
	private IEmpleadoService empleadoService;

	@Autowired
	private EmpleadoRepository empleadoRepository;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private RolRepository rolRepository;

	@Autowired
	private UnidadDeVentaRepository unidadDeVentaRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	// ---------- RECIBO DE SUELDO ----------

	@GetMapping("/mi-sueldo")
	public String verMiSueldo(@AuthenticationPrincipal UserDetails userDetails, Model model) {
		Optional<Usuario> usuarioOpt = usuarioRepository.findByUserName(userDetails.getUsername());
		if (usuarioOpt.isEmpty()) {
			model.addAttribute("mensajeError", "No se encontró el usuario autenticado.");
			return "index";
		}
		Usuario usuarioLogueado = usuarioOpt.get();

		Optional<com.epicentrogourmet.hito2.entity.Empleado> empleadoOpt = empleadoRepository
				.findByUsuario_IdUsuario(usuarioLogueado.getIdUsuario());
		if (empleadoOpt.isEmpty()) {
			model.addAttribute("mensajeError", "No se encontró un perfil de empleado activo para este usuario.");
			return "index";
		}

		com.epicentrogourmet.hito2.entity.Empleado emp = empleadoOpt.get();
		Long idFestivalActivo = 1L;
		
		
		
		if (emp.getUnidadAsignada() != null && emp.getUnidadAsignada().getFestival() != null) {
		    idFestivalActivo = emp.getUnidadAsignada().getFestival().getIdFestival();
		}
		
		
		
		int diasDuracionFestival = festivalService.calcularDuracionEnDias(idFestivalActivo);
		double sueldoFinalCalculado = empleadoService.calcularSueldoTotal(emp.getIdEmpleado(), diasDuracionFestival,
				idFestivalActivo);

		model.addAttribute("titulo", "Recibo de Haberes Digital");
		model.addAttribute("empleado", emp);
		model.addAttribute("rolUsuario", usuarioLogueado.getRol().getNombreRol());
		model.addAttribute("sueldoCalculado", sueldoFinalCalculado);

		return "empleados/recibo";
	}

	@GetMapping("/descargar-sueldo")
	public void descargarReciboPdf(@AuthenticationPrincipal UserDetails userDetails, HttpServletResponse response)
			throws Exception {
		Optional<Usuario> usuarioOpt = usuarioRepository.findByUserName(userDetails.getUsername());
		if (usuarioOpt.isEmpty()) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Usuario no encontrado");
			return;
		}
		Usuario usuarioLogueado = usuarioOpt.get();

		Optional<com.epicentrogourmet.hito2.entity.Empleado> empleadoOpt = empleadoRepository
				.findByUsuario_IdUsuario(usuarioLogueado.getIdUsuario());
		if (empleadoOpt.isEmpty()) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Perfil no encontrado");
			return;
		}

		com.epicentrogourmet.hito2.entity.Empleado emp = empleadoOpt.get();
		Long idFestivalActivo = 1L;
		
		
		
		if (emp.getUnidadAsignada() != null && emp.getUnidadAsignada().getFestival() != null) {
		    idFestivalActivo = emp.getUnidadAsignada().getFestival().getIdFestival();
		}
		
		
		
		
		int diasDuracionFestival = festivalService.calcularDuracionEnDias(idFestivalActivo);
		double sueldoFinal = empleadoService.calcularSueldoTotal(emp.getIdEmpleado(), diasDuracionFestival,
				idFestivalActivo);

		response.setContentType("application/pdf");
		String headerKey = "Content-Disposition";
		String headerValue = "attachment; filename=recibo_haberes_" + emp.getIdEmpleado() + ".pdf";
		response.setHeader(headerKey, headerValue);

		Document documento = new Document(PageSize.A4);
		PdfWriter.getInstance(documento, response.getOutputStream());
		documento.open();

		Font fuenteTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.DARK_GRAY);
		Font fuenteSubtitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.GRAY);
		Font fuenteCuerpo = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);
		Font fuenteTablaHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);

		Paragraph titulo = new Paragraph("EPICENTRO GOURMET - FESTIVAL INDUSTRIAL", fuenteTitulo);
		titulo.setAlignment(Paragraph.ALIGN_CENTER);
		documento.add(titulo);

		Paragraph subtitulo = new Paragraph("Liquidación Oficial de Haberes", fuenteSubtitulo);
		subtitulo.setAlignment(Paragraph.ALIGN_CENTER);
		subtitulo.setSpacingAfter(20);
		documento.add(subtitulo);

		documento.add(new Paragraph("ID Empleado: " + emp.getIdEmpleado(), fuenteCuerpo));
		documento.add(new Paragraph("Usuario Operador: " + usuarioLogueado.getUserName(), fuenteCuerpo));
		documento.add(new Paragraph("Rol Operacional: " + usuarioLogueado.getRol().getNombreRol(), fuenteCuerpo));
		documento.add(new Paragraph("Fecha de Emisión: " + LocalDate.now(), fuenteCuerpo));
		documento.add(new Paragraph(
				"--------------------------------------------------------------------------------------------------------\n\n",
				fuenteCuerpo));

		PdfPTable tabla = new PdfPTable(2);
		tabla.setWidthPercentage(100);
		tabla.setSpacingBefore(10);

		PdfPCell cellHeader1 = new PdfPCell(new Phrase("Concepto / Descripción", fuenteTablaHeader));
		cellHeader1.setBackgroundColor(new Color(0, 123, 255));
		cellHeader1.setPadding(8);

		PdfPCell cellHeader2 = new PdfPCell(new Phrase("Monto Neto ($)", fuenteTablaHeader));
		cellHeader2.setBackgroundColor(new Color(0, 123, 255));
		cellHeader2.setPadding(8);
		cellHeader2.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);

		tabla.addCell(cellHeader1);
		tabla.addCell(cellHeader2);

		tabla.addCell(new PdfPCell(new Phrase("Sueldo Básico por Jornadas del Festival", fuenteCuerpo)));
		PdfPCell c1 = new PdfPCell(new Phrase(String.format("$%.2f", sueldoFinal), fuenteCuerpo));
		c1.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
		tabla.addCell(c1);

		documento.add(tabla);

		Paragraph totalParagraph = new Paragraph("\nTOTAL NETO A DEPOSITAR: " + String.format("$%.2f", sueldoFinal),
				FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, new Color(40, 167, 69)));
		totalParagraph.setAlignment(Paragraph.ALIGN_RIGHT);
		documento.add(totalParagraph);

		documento.close();
	}

	// ---------- ALTA DE EMPLEADO ----------

	@GetMapping("/nuevo")
	public String mostrarFormularioAlta(Model model) {
		model.addAttribute("empleadoForm", new EmpleadoFormDTO());
		model.addAttribute("unidades", unidadDeVentaRepository.findByEliminadoFalse());
		return "empleados/formulario";
	}

	@PostMapping("/nuevo")
	public String guardarEmpleado(@ModelAttribute("empleadoForm") EmpleadoFormDTO form, Model model) {
		model.addAttribute("unidades", unidadDeVentaRepository.findByEliminadoFalse());

		if (form.getNombre() == null || form.getNombre().isBlank()) {
			model.addAttribute("mensajeError", "El nombre es obligatorio.");
			model.addAttribute("campoError", "nombre");
			return "empleados/formulario";
		}

		if (form.getApellido() == null || form.getApellido().isBlank()) {
			model.addAttribute("mensajeError", "El apellido es obligatorio.");
			model.addAttribute("campoError", "apellido");
			return "empleados/formulario";
		}

		if (form.getDni() == null) {
			model.addAttribute("mensajeError", "El DNI es obligatorio.");
			model.addAttribute("campoError", "dni");
			return "empleados/formulario";
		}

		if (form.getDiaNacimiento() == null || form.getMesNacimiento() == null || form.getAnioNacimiento() == null) {
			model.addAttribute("mensajeError", "La fecha de nacimiento es obligatoria.");
			model.addAttribute("campoError", "fechaNacimiento");
			return "empleados/formulario";
		}

		if (form.getDiaIngreso() == null || form.getMesIngreso() == null || form.getAnioIngreso() == null) {
			model.addAttribute("mensajeError", "La fecha de ingreso es obligatoria.");
			model.addAttribute("campoError", "fechaIngreso");
			return "empleados/formulario";
		}

		if (form.getIdUnidadAsignada() == null) {
			model.addAttribute("mensajeError", "La unidad de venta es obligatoria.");
			model.addAttribute("campoError", "idUnidadAsignada");
			return "empleados/formulario";
		}

		if (form.getTipoEmpleado() == null || form.getTipoEmpleado().isBlank()) {
			model.addAttribute("mensajeError", "El tipo de empleado es obligatorio.");
			model.addAttribute("campoError", "tipoEmpleado");
			return "empleados/formulario";
		}

		LocalDate fechaNacimiento;
		LocalDate fechaIngreso;
		try {
			fechaNacimiento = LocalDate.of(form.getAnioNacimiento(), form.getMesNacimiento(), form.getDiaNacimiento());
		} catch (Exception e) {
			model.addAttribute("mensajeError", "La fecha de nacimiento no es válida.");
			model.addAttribute("campoError", "fechaNacimiento");
			return "empleados/formulario";
		}
		try {
			fechaIngreso = LocalDate.of(form.getAnioIngreso(), form.getMesIngreso(), form.getDiaIngreso());
		} catch (Exception e) {
			model.addAttribute("mensajeError", "La fecha de ingreso no es válida.");
			model.addAttribute("campoError", "fechaIngreso");
			return "empleados/formulario";
		}

		if (fechaNacimiento.plusYears(18).isAfter(LocalDate.now())) {
			model.addAttribute("mensajeError", "El empleado debe ser mayor de edad.");
			model.addAttribute("campoError", "fechaNacimiento");
			return "empleados/formulario";
		}

		if (fechaIngreso.isAfter(LocalDate.now())) {
			model.addAttribute("mensajeError", "La fecha de ingreso no puede ser futura.");
			model.addAttribute("campoError", "fechaIngreso");
			return "empleados/formulario";
		}

		if (empleadoRepository.findByDni(form.getDni()).isPresent()) {
			model.addAttribute("mensajeError", "Ya existe un empleado con ese DNI.");
			model.addAttribute("campoError", "dni");
			return "empleados/formulario";
		}

		Optional<UnidadDeVenta> unidadOpt = unidadDeVentaRepository.findById(form.getIdUnidadAsignada());
		if (unidadOpt.isEmpty()) {
			model.addAttribute("mensajeError", "La unidad seleccionada no existe.");
			model.addAttribute("campoError", "idUnidadAsignada");
			return "empleados/formulario";
		}

		Optional<Rol> rolOpt = rolRepository.findByNombreRol("EMPLEADO");
		if (rolOpt.isEmpty()) {
			model.addAttribute("mensajeError", "No existe el rol EMPLEADO en la base.");
			return "empleados/formulario";
		}

		String username = form.getNombre().toLowerCase() + "." + form.getApellido().toLowerCase() + "@epicentro.com";
		if (usuarioRepository.findByUserName(username).isPresent()) {
			model.addAttribute("mensajeError",
					"Ya existe un usuario con ese nombre. Probá con otro nombre o apellido.");
			model.addAttribute("campoError", "nombre");
			return "empleados/formulario";
		}

		String tipo = form.getTipoEmpleado().toUpperCase();
		Turno turno = null;
		RolCocina rolCocina = null;
		double plus = 0.0;

		if ("CAJERO".equals(tipo)) {
			if (form.getTurno() == null || form.getTurno().isBlank()) {
				model.addAttribute("mensajeError", "Para un cajero, el turno es obligatorio.");
				model.addAttribute("campoError", "turno");
				return "empleados/formulario";
			}
			try {
				turno = Turno.valueOf(form.getTurno().toUpperCase());
			} catch (Exception e) {
				model.addAttribute("mensajeError", "Turno inválido.");
				model.addAttribute("campoError", "turno");
				return "empleados/formulario";
			}
		} else if ("COCINERO".equals(tipo)) {
			if (form.getRolCocina() == null || form.getRolCocina().isBlank()) {
				model.addAttribute("mensajeError", "Para un cocinero, la categoría es obligatoria.");
				model.addAttribute("campoError", "rolCocina");
				return "empleados/formulario";
			}
			try {
				rolCocina = RolCocina.valueOf(form.getRolCocina().toUpperCase());
			} catch (Exception e) {
				model.addAttribute("mensajeError", "Categoría de cocina inválida.");
				model.addAttribute("campoError", "rolCocina");
				return "empleados/formulario";
			}
			switch (rolCocina) {
			case COCINERO:
				plus = 150000.0;
				break;
			case COCINERO_AYUDANTE:
				plus = 100000.0;
				break;
			case LAVAPLATOS:
				plus = 50000.0;
				break;
			default:
				plus = 0.0;
			}
		} else {
			model.addAttribute("mensajeError", "Tipo de empleado inválido.");
			model.addAttribute("campoError", "tipoEmpleado");
			return "empleados/formulario";
		}

		// Todas las validaciones pasaron: recién acá se crea el usuario y el empleado.
		String claveAleatoria = generarClaveAleatoria();

		Usuario usuario = new Usuario();
		usuario.setUserName(username);
		usuario.setPassword(passwordEncoder.encode(claveAleatoria));
		usuario.setActivo(true);
		usuario.setRol(rolOpt.get());
		usuarioRepository.save(usuario);

		if ("CAJERO".equals(tipo)) {
			Cajero cajero = new Cajero();
			cajero.setNombre(form.getNombre());
			cajero.setApellido(form.getApellido());
			cajero.setDni(form.getDni());
			cajero.setFechaNacimiento(fechaNacimiento);
			cajero.setFechaIngreso(fechaIngreso);
			cajero.setSueldoBase(100000.0);
			cajero.setEliminado(false);
			cajero.setUsuario(usuario);
			cajero.setUnidadAsignada(unidadOpt.get());
			cajero.setTurno(turno);
			cajero.setPlusAntiguedad(0.0);
			empleadoRepository.save(cajero);
		} else {
			Cocinero cocinero = new Cocinero();
			cocinero.setNombre(form.getNombre());
			cocinero.setApellido(form.getApellido());
			cocinero.setDni(form.getDni());
			cocinero.setFechaNacimiento(fechaNacimiento);
			cocinero.setFechaIngreso(fechaIngreso);
			cocinero.setSueldoBase(100000.0);
			cocinero.setEliminado(false);
			cocinero.setUsuario(usuario);
			cocinero.setUnidadAsignada(unidadOpt.get());
			cocinero.setRolCocina(rolCocina);
			cocinero.setPlusCategoria(plus);
			empleadoRepository.save(cocinero);
		}

		model.addAttribute("mensajeExito", "Empleado dado de alta correctamente. Usuario: " + username);
		model.addAttribute("empleadoForm", new EmpleadoFormDTO());
		return "empleados/formulario";
	}

	private String generarClaveAleatoria() {
		String caracteres = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789";
		SecureRandom random = new SecureRandom();
		StringBuilder sb = new StringBuilder(10);
		for (int i = 0; i < 10; i++) {
			sb.append(caracteres.charAt(random.nextInt(caracteres.length())));
		}
		return sb.toString();
	}
}
