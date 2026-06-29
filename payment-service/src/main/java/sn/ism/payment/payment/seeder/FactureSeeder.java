package sn.ism.payment.payment.seeder;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import sn.ism.payment.payment.service.PaymentService;

/**
 * Injecte des factures de demonstration au demarrage de l'application.
 */
@Component
@RequiredArgsConstructor
public class FactureSeeder implements CommandLineRunner {

    private final PaymentService paymentService;

    @Override
    public void run(String... args) {
        paymentService.seedFactures();
    }
}
