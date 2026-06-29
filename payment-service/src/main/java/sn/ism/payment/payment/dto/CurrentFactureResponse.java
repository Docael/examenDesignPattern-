package sn.ism.payment.payment.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * Vue synthetique des factures du mois courant pour un portefeuille.
 */
@Getter
@Setter
@Builder
public class CurrentFactureResponse {
    private String walletCode;
    private Integer month;
    private Integer year;
    private Integer count;
    private BigDecimal totalAmount;
    private List<FactureResponse> factures;
}
