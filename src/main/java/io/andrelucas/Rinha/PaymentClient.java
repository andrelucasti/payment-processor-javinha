package io.andrelucas.Rinha;


import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import io.andrelucas.Rinha.paymentsummary.PaymentIntegrationType;


public interface PaymentClient {

    public record PaymentRequest(UUID correlationId, BigDecimal amount, Instant requestedAt) {}
    public record PaymentResult(PaymentIntegrationType integrationType, Instant requestedAt) {}

    PaymentResult processPayment(PaymentRequest payment);

}
