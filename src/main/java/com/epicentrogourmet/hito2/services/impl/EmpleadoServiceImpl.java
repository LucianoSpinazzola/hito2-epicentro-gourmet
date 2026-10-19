package com.epicentrogourmet.hito2.services.impl;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epicentrogourmet.hito2.entity.Empleado;
import com.epicentrogourmet.hito2.entity.Cajero;
import com.epicentrogourmet.hito2.entity.Cocinero;
import com.epicentrogourmet.hito2.entity.Costo;
import com.epicentrogourmet.hito2.entity.Usuario;
import com.epicentrogourmet.hito2.repositories.CostoRepository;
import com.epicentrogourmet.hito2.repositories.EmpleadoRepository;
import com.epicentrogourmet.hito2.services.IEmpleadoService;

@Service
public class EmpleadoServiceImpl implements IEmpleadoService {

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Override
    public List<Empleado> listarActivos() {
        return empleadoRepository.findByEliminadoFalse();
    }

    @Override
    public Empleado darDeAlta(Empleado empleado, String emailUsuario) {
        
        empleado.setEliminado(false);
        
        
        String contraseniaGenerada = UUID.randomUUID().toString().substring(0, 8);
        
        
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setUserName(emailUsuario);
        nuevoUsuario.setPassword(contraseniaGenerada); 
        
        empleado.setUsuario(nuevoUsuario);
        
        
        System.out.println("----------------------------------------");
        System.out.println("------ EMAIL ENVIADO AL EMPLEADO -------");
        System.out.println("Destinatario: " + emailUsuario);
        System.out.println("Tu usuario es tu email y tu clave es: " + contraseniaGenerada);
        System.out.println("-----------------------------------------");
        
        return empleadoRepository.save(empleado);
    }

    @Override
    public void eliminarLogico(Long id) {
        Empleado empleado = empleadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));
        
        empleado.setEliminado(true);
        empleadoRepository.save(empleado);
    }

    @Autowired
    private CostoRepository costoRepository; 

    @Override
    public double calcularSueldoTotal(Long idEmpleado, int diasDuracionFestival, Long idFestival) {
        Empleado empleado = empleadoRepository.findById(idEmpleado)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));
        
       
        Costo costosFestival = costoRepository.findByFestivalIdFestival(idFestival)
                .orElseThrow(() -> new RuntimeException("No se encontraron costos configurados para este festival"));
        
        
        double sueldoBase = costosFestival.getSueldoBase(); 
        
        if (empleado instanceof Cocinero) {
            Cocinero cocinero = (Cocinero) empleado;
            double plusCategoria = cocinero.getPlusCategoria();
            int factorIntervalos = (diasDuracionFestival / 15) + 1;
            return factorIntervalos * 0.5 * (sueldoBase + plusCategoria);
        } 
        
        if (empleado instanceof Cajero) {
            long anosAntiguedad = ChronoUnit.YEARS.between(empleado.getFechaIngreso(), LocalDate.now());
           
            double montoAntiguedad = anosAntiguedad * costosFestival.getValorAntiguedad();
            
            int factorIntervalos = (diasDuracionFestival / 15) + 1;
            return factorIntervalos * 0.5 * (sueldoBase + montoAntiguedad);
        }
        
        return sueldoBase;
    }
}