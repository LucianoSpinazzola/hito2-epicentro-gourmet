package com.epicentrogourmet.hito2.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

	@Autowired(required = false)
	private JavaMailSender mailSender;

	@Value("${SMTP_USERNAME:no-reply@epicentro.com}")
	private String remitente;

	public void enviarCredenciales(String destinatario, String usuario, String clave, String puntoDeVenta) {
		if (mailSender == null) {
			System.out.println("Mail no configurado. No se envio el correo a " + destinatario);
			return;
		}

		SimpleMailMessage mensaje = new SimpleMailMessage();
		mensaje.setFrom(remitente);
		mensaje.setTo(destinatario);
		mensaje.setSubject("Alta en Epicentro Gourmet");
		mensaje.setText("Hola,\n\n" + "Fuiste dado de alta en el sistema Epicentro Gourmet.\n\n" + "Usuario: " + usuario
				+ "\n" + "Clave temporal: " + clave + "\n" + "Punto de venta asignado: " + puntoDeVenta + "\n\n"
				+ "Por seguridad, cambiá la clave después de iniciar sesión.\n\n"
				+ "Saludos,\nEquipo Epicentro Gourmet");

		try {
			mailSender.send(mensaje);
			System.out.println("Mail enviado a " + destinatario);
		} catch (Exception e) {
			System.out.println("No se pudo enviar el mail a " + destinatario + ": " + e.getMessage());
		}
	}
}
