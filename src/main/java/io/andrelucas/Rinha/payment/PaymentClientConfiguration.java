package io.andrelucas.Rinha.payment;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class PaymentClientConfiguration {

    @Bean
    public WebClient paymentProcessorDefault() {
        return WebClient.builder()
        .baseUrl("http://localhost:8001")
        .defaultHeader("Content-Type", "application/json")
        .build();
    }

    @Bean
    public WebClient paymentProcessorFallback() {
        return WebClient.builder()
        .baseUrl("http://localhost:8002")
        .defaultHeader("Content-Type", "application/json")
        .build();
    }
}
