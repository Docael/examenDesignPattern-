package sn.ism.wallet.wallet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PayFactureRequest {

    @NotBlank(message = "Le numero de telephone est obligatoire")
    private String phoneNumber;

    /** ex : SENELEC, SDE, SONATEL... */
    private String serviceName;

    @NotEmpty(message = "Au moins une reference de facture est requise")
    private List<String> factureReferences;
}
