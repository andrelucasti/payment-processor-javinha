package io.andrelucas.Rinha;


import java.time.Instant;

import io.andrelucas.Rinha.payment.Payment;
import io.andrelucas.Rinha.paymentsummary.PaymentIntegrationType;
import reactor.core.publisher.Mono;

public interface PaymentClient {

    public record PaymentResult(PaymentIntegrationType integrationType, Instant requestedAt) {}

    Mono<PaymentResult> processPayment(Payment payment);

}
