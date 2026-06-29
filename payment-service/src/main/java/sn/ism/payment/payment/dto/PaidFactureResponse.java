package sn.ism.payment.payment.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
public class PaidFactureResponse {
    private String walletCode;
    private List<String> references;
    private BigDecimal totalAmount;
    private Integer count;
    private String status;
}
