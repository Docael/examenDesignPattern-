package sn.ism.wallet.wallet.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class WalletRequest {

    @NotBlank(message = "Le numero de telephone est obligatoire")
    private String phoneNumber;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email n'est pas valide")
    private String email;

    @PositiveOrZero(message = "Le solde initial ne peut pas etre negatif")
    private BigDecimal initialBalance;

    /** Optionnel : si non fourni, un code est genere automatiquement. */
    private String code;

    /** Optionnel : devise (XOF par defaut). */
    private String currency;
}
