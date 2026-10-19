package com.epicentrogourmet.hito2.controllers;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

import org.modelmapper.ModelMapper; // Inyección nativa del mapeador del proyecto
import org.springframework.beans.factory.annotation.Autowired;
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
import com.epicentrogourmet.hito2.entity.Usuario;
import com.epicentrogourmet.hito2.repositories.PedidoRepository;
import com.epicentrogourmet.hito2.repositories.PlatoRepository;
import com.epicentrogourmet.hito2.repositories.UnidadDeVentaRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/pedidos")
public class PedidoController {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private PlatoRepository platoRepository;

    @Autowired
    private UnidadDeVentaRepository unidadDeVentaRepository;

    // Inyectamos el bean de ModelMapper que tienen configurado en el proyecto
    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/nuevo")
    public String mostrarFormularioPedido(HttpSession session, Model model) {
        // ... (Tu GET estructurado con el DTO que ya dejamos impecable en la respuesta anterior) ...
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) return "redirect:/login";
        if (!usuarioLogueado.getRol().getNombreRol().equals("ADMINISTRADOR") && !usuarioLogueado.getRol().getNombreRol().equals("EMPLEADO_CAJERO")) return "redirect:/home?errorAcceso=true";

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
    public String guardarPedido(@ModelAttribute("pedidoForm") PedidoFormDTO pedidoForm, HttpSession session) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) {
            return "redirect:/login";
        }

        if (!usuarioLogueado.getRol().getNombreRol().equals("ADMINISTRADOR") && 
            !usuarioLogueado.getRol().getNombreRol().equals("EMPLEADO_CAJERO")) {
            return "redirect:/home?errorAcceso=true";
        }

        // 1. Usamos ModelMapper para transformar el DTO en la Entidad Base Pedido de forma automática
        Pedido pedido = modelMapper.map(pedidoForm, Pedido.class);
        
        // Seteamos los campos operacionales que no vienen del formulario
        pedido.setFecha(LocalDate.now());
        pedido.setEstado("ENTREGADO");
        pedido.setItems(new HashSet<>());

        // Buscamos la Unidad de Venta real para amarrarla por JPA
        UnidadDeVenta unidad = unidadDeVentaRepository.findById(pedidoForm.getIdUnidadVenta())
                .orElseThrow(() -> new RuntimeException("Unidad de venta no encontrada"));
        pedido.setUnidadVenta(unidad);

        double totalAcumulado = 0.0;

        // 2. Recorremos los ítems del DTO para filtrar los que tienen cantidad > 0
        for (ItemFormDTO itemDto : pedidoForm.getItems()) {
            if (itemDto.getCantidad() > 0) {
                Plato plato = platoRepository.findById(itemDto.getIdPlato())
                        .orElseThrow(() -> new RuntimeException("Plato no encontrado"));

                // ¡Mágia de ModelMapper!: Mapea el renglón DTO directo a la entidad ItemPedido
                ItemPedido itemEntidad = modelMapper.map(itemDto, ItemPedido.class);
                
                // Aseguramos los amarres relacionales estrictos de Hibernate
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

        // Asignamos el precio final consolidado y guardamos de forma limpia
        pedido.setTotal(totalAcumulado);
        pedidoRepository.save(pedido); 

        return "redirect:/home?pedidoExito=true";
    }
}