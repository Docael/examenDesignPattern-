package sn.ism.payment.payment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PayFactureRequest {

    @NotBlank(message = "Le code du portefeuille est obligatoire")
    private String walletCode;

    /** Optionnel : filtre par service (ex : SENELEC). */
    private String serviceName;

    /** References precises a payer. Si vide -> factures du mois courant. */
    private List<String> references;
}
