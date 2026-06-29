package sn.ism.wallet.wallet.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.ism.wallet.common.dto.ApiResponse;
import sn.ism.wallet.wallet.dto.*;
import sn.ism.wallet.wallet.service.TransactionService;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    // feature/transaction-deposit -> POST /deposit/{phone}
    @PostMapping("/deposit/{phone}")
    public ResponseEntity<ApiResponse<TransactionResponse>> deposit(
            @PathVariable String phone,
            @Valid @RequestBody DepositRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Depot effectue",
                transactionService.deposit(phone, request)));
    }

    // feature/transaction-withdraw -> POST /withdraw
    @PostMapping("/withdraw")
    public ResponseEntity<ApiResponse<TransactionResponse>> withdraw(
            @Valid @RequestBody WithdrawRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Retrait effectue",
                transactionService.withdraw(request)));
    }

    // feature/transaction-transfer -> POST /transfer
    @PostMapping("/transfer")
    public ResponseEntity<ApiResponse<TransactionResponse>> transfer(
            @Valid @RequestBody TransferRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Transfert effectue",
                transactionService.transfer(request)));
    }

    // feature/payment-services -> POST /pay
    @PostMapping("/pay")
    public ResponseEntity<ApiResponse<TransactionResponse>> pay(
            @Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Paiement effectue",
                transactionService.pay(request)));
    }

    // feature/payment-services -> POST /pay-factures
    @PostMapping("/pay-factures")
    public ResponseEntity<ApiResponse<TransactionResponse>> payFactures(
            @Valid @RequestBody PayFactureRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Factures payees",
                transactionService.payFactures(request)));
    }
}
