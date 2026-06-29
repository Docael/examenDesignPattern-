
package sn.ism.wallet.wallet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.ism.wallet.common.dto.ApiResponse;
import sn.ism.wallet.exception.ResourceNotFoundException;
import sn.ism.wallet.wallet.client.FactureDto;
import sn.ism.wallet.wallet.client.PaymentClient;
import sn.ism.wallet.wallet.model.Wallet;
import sn.ism.wallet.wallet.repository.WalletRepository;

import java.util.List;

/**
 * Proxy vers le payment-service : permet de consulter les factures
 * d'un utilisateur a partir de son numero de telephone.
 * Branche : feature/proxy-factures
 */
@RestController
@RequestMapping("/external/factures")
@RequiredArgsConstructor
public class FactureProxyController {

    private final PaymentClient paymentClient;
    private final WalletRepository walletRepository;

    // GET /external/factures/current?phone=...
    @GetMapping("/current")
    public ResponseEntity<ApiResponse<List<FactureDto>>> current(
            @RequestParam String phone,
            @RequestParam(required = false) String unite) {
        String code = resolveWalletCode(phone);
        List<FactureDto> factures = (unite != null && !unite.isBlank())
                ? paymentClient.getCurrentFacturesByService(code, unite)
                : paymentClient.getCurrentFactures(code);
        return ResponseEntity.ok(ApiResponse.ok("Factures du mois courant", factures));
    }

    // GET /external/factures/periode?phone=...&start=...&end=...
    @GetMapping("/periode")
    public ResponseEntity<ApiResponse<List<FactureDto>>> periode(
            @RequestParam String phone,
            @RequestParam String start,
            @RequestParam String end) {
        String code = resolveWalletCode(phone);
        return ResponseEntity.ok(ApiResponse.ok("Factures sur la periode",
                paymentClient.getFacturesByPeriode(code, start, end)));
    }

    private String resolveWalletCode(String phone) {
        Wallet wallet = walletRepository.findByPhoneNumber(phone)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Aucun portefeuille trouve pour le numero " + phone));
        return wallet.getCode();
    }
}
