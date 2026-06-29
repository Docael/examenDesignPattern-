package sn.ism.wallet.wallet.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class DepositRequest {

    @NotNull(message = "Le montant est obligatoire")
    @Positive(message = "Le montant doit etre positif")
    private BigDecimal amount;

    /** ex : ORANGE_MONEY, WAVE, CARTE_BANCAIRE, ESPECES... */
    private String paymentMethod;
}
