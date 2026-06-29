package sn.ism.wallet.wallet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PaymentRequest {

    @NotBlank(message = "Le numero de telephone est obligatoire")
    private String phoneNumber;

    @NotBlank(message = "Le nom du service est obligatoire")
    private String serviceName;

    @NotNull(message = "Le montant est obligatoire")
    @Positive(message = "Le montant doit etre positif")
    private BigDecimal amount;
}
