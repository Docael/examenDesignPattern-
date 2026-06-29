package sn.ism.wallet.wallet.client;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

/**
 * Client REST vers le payment-service (port 8081).
 * Branche : feature/proxy-factures + feature/payment-services
 */
@Component
@RequiredArgsConstructor
public class PaymentClient {

    private final RestClient paymentRestClient;

    /** Factures du mois courant pour un code de portefeuille. */
    public List<FactureDto> getCurrentFactures(String walletCode) {
        return paymentRestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/factures/current")
                        .queryParam("walletCode", walletCode)
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<List<FactureDto>>() {});
    }

    /** Factures du mois courant filtrees par service (unite). */
    public List<FactureDto> getCurrentFacturesByService(String walletCode, String unite) {
        return paymentRestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/factures/current")
                        .queryParam("walletCode", walletCode)
                        .queryParam("unite", unite)
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<List<FactureDto>>() {});
    }

    /** Factures sur une periode donnee (format ISO yyyy-MM-dd). */
    public List<FactureDto> getFacturesByPeriode(String walletCode, String start, String end) {
        return paymentRestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/factures/periode")
                        .queryParam("walletCode", walletCode)
                        .queryParam("start", start)
                        .queryParam("end", end)
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<List<FactureDto>>() {});
    }

    /** Recupere des factures par leurs references (utilise avant debit du wallet). */
    public List<FactureDto> getFacturesByReferences(List<String> references) {
        return paymentRestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/factures")
                        .queryParam("references", references)
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<List<FactureDto>>() {});
    }

    /** Demande au payment-service de marquer les factures comme payees. */
    public PaidFactureDto payFactures(PayFactureClientRequest request) {
        return paymentRestClient.post()
                .uri("/pay-factures")
                .body(request)
                .retrieve()
                .body(PaidFactureDto.class);
    }
}
