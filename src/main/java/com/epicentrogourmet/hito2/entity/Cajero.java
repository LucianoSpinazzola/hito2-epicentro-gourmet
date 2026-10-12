package com.epicentrogourmet.hito2.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cajero")
@DiscriminatorValue("CAJERO")
@Getter
@Setter
@NoArgsConstructor
public class Cajero extends Empleado {

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Turno turno;

	@Column(nullable = false)
	private Double plusAntiguedad;

}
