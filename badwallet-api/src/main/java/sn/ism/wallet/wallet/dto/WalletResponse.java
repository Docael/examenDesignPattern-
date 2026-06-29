package sn.ism.wallet.wallet.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class WalletResponse {
    private Long id;
    private String phoneNumber;
    private String email;
    private String code;
    private BigDecimal balance;
    private String currency;
    private LocalDateTime createdAt;
}
