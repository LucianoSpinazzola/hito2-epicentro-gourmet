package com.epicentrogourmet.hito2.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cocinero")
@DiscriminatorValue("COCINERO")
@Getter
@Setter
@NoArgsConstructor
public class Cocinero extends Empleado {

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private RolCocina rolCocina;

	@Column(nullable = false)
	private Double plusCategoria;

}
