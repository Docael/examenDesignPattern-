package sn.ism.wallet.wallet.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sn.ism.wallet.common.enums.TransactionType;
import sn.ism.wallet.exception.BusinessException;
import sn.ism.wallet.wallet.client.PaymentClient;
import sn.ism.wallet.wallet.dto.DepositRequest;
import sn.ism.wallet.wallet.dto.TransactionResponse;
import sn.ism.wallet.wallet.dto.TransferRequest;
import sn.ism.wallet.wallet.dto.WithdrawRequest;
import sn.ism.wallet.wallet.mapper.TransactionMapper;
import sn.ism.wallet.wallet.mapper.WalletMapper;
import sn.ism.wallet.wallet.model.Transaction;
import sn.ism.wallet.wallet.model.Wallet;
import sn.ism.wallet.wallet.repository.TransactionRepository;
import sn.ism.wallet.wallet.repository.WalletRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private WalletRepository walletRepository;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private PaymentClient paymentClient;

    private TransactionServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new TransactionServiceImpl(
                walletRepository,
                transactionRepository,
                new TransactionMapper(),
                new WalletMapper(),
                paymentClient);
    }

    private Wallet wallet(String phone, String balance) {
        return Wallet.builder()
                .phoneNumber(phone)
                .code("WAL-" + phone)
                .currency("XOF")
                .balance(new BigDecimal(balance))
                .build();
    }

    @Test
    @DisplayName("deposit : augmente le solde du portefeuille")
    void deposit_shouldIncreaseBalance() {
        Wallet w = wallet("770000001", "1000");
        when(walletRepository.findByPhoneNumber("770000001")).thenReturn(Optional.of(w));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));

        DepositRequest req = new DepositRequest();
        req.setAmount(new BigDecimal("500"));
        req.setPaymentMethod("WAVE");

        TransactionResponse resp = service.deposit("770000001", req);

        assertThat(w.getBalance()).isEqualByComparingTo("1500");
        assertThat(resp.getType()).isEqualTo(TransactionType.DEPOSIT);
        assertThat(resp.getAmount()).isEqualByComparingTo("500");
    }

    @Test
    @DisplayName("withdraw : echoue si le solde est insuffisant")
    void withdraw_shouldThrow_whenInsufficientBalance() {
        Wallet w = wallet("770000001", "100");
        when(walletRepository.findByPhoneNumber("770000001")).thenReturn(Optional.of(w));

        WithdrawRequest req = new WithdrawRequest();
        req.setPhoneNumber("770000001");
        req.setAmount(new BigDecimal("5000"));

        assertThrows(BusinessException.class, () -> service.withdraw(req));
    }

    @Test
    @DisplayName("transfer : debite l'emetteur (montant + frais) et credite le destinataire")
    void transfer_shouldMoveFunds() {
        Wallet sender = wallet("A", "10000");
        Wallet receiver = wallet("B", "0");
        when(walletRepository.findByPhoneNumber("A")).thenReturn(Optional.of(sender));
        when(walletRepository.findByPhoneNumber("B")).thenReturn(Optional.of(receiver));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));

        TransferRequest req = new TransferRequest();
        req.setSenderPhone("A");
        req.setReceiverPhone("B");
        req.setAmount(new BigDecimal("2000"));

        service.transfer(req);

        // frais = 1% de 2000 = 20 -> l'emetteur paie 2020
        assertThat(sender.getBalance()).isEqualByComparingTo("7980");
        assertThat(receiver.getBalance()).isEqualByComparingTo("2000");
    }

    @Test
    @DisplayName("transfer : echoue si emetteur = destinataire")
    void transfer_shouldThrow_whenSameWallet() {
        TransferRequest req = new TransferRequest();
        req.setSenderPhone("A");
        req.setReceiverPhone("A");
        req.setAmount(new BigDecimal("1000"));

        assertThrows(BusinessException.class, () -> service.transfer(req));
    }
}
