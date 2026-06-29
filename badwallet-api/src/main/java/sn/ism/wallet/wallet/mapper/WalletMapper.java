package sn.ism.wallet.wallet.mapper;

import org.springframework.stereotype.Component;
import sn.ism.wallet.wallet.dto.BalanceResponse;
import sn.ism.wallet.wallet.dto.WalletRequest;
import sn.ism.wallet.wallet.dto.WalletResponse;
import sn.ism.wallet.wallet.model.Wallet;

import java.math.BigDecimal;

@Component
public class WalletMapper {

    public Wallet toEntity(WalletRequest request) {
        return Wallet.builder()
                .phoneNumber(request.getPhoneNumber())
                .email(request.getEmail())
                .balance(request.getInitialBalance() != null ? request.getInitialBalance() : BigDecimal.ZERO)
                .currency(request.getCurrency() != null && !request.getCurrency().isBlank()
                        ? request.getCurrency() : "XOF")
                .build();
    }

    public WalletResponse toResponse(Wallet wallet) {
        return WalletResponse.builder()
                .id(wallet.getId())
                .phoneNumber(wallet.getPhoneNumber())
                .email(wallet.getEmail())
                .code(wallet.getCode())
                .balance(wallet.getBalance())
                .currency(wallet.getCurrency())
                .createdAt(wallet.getCreatedAt())
                .build();
    }

    public BalanceResponse toBalanceResponse(Wallet wallet) {
        return BalanceResponse.builder()
                .phoneNumber(wallet.getPhoneNumber())
                .balance(wallet.getBalance())
                .currency(wallet.getCurrency())
                .build();
    }
}
