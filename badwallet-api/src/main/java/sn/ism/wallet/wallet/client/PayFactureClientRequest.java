package sn.ism.wallet.wallet.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Corps envoye au payment-service pour regler des factures.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PayFactureClientRequest {
    private String walletCode;
    private String serviceName;
    private List<String> references;
}
