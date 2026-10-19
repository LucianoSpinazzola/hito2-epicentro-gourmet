package com.epicentrogourmet.hito2.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.epicentrogourmet.hito2.entity.Usuario;
import com.epicentrogourmet.hito2.entity.Empleado;
import com.epicentrogourmet.hito2.repositories.EmpleadoRepository;
import com.epicentrogourmet.hito2.services.IEmpleadoService;
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
import java.time.LocalDate;

import jakarta.servlet.http.HttpSession;
import java.util.Optional;

@Controller
@RequestMapping("/empleados")
public class EmpleadoController {
	 @Autowired
	    private com.epicentrogourmet.hito2.services.IFestivalService festivalService;
    @Autowired
    private IEmpleadoService empleadoService;

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @GetMapping("/mi-sueldo")
    public String verMiSueldo(HttpSession session, Model model) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) {
            return "redirect:/login";
        }

        // Buscamos el empleado físico vinculado en la herencia JOINED por su ID de usuario
        Optional<Empleado> empleadoOpt = empleadoRepository.findById(usuarioLogueado.getIdUsuario());
        
        if (empleadoOpt.isEmpty()) {
            model.addAttribute("mensajeError", "No se encontró un perfil de empleado activo para este usuario.");
            return "index";
        }

        Empleado emp = empleadoOpt.get();
        Long idFestivalActivo = 1L; // Simulamos con el Festival 1 de tu script de carga
        int diasDuracionFestival = festivalService.calcularDuracionEnDias(idFestivalActivo);
        // Lógica paramétrica que calcula la antigüedad y el tramo del enunciado
        double sueldoFinalCalculado = empleadoService.calcularSueldoTotal(emp.getIdEmpleado(), diasDuracionFestival, idFestivalActivo);
        model.addAttribute("titulo", "Recibo de Haberes Digital");
        model.addAttribute("empleado", emp);
        model.addAttribute("sueldoCalculado", sueldoFinalCalculado);

        return "empleados/recibo"; // Buscará templates/empleados/recibo.html
    }
    @GetMapping("/descargar-sueldo")
    public void descargarReciboPdf(HttpSession session, HttpServletResponse response) throws Exception {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) {
            response.sendRedirect("/login");
            return;
        }

        Optional<Empleado> empleadoOpt = empleadoRepository.findById(usuarioLogueado.getIdUsuario());
        if (empleadoOpt.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Perfil no encontrado");
            return;
        }

        Empleado emp = empleadoOpt.get();
        Long idFestivalActivo = 1L;
        int diasDuracionFestival = festivalService.calcularDuracionEnDias(idFestivalActivo);
        double sueldoFinal = empleadoService.calcularSueldoTotal(emp.getIdEmpleado(), diasDuracionFestival, idFestivalActivo);

        // 1. Configurar los encabezados de respuesta para descarga de archivo binario
        response.setContentType("application/pdf");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=recibo_haberes_" + emp.getIdEmpleado() + ".pdf";
        response.setHeader(headerKey, headerValue);

        // 2. Inicializar el documento OpenPDF
        Document documento = new Document(PageSize.A4);
        PdfWriter.getInstance(documento, response.getOutputStream());
        documento.open();

        // Estilos de fuentes profesionales
        Font fuenteTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.DARK_GRAY);
        Font fuenteSubtitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.GRAY);
        Font fuenteCuerpo = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);
        Font fuenteTablaHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);

        // Cabecera del Documento
        Paragraph titulo = new Paragraph("EPICENTRO GOURMET - FESTIVAL INDUSTRIAL", fuenteTitulo);
        titulo.setAlignment(Paragraph.ALIGN_CENTER);
        documento.add(titulo);

        Paragraph subtitulo = new Paragraph("Liquidación Oficial de Haberes", fuenteSubtitulo);
        subtitulo.setAlignment(Paragraph.ALIGN_CENTER);
        subtitulo.setSpacingAfter(20);
        documento.add(subtitulo);

        // Datos del Empleado
        documento.add(new Paragraph("ID Empleado: " + emp.getIdEmpleado(), fuenteCuerpo));
        documento.add(new Paragraph("Usuario Operador: " + usuarioLogueado.getUserName(), fuenteCuerpo));
        documento.add(new Paragraph("Rol Operacional: " + usuarioLogueado.getRol().getNombreRol(), fuenteCuerpo));
        documento.add(new Paragraph("Fecha de Emisión: " + LocalDate.now(), fuenteCuerpo));
        documento.add(new Paragraph("--------------------------------------------------------------------------------------------------------\n\n", fuenteCuerpo));

        // 3. Crear Tabla de Conceptos
        PdfPTable tabla = new PdfPTable(2); // 2 columnas
        tabla.setWidthPercentage(100);
        tabla.setSpacingBefore(10);

        // Encabezados de la Tabla
        PdfPCell cellHeader1 = new PdfPCell(new Phrase("Concepto / Descripción", fuenteTablaHeader));
        cellHeader1.setBackgroundColor(new Color(0, 123, 255)); // Azul corporativo
        cellHeader1.setPadding(8);
        
        PdfPCell cellHeader2 = new PdfPCell(new Phrase("Monto Neto ($)", fuenteTablaHeader));
        cellHeader2.setBackgroundColor(new Color(0, 123, 255));
        cellHeader2.setPadding(8);
        cellHeader2.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);

        tabla.addCell(cellHeader1);
        tabla.addCell(cellHeader2);

        // Fila 1: Haberes Base proporcionales
        tabla.addCell(new PdfPCell(new Phrase("Sueldo Básico por Jornadas del Festival", fuenteCuerpo)));
        PdfPCell c1 = new PdfPCell(new Phrase(String.format("$%.2f", sueldoFinal), fuenteCuerpo));
        c1.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
        tabla.addCell(c1);

        documento.add(tabla);

        // Cierre con el Total Neto a Cobrar
        Paragraph totalParagraph = new Paragraph("\nTOTAL NETO A DEPOSITAR: " + String.format("$%.2f", sueldoFinal), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, new Color(40, 167, 69)));
        totalParagraph.setAlignment(Paragraph.ALIGN_RIGHT);
        documento.add(totalParagraph);

        // 4. Cerrar el flujo
        documento.close();
    }
}