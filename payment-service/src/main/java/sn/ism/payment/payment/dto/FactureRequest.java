package sn.ism.payment.payment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class FactureRequest {

    private String reference;

    @NotBlank(message = "Le code du portefeuille est obligatoire")
    private String walletCode;

    @NotBlank(message = "Le nom du service est obligatoire")
    private String serviceName;

    @NotNull(message = "Le montant est obligatoire")
    @Positive(message = "Le montant doit etre positif")
    private BigDecimal amount;

    private Integer month;
    private Integer year;
}
