package sn.ism.payment.payment.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import sn.ism.payment.common.enums.FactureStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class FactureResponse {
    private Long id;
    private String reference;
    private String walletCode;
    private String serviceName;
    private BigDecimal amount;
    private Integer month;
    private Integer year;
    private FactureStatus status;
    private LocalDateTime createdAt;
}
