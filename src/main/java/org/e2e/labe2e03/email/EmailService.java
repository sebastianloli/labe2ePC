package org.e2e.labe2e03.email;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public void sendHtml(String to, String subject, String template, Map<String,Object> model) {
        try {
            Context ctx = new Context(); ctx.setVariables(model);
            String html = templateEngine.process(template, ctx);
            MimeMessage mm = mailSender.createMimeMessage();
            MimeMessageHelper h = new MimeMessageHelper(mm, true, "UTF-8");
            h.setTo(to); h.setSubject(subject); h.setText(html, true);
            mailSender.send(mm);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error enviando correo HTML" + e.getMessage(), e);
        }
    }
}
