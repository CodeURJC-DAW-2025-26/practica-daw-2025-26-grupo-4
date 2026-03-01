package es.urjc.daw04.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void enviarCorreoBienvenida(String destinatario, String nombreUsuario) {
        try {
            System.out.println("Intentando enviar correo a: " + destinatario);
            SimpleMailMessage mensaje = new SimpleMailMessage();
            mensaje.setTo(destinatario);
            mensaje.setSubject("¡Bienvenido a PlantaZón!");
            mensaje.setText(crearContenidoCorreo(nombreUsuario));
            mensaje.setFrom("plantazon417@gmail.com");

            mailSender.send(mensaje);
            System.out.println("✓ Correo enviado exitosamente a: " + destinatario);
        } catch (Exception e) {
            System.err.println("✗ Error al enviar correo a " + destinatario + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String crearContenidoCorreo(String nombreUsuario) {
        return "Hola " + nombreUsuario + ",\n\n" +
                "¡Bienvenido a PlantaZon! Nos alegra mucho que te hayas unido a nuestra comunidad.\n\n" +
                "En PlantaZon encontrarás:\n" +
                "✓ Una amplia variedad de plantas para tu hogar\n" +
                "✓ Herramientas de jardinería de calidad\n" +
                "✓ Sustratos y nutrientes para el cuidado de tus plantas\n" +
                "✓ Consejos y recomendaciones personalizadas\n\n" +
                "Tu cuenta ha sido creada exitosamente. Ahora puedes:\n" +
                "- Explorar nuestro catálogo de productos\n" +
                "- Realizar compras\n" +
                "- Ver tus pedidos\n" +
                "- Guardar tus artículos favoritos\n\n" +
                "Si tienes alguna pregunta o necesitas ayuda, no dudes en contactarnos.\n\n" +
                "¡Que disfrutes tu experiencia en PlantaZon!\n\n" +
                "Saludos,\n" +
                "El equipo de PlantaZon";
    }
}
