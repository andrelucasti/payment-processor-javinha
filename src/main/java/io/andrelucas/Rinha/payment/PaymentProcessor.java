package io.andrelucas.Rinha.payment;

import java.time.Instant;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import io.andrelucas.Rinha.paymentsummary.PaymentIntegrationType;
import io.andrelucas.Rinha.PaymentClient;   

@Service
public class PaymentProcessor {

    private final PaymentClient paymentClient;
    private final RedisTemplate<String, String> paymentTemplate;

    public PaymentProcessor(final PaymentClient paymentClient, final RedisTemplate<String, String> paymentTemplate) {
        this.paymentClient = paymentClient;
        this.paymentTemplate = paymentTemplate;
    }

    public void processPayments(final Payment payment) {
        final var paymentRequest = new PaymentClient.PaymentRequest(payment.correlationId(), payment.amount(), Instant.now());
        final var paymentResult = paymentClient.processPayment(paymentRequest);

        if (paymentResult.integrationType() == PaymentIntegrationType.DEFAULT) {
            paymentTemplate.opsForValue().increment("payments_default:count", 1);
            paymentTemplate.opsForValue().increment("payments_default:total", payment.amount().doubleValue());
        } else {
            paymentTemplate.opsForValue().increment("payments_fallback:count", 1);
            paymentTemplate.opsForValue().increment("payments_fallback:total", payment.amount().doubleValue());
        }       
    }


}
