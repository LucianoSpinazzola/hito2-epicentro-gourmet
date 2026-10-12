package com.epicentrogourmet.hito2.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "puesto_desarmable")
@DiscriminatorValue("PUESTO_DESARMABLE")
@Getter
@Setter
@NoArgsConstructor
public class PuestoDesarmable extends UnidadDeVenta {

	@Column(nullable = false)
	private Integer cantidadCarpas;

	@Column(nullable = false)
	private Integer tiempoMontaje;

}
