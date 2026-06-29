package sn.ism.wallet.wallet.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * Reponse du payment-service apres reglement de factures.
 */
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaidFactureDto {
    private String walletCode;
    private List<String> references;
    private BigDecimal totalAmount;
    private Integer count;
    private String status;
}
