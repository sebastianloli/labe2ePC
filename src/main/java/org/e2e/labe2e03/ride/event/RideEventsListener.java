package org.e2e.labe2e03.ride.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.e2e.labe2e03.email.EmailService;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class RideEventsListener {

    private final EmailService emailService;

    @Async
    @EventListener
    public void on(RideCreatedEvent e) {
        log.info("Enviando correo de confirmación de viaje a: {}", e.passengerEmail());

        try {
            emailService.sendHtml(
                    e.passengerEmail(),
                    "¡Confirmación de viaje - Ride Share E2E!",
                    "ride-created",
                    Map.of(
                            "destinationName", e.destinationName(),
                            "departureDate", e.departureDate(),
                            "price", e.price(),
                            "rideId", e.rideId()
                    )
            );
            log.info("Correo de confirmación enviado exitosamente a: {}", e.passengerEmail());
        } catch (Exception ex) {
            log.error("Error enviando correo de confirmación a {}: {}", e.passengerEmail(), ex.getMessage());
        }
    }

    @Async
    @EventListener
    public void on(HolaEmailEvent e) {
        log.info("Enviando correo de prueba a: {}", e.email());

        try {
            emailService.sendHtml(
                    e.email(),
                    "¡Correo de prueba - Ride Share E2E!",
                    "test-email",
                    Map.of(
                            "email", e.email(),
                            "message", "Este es un correo de prueba desde tu aplicación Spring Boot"
                    )
            );
            log.info("Correo de prueba enviado exitosamente a: {}", e.email());
        } catch (Exception ex) {
            log.error("Error enviando correo de prueba a {}: {}", e.email(), ex.getMessage());
        }
    }
}
