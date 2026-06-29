package sn.ism.payment.payment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.ism.payment.payment.dto.PaidFactureResponse;
import sn.ism.payment.payment.dto.PayFactureRequest;
import sn.ism.payment.payment.service.PaymentService;

@RestController
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // POST /pay  -> regle les factures du mois courant
    @PostMapping("/pay")
    public ResponseEntity<PaidFactureResponse> pay(@Valid @RequestBody PayFactureRequest request) {
        return ResponseEntity.ok(paymentService.payCurrentFacture(request));
    }

    // POST /pay-factures  -> regle des factures par reference (appele par le wallet)
    @PostMapping("/pay-factures")
    public ResponseEntity<PaidFactureResponse> payFactures(@Valid @RequestBody PayFactureRequest request) {
        return ResponseEntity.ok(paymentService.payFactures(request));
    }
}
