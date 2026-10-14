package com.epicentrogourmet.hito2.services;

import java.util.List;
import com.epicentrogourmet.hito2.entity.Empleado;

public interface IEmpleadoService {
    
    
    List<Empleado> listarActivos();
    
    
    Empleado darDeAlta(Empleado empleado, String emailUsuario);
    
    
    void eliminarLogico(Long id);
    
    
    double calcularSueldoTotal(Long idEmpleado, int diasDuracionFestival, Long idFestival);
}