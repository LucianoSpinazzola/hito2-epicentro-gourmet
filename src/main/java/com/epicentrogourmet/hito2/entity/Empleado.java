package com.epicentrogourmet.hito2.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "empleado")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "tipo_empleado")
@Getter
@Setter
@NoArgsConstructor
public abstract class Empleado {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idEmpleado;

	@Column(nullable = false)
	private String nombre;

	@Column(nullable = false)
	private String apellido;

	@Column(nullable = false, unique = true)
	private Integer dni;

	@Column(nullable = false)
	private LocalDate fechaNacimiento;

	@Column(nullable = false)
	private LocalDate fechaIngreso;

	@Column(nullable = false)
	private Double sueldoBase;

	@Column(nullable = false)
	private boolean eliminado = false;

	@OneToOne
	@JoinColumn(name = "id_usuario")
	private Usuario usuario;

}
