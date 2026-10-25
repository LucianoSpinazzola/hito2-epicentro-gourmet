package com.epicentrogourmet.hito2.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmpleadoFormDTO {

	private String nombre;
	private String apellido;
	private Integer dni;

	// Email del empleado: es su usuario en el sistema y el destinatario del mail de
	// alta.
	private String email;

	private Integer diaNacimiento;
	private Integer mesNacimiento;
	private Integer anioNacimiento;

	private Integer diaIngreso;
	private Integer mesIngreso;
	private Integer anioIngreso;

	private Long idUnidadAsignada;

	// Tipo de empleado: "CAJERO" o "COCINERO".
	// Para CAJERO se usa el turno; para COCINERO se usa la categoria de cocina.
	private String tipoEmpleado;
	private String turno;
	private String rolCocina;
}
