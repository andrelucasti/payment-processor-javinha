package io.andrelucas.Rinha.payment;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.andrelucas.Rinha.paymentsummary.PaymentIntegrationType;
import io.andrelucas.Rinha.PaymentClient;   


@Service
public class PaymentProcessor {

    private final PaymentClient paymentClient;
    private final RedisTemplate<String, String> paymentTemplate;

    record PaymentScore(String correlationId, double amount) {
        public String toJson() {
            try {
                return new ObjectMapper().writeValueAsString(this);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public PaymentProcessor(final PaymentClient paymentClient, final RedisTemplate<String, String> paymentTemplate) {
        this.paymentClient = paymentClient;
        this.paymentTemplate = paymentTemplate;
    }

    public void processPayments(final Payment payment) {
        final var paymentRequest = new PaymentClient.PaymentRequest(payment.correlationId().toString(), payment.amount(), Instant.now());
        
        paymentClient.processPayment(paymentRequest)
            .subscribe(paymentResult -> {
                final var correlationId = paymentRequest.correlationId();
                final var requestedAt = paymentResult.requestedAt();
                final var paymentScore = new PaymentScore(correlationId, payment.amount().doubleValue());

                if (paymentResult.integrationType() == PaymentIntegrationType.DEFAULT) {
                    paymentTemplate.opsForZSet().add("payments_default:sorted", paymentScore.toJson(), requestedAt.toEpochMilli());
                    paymentTemplate.opsForValue().increment("payments_default:count", 1);
                    paymentTemplate.opsForValue().increment("payments_default:total", payment.amount().doubleValue());
                } else {
                    paymentTemplate.opsForZSet().add("payments_fallback:sorted", correlationId, requestedAt.toEpochMilli());
                    paymentTemplate.opsForValue().increment("payments_fallback:count", 1);
                    paymentTemplate.opsForValue().increment("payments_fallback:total", payment.amount().doubleValue());
                }
            });
    }


}
