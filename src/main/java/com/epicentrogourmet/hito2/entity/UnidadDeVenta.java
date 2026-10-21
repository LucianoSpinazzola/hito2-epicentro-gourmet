package com.epicentrogourmet.hito2.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "unidad_de_venta")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "tipo_unidad")
@Getter
@Setter
@NoArgsConstructor
public abstract class UnidadDeVenta {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idUnidadDeVenta;

	@Column(nullable = false)
	private String nombreComercial;

	@Column(nullable = false)
	private Double superficie;

	@Column(nullable = false, unique = true, length = 10)
	private String codigo;

	@Column(nullable = false)
	private boolean eliminado = false;

	@ManyToOne
	@JoinColumn(name = "id_responsable", nullable = false)
	private Empleado responsable;

	@ManyToOne
	@JoinColumn(name = "id_festival", nullable = false)
	private Festival festival;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "unidad_venta_plato", joinColumns = @JoinColumn(name = "id_unidad_venta"), inverseJoinColumns = @JoinColumn(name = "id_plato"))
	private java.util.Set<Plato> platos;

	// Staff asignado a esta unidad (lado inverso de Empleado.unidadAsignada).
	// No se cascadea ni se borra en cascada: dar de baja una unidad no debe borrar
	// empleados.
	@OneToMany(mappedBy = "unidadAsignada")
	private java.util.List<Empleado> staff;

}
