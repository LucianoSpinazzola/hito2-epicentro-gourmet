package com.epicentrogourmet.hito2;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
class Hito2ApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void generarClaveReal() {
		System.out.println("==================================================");
		System.out.println("HASH REAL: " + new BCryptPasswordEncoder().encode("123456"));
		System.out.println("==================================================");
	}
}