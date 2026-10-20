package com.epicentrogourmet.hito2.config;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.epicentrogourmet.hito2.entity.Usuario;
import com.epicentrogourmet.hito2.repositories.UsuarioRepository;

import java.util.List;

@Service
public class UsuarioDetailsService implements UserDetailsService {

	private final UsuarioRepository usuarioRepository;

	public UsuarioDetailsService(UsuarioRepository usuarioRepository) {
		this.usuarioRepository = usuarioRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
		Usuario usuario = usuarioRepository.findByUserName(userName)
				.orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + userName));

		String authority = "ROLE_" + usuario.getRol().getNombreRol();

		return User.builder().username(usuario.getUserName()).password(usuario.getPassword())
				.disabled(!usuario.isActivo()).authorities(List.of(new SimpleGrantedAuthority(authority))).build();
	}
}
