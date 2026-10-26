package com.epicentrogourmet.hito2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private final UsuarioDetailsService usuarioDetailsService;

	public SecurityConfig(UsuarioDetailsService usuarioDetailsService) {
		this.usuarioDetailsService = usuarioDetailsService;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(
				authorize -> authorize
						// 1. Recursos estáticos y páginas públicas (Landing y Login)
						.requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
						.requestMatchers("/", "/login", "/error").permitAll()
						
						// 2. Panel de control (Dashboard) requiere estar logueado
						.requestMatchers("/home").authenticated()   // <--- /home ESTÁ PROTEGIDO
						
						// 3. Rutas de ADMIN
						.requestMatchers("/admin/**").hasRole("ADMIN")
						.requestMatchers("/reportes/**").hasRole("ADMIN")
						
						// 4. Rutas de RESPONSABLE
						.requestMatchers("/responsable/**").hasRole("RESPONSABLE")
						
						// 5. Rutas de EMPLEADO y mixtas
						.requestMatchers("/empleados/mi-sueldo", "/empleados/descargar-sueldo").hasRole("EMPLEADO")
						.requestMatchers("/empleados/nuevo").hasAnyRole("RESPONSABLE", "ADMIN")
						.requestMatchers("/pedidos/**").hasRole("EMPLEADO")
						
						// 6. Cualquier otra ruta
						.anyRequest().authenticated())
				.formLogin(form -> form.loginPage("/login").loginProcessingUrl("/login")
						.defaultSuccessUrl("/home", true).failureUrl("/login?error=true").permitAll())
				.logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/login?logout=true")
						.invalidateHttpSession(true).deleteCookies("JSESSIONID").permitAll());
		return http.build();
	}
}

