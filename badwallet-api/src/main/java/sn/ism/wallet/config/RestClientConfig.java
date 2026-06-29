package sn.ism.wallet.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Configuration du client REST utilise pour appeler le payment-service.
 */
@Configuration
public class RestClientConfig {

    @Value("${payment-service.base-url}")
    private String paymentServiceBaseUrl;

    @Bean
    public RestClient paymentRestClient() {
        return RestClient.builder()
                .baseUrl(paymentServiceBaseUrl)
                .build();
    }
}
