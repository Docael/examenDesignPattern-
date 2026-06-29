package sn.ism.wallet.wallet.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sn.ism.wallet.exception.BusinessException;
import sn.ism.wallet.exception.ResourceNotFoundException;
import sn.ism.wallet.wallet.dto.WalletRequest;
import sn.ism.wallet.wallet.dto.WalletResponse;
import sn.ism.wallet.wallet.mapper.WalletMapper;
import sn.ism.wallet.wallet.model.Wallet;
import sn.ism.wallet.wallet.repository.WalletRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WalletServiceImplTest {

    @Mock
    private WalletRepository walletRepository;

    private WalletServiceImpl walletService;

    @BeforeEach
    void setUp() {
        // On utilise le vrai mapper (simple) et on mocke le repository
        walletService = new WalletServiceImpl(walletRepository, new WalletMapper());
    }

    @Test
    @DisplayName("createWallet : cree un portefeuille avec un code genere")
    void createWallet_shouldSucceed() {
        WalletRequest request = new WalletRequest();
        request.setPhoneNumber("770000001");
        request.setEmail("test@ism.sn");
        request.setInitialBalance(new BigDecimal("1000"));

        when(walletRepository.existsByPhoneNumber("770000001")).thenReturn(false);
        when(walletRepository.existsByEmail("test@ism.sn")).thenReturn(false);
        when(walletRepository.findByCode(anyString())).thenReturn(Optional.empty());
        when(walletRepository.save(any(Wallet.class))).thenAnswer(inv -> {
            Wallet w = inv.getArgument(0);
            w.setId(1L);
            return w;
        });

        WalletResponse response = walletService.createWallet(request);

        assertThat(response).isNotNull();
        assertThat(response.getPhoneNumber()).isEqualTo("770000001");
        assertThat(response.getBalance()).isEqualByComparingTo("1000");
        assertThat(response.getCode()).startsWith("WAL-");
    }

    @Test
    @DisplayName("createWallet : echoue si le numero existe deja")
    void createWallet_shouldThrow_whenPhoneAlreadyExists() {
        WalletRequest request = new WalletRequest();
        request.setPhoneNumber("770000001");
        request.setEmail("test@ism.sn");

        when(walletRepository.existsByPhoneNumber("770000001")).thenReturn(true);

        assertThrows(BusinessException.class, () -> walletService.createWallet(request));
    }

    @Test
    @DisplayName("getBalance : echoue si le portefeuille est introuvable")
    void getBalance_shouldThrow_whenWalletNotFound() {
        when(walletRepository.findByPhoneNumber("000")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> walletService.getBalance("000"));
    }
}
