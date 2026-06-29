package sn.ism.wallet.wallet.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.ism.wallet.common.dto.ApiResponse;
import sn.ism.wallet.wallet.dto.BalanceResponse;
import sn.ism.wallet.wallet.dto.TransactionResponse;
import sn.ism.wallet.wallet.dto.WalletRequest;
import sn.ism.wallet.wallet.dto.WalletResponse;
import sn.ism.wallet.wallet.service.TransactionService;
import sn.ism.wallet.wallet.service.WalletService;

import java.util.List;

@RestController
@RequestMapping("/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;
    private final TransactionService transactionService;

    // feature/wallet-creation -> POST /wallets
    @PostMapping
    public ResponseEntity<ApiResponse<WalletResponse>> create(@Valid @RequestBody WalletRequest request) {
        WalletResponse response = walletService.createWallet(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Portefeuille cree avec succes", response));
    }

    // feature/wallet-listing -> GET /wallets
    @GetMapping
    public ResponseEntity<ApiResponse<List<WalletResponse>>> findAll() {
        return ResponseEntity.ok(ApiResponse.ok("Liste des portefeuilles", walletService.findAll()));
    }

    // feature/wallet-consultation -> GET /wallets/{phone}
    @GetMapping("/{phone}")
    public ResponseEntity<ApiResponse<WalletResponse>> findByPhone(@PathVariable String phone) {
        return ResponseEntity.ok(ApiResponse.ok("Portefeuille trouve", walletService.findByPhone(phone)));
    }

    // feature/wallet-consultation -> GET /wallets/{phone}/balance
    @GetMapping("/{phone}/balance")
    public ResponseEntity<ApiResponse<BalanceResponse>> getBalance(@PathVariable String phone) {
        return ResponseEntity.ok(ApiResponse.ok("Solde du portefeuille", walletService.getBalance(phone)));
    }

    // feature/transaction-history -> GET /wallets/{phone}/transactions
    @GetMapping("/{phone}/transactions")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> history(@PathVariable String phone) {
        return ResponseEntity.ok(ApiResponse.ok("Historique des transactions",
                transactionService.history(phone)));
    }

    // feature/wallet-seeder -> POST /wallets/seed
    @PostMapping("/seed")
    public ResponseEntity<ApiResponse<List<WalletResponse>>> seed() {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Donnees de demonstration injectees", transactionService.seed()));
    }
}
