package sn.ism.payment.payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.ism.payment.payment.dto.FactureResponse;
import sn.ism.payment.payment.service.PaymentService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/factures")
@RequiredArgsConstructor
public class FactureController {

    private final PaymentService paymentService;

    // GET /factures/current?walletCode=...            (toutes les factures du mois)
    // GET /factures/current?walletCode=...&unite=...  (filtre par service)
    @GetMapping("/current")
    public ResponseEntity<List<FactureResponse>> current(
            @RequestParam String walletCode,
            @RequestParam(required = false) String unite) {
        List<FactureResponse> factures = (unite != null && !unite.isBlank())
                ? paymentService.findCurrentByService(walletCode, unite)
                : paymentService.findCurrent(walletCode);
        return ResponseEntity.ok(factures);
    }

    // GET /factures/periode?walletCode=...&start=yyyy-MM-dd&end=yyyy-MM-dd
    @GetMapping("/periode")
    public ResponseEntity<List<FactureResponse>> periode(
            @RequestParam String walletCode,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(paymentService.findBetweenDates(walletCode, start, end));
    }

    // GET /factures?references=FAC-1,FAC-2  (consomme par le wallet avant debit)
    @GetMapping
    public ResponseEntity<List<FactureResponse>> byReferences(@RequestParam List<String> references) {
        return ResponseEntity.ok(paymentService.findByReferences(references));
    }
}
