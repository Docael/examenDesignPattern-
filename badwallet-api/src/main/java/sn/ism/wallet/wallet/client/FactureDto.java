package sn.ism.wallet.wallet.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Representation d'une facture telle que renvoyee par le payment-service.
 */
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FactureDto {
    private Long id;
    private String reference;
    private String walletCode;
    private String serviceName;
    private BigDecimal amount;
    private Integer month;
    private Integer year;
    private String status;
}
