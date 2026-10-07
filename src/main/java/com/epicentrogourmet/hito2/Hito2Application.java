package com.epicentrogourmet.hito2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan; // <-- FIJATE SI TE PONE ESTE IMPORT SOLO, SINO AGREGALO

@SpringBootApplication
@ComponentScan(basePackages = {"com.epicentrogourmet.hito2"}) // <-- ESTO OBLIGA A BUSCAR EN TODO EL PROYECTO
public class Hito2Application {

    public static void main(String[] eloquence) {
        SpringApplication.run(Hito2Application.class, eloquence);
    }
}
