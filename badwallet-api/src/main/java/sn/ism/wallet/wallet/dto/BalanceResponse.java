package sn.ism.wallet.wallet.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class BalanceResponse {
    private String phoneNumber;
    private BigDecimal balance;
    private String currency;
}
