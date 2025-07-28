package io.andrelucas.Rinha;


import java.math.BigDecimal;
import java.time.Instant;
import io.andrelucas.Rinha.paymentsummary.PaymentIntegrationType;
import reactor.core.publisher.Mono;


public interface PaymentClient {

    public record PaymentRequest(String correlationId, BigDecimal amount, Instant requestedAt) {}
    public record PaymentResult(PaymentIntegrationType integrationType, Instant requestedAt) {}

    Mono<PaymentClient.PaymentResult> processPayment(PaymentRequest payment);

}
