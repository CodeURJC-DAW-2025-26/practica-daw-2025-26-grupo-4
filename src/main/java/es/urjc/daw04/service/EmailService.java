package es.urjc.daw04.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendWelcomeEmail(String recipient, String username) {
        try {
            System.out.println("Attempting to send email to: " + recipient);
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(recipient);
            message.setSubject("¡Bienvenido a PlantaZon!");
            message.setText(createEmailContent(username));
            message.setFrom("plantazon417@gmail.com");

            mailSender.send(message);
            System.out.println("Email sent successfully to: " + recipient);
        } catch (Exception e) {
            System.err.println("Error sending email to " + recipient + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String createEmailContent(String username) {
        return "Hola " + username + ",\n\n" +
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
