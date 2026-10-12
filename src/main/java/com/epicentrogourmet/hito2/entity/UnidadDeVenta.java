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

}
