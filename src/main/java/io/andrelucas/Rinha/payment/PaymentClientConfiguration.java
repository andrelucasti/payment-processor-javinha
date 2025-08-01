package io.andrelucas.Rinha.payment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class PaymentClientConfiguration {

    @Value("${payment-processor.default.base-url}")
    private String paymentProcessorDefaultBaseUrl;

    @Value("${payment-processor.fallback.base-url}")
    private String paymentProcessorFallbackBaseUrl;

    @Bean
    public WebClient paymentProcessorDefault() {
        return WebClient.builder()
        .baseUrl(paymentProcessorDefaultBaseUrl)
        .defaultHeader("Content-Type", "application/json")
        .build();
    }

    @Bean
    public WebClient paymentProcessorFallback() {
        return WebClient.builder()
        .baseUrl(paymentProcessorFallbackBaseUrl)
        .defaultHeader("Content-Type", "application/json")
        .build();
    }
}
