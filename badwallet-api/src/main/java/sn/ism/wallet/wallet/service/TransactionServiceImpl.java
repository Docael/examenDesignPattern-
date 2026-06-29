package sn.ism.wallet.wallet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.ism.wallet.common.enums.TransactionStatus;
import sn.ism.wallet.common.enums.TransactionType;
import sn.ism.wallet.common.utils.FeeCalculator;
import sn.ism.wallet.exception.BusinessException;
import sn.ism.wallet.exception.ResourceNotFoundException;
import sn.ism.wallet.wallet.client.FactureDto;
import sn.ism.wallet.wallet.client.PaidFactureDto;
import sn.ism.wallet.wallet.client.PayFactureClientRequest;
import sn.ism.wallet.wallet.client.PaymentClient;
import sn.ism.wallet.wallet.dto.*;
import sn.ism.wallet.wallet.mapper.TransactionMapper;
import sn.ism.wallet.wallet.mapper.WalletMapper;
import sn.ism.wallet.wallet.model.Transaction;
import sn.ism.wallet.wallet.model.Wallet;
import sn.ism.wallet.wallet.repository.TransactionRepository;
import sn.ism.wallet.wallet.repository.WalletRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final WalletMapper walletMapper;
    private final PaymentClient paymentClient;

    // ---------------------------------------------------------------------
    // DEPOSIT  (branche feature/transaction-deposit)
    // ---------------------------------------------------------------------
    @Override
    @Transactional
    public TransactionResponse deposit(String phoneNumber, DepositRequest request) {
        Wallet wallet = getWallet(phoneNumber);

        BigDecimal fees = FeeCalculator.compute(TransactionType.DEPOSIT, request.getAmount());
        wallet.setBalance(wallet.getBalance().add(request.getAmount()));
        walletRepository.save(wallet);

        Transaction tx = Transaction.builder()
                .type(TransactionType.DEPOSIT)
                .amount(request.getAmount())
                .fees(fees)
                .status(TransactionStatus.SUCCESS)
                .description("Depot via " + (request.getPaymentMethod() != null ? request.getPaymentMethod() : "N/A"))
                .receiverWallet(wallet)
                .build();

        return transactionMapper.toResponse(transactionRepository.save(tx));
    }

    // ---------------------------------------------------------------------
    // WITHDRAW  (branche feature/transaction-withdraw)
    // ---------------------------------------------------------------------
    @Override
    @Transactional
    public TransactionResponse withdraw(WithdrawRequest request) {
        Wallet wallet = getWallet(request.getPhoneNumber());

        BigDecimal fees = FeeCalculator.compute(TransactionType.WITHDRAW, request.getAmount());
        BigDecimal total = request.getAmount().add(fees);

        ensureSufficientBalance(wallet, total);
        wallet.setBalance(wallet.getBalance().subtract(total));
        walletRepository.save(wallet);

        Transaction tx = Transaction.builder()
                .type(TransactionType.WITHDRAW)
                .amount(request.getAmount())
                .fees(fees)
                .status(TransactionStatus.SUCCESS)
                .description("Retrait")
                .senderWallet(wallet)
                .build();

        return transactionMapper.toResponse(transactionRepository.save(tx));
    }

    // ---------------------------------------------------------------------
    // TRANSFER  (branche feature/transaction-transfer)
    // ---------------------------------------------------------------------
    @Override
    @Transactional
    public TransactionResponse transfer(TransferRequest request) {
        if (request.getSenderPhone().equals(request.getReceiverPhone())) {
            throw new BusinessException("Impossible de transferer vers le meme portefeuille");
        }

        Wallet sender = getWallet(request.getSenderPhone());
        Wallet receiver = getWallet(request.getReceiverPhone());

        BigDecimal fees = FeeCalculator.compute(TransactionType.TRANSFER, request.getAmount());
        BigDecimal total = request.getAmount().add(fees);

        ensureSufficientBalance(sender, total);
        sender.setBalance(sender.getBalance().subtract(total));
        receiver.setBalance(receiver.getBalance().add(request.getAmount()));
        walletRepository.save(sender);
        walletRepository.save(receiver);

        Transaction tx = Transaction.builder()
                .type(TransactionType.TRANSFER)
                .amount(request.getAmount())
                .fees(fees)
                .status(TransactionStatus.SUCCESS)
                .description("Transfert de " + sender.getPhoneNumber() + " vers " + receiver.getPhoneNumber())
                .senderWallet(sender)
                .receiverWallet(receiver)
                .build();

        return transactionMapper.toResponse(transactionRepository.save(tx));
    }

    // ---------------------------------------------------------------------
    // PAY  (paiement direct d'un service - branche feature/payment-services)
    // ---------------------------------------------------------------------
    @Override
    @Transactional
    public TransactionResponse pay(PaymentRequest request) {
        Wallet wallet = getWallet(request.getPhoneNumber());

        BigDecimal fees = FeeCalculator.compute(TransactionType.PAYMENT, request.getAmount());
        BigDecimal total = request.getAmount().add(fees);

        ensureSufficientBalance(wallet, total);
        wallet.setBalance(wallet.getBalance().subtract(total));
        walletRepository.save(wallet);

        Transaction tx = Transaction.builder()
                .type(TransactionType.PAYMENT)
                .amount(request.getAmount())
                .fees(fees)
                .status(TransactionStatus.SUCCESS)
                .description("Paiement service : " + request.getServiceName())
                .senderWallet(wallet)
                .build();

        return transactionMapper.toResponse(transactionRepository.save(tx));
    }

    // ---------------------------------------------------------------------
    // PAY-FACTURES  (paiement de factures via payment-service)
    // ---------------------------------------------------------------------
    @Override
    @Transactional
    public TransactionResponse payFactures(PayFactureRequest request) {
        Wallet wallet = getWallet(request.getPhoneNumber());

        // 1. Recuperation des factures aupres du payment-service
        List<FactureDto> factures = paymentClient.getFacturesByReferences(request.getFactureReferences());
        if (factures == null || factures.isEmpty()) {
            throw new BusinessException("Aucune facture trouvee pour les references fournies");
        }

        // 2. Calcul du total
        BigDecimal total = factures.stream()
                .map(FactureDto::getAmount)
                .filter(a -> a != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal fees = FeeCalculator.compute(TransactionType.PAYMENT, total);
        BigDecimal grandTotal = total.add(fees);

        // 3. Verification du solde + debit
        ensureSufficientBalance(wallet, grandTotal);
        wallet.setBalance(wallet.getBalance().subtract(grandTotal));
        walletRepository.save(wallet);

        // 4. Marquage des factures comme payees cote payment-service
        PaidFactureDto paid = paymentClient.payFactures(new PayFactureClientRequest(
                wallet.getCode(),
                request.getServiceName(),
                request.getFactureReferences()));

        // 5. Enregistrement de la transaction
        Transaction tx = Transaction.builder()
                .type(TransactionType.PAYMENT)
                .amount(total)
                .fees(fees)
                .status(TransactionStatus.SUCCESS)
                .description("Paiement factures " + request.getServiceName()
                        + " (" + request.getFactureReferences().size() + " facture(s))")
                .senderWallet(wallet)
                .build();

        TransactionResponse response = transactionMapper.toResponse(transactionRepository.save(tx));
        if (paid != null) {
            response.setDescription(response.getDescription()
                    + " | confirme payment-service : " + paid.getStatus());
        }
        return response;
    }

    // ---------------------------------------------------------------------
    // HISTORY  (branche feature/transaction-history)
    // ---------------------------------------------------------------------
    @Override
    @Transactional(readOnly = true)
    public List<TransactionResponse> history(String phoneNumber) {
        Wallet wallet = getWallet(phoneNumber);
        return transactionRepository.findByWalletOrderByDateDesc(wallet).stream()
                .map(transactionMapper::toResponse)
                .toList();
    }

    // ---------------------------------------------------------------------
    // SEED  (branche feature/wallet-seeder)
    // ---------------------------------------------------------------------
    @Override
    @Transactional
    public List<WalletResponse> seed() {
        List<Wallet> samples = new ArrayList<>();
        samples.add(buildWallet("770000001", "awa@ism.sn", "WAL-DEMO0001", new BigDecimal("50000")));
        samples.add(buildWallet("770000002", "modou@ism.sn", "WAL-DEMO0002", new BigDecimal("120000")));
        samples.add(buildWallet("770000003", "fatou@ism.sn", "WAL-DEMO0003", new BigDecimal("0")));

        List<WalletResponse> created = new ArrayList<>();
        for (Wallet w : samples) {
            if (!walletRepository.existsByPhoneNumber(w.getPhoneNumber())) {
                Wallet saved = walletRepository.save(w);
                created.add(walletMapper.toResponse(saved));

                if (saved.getBalance().signum() > 0) {
                    transactionRepository.save(Transaction.builder()
                            .type(TransactionType.DEPOSIT)
                            .amount(saved.getBalance())
                            .fees(BigDecimal.ZERO)
                            .status(TransactionStatus.SUCCESS)
                            .description("Depot initial (seed)")
                            .receiverWallet(saved)
                            .build());
                }
            }
        }
        return created;
    }

    // ---------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------
    private Wallet buildWallet(String phone, String email, String code, BigDecimal balance) {
        return Wallet.builder()
                .phoneNumber(phone)
                .email(email)
                .code(code)
                .currency("XOF")
                .balance(balance)
                .build();
    }

    private Wallet getWallet(String phoneNumber) {
        return walletRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Aucun portefeuille trouve pour le numero " + phoneNumber));
    }

    private void ensureSufficientBalance(Wallet wallet, BigDecimal amount) {
        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new BusinessException("Solde insuffisant. Solde actuel : "
                    + wallet.getBalance() + " " + wallet.getCurrency()
                    + ", montant requis : " + amount);
        }
    }
}
