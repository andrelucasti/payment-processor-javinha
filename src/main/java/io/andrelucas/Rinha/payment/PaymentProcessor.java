package io.andrelucas.Rinha.payment;

import java.time.Instant;

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
    private final ObjectMapper objectMapper;

    public PaymentProcessor(final PaymentClient paymentClient, final RedisTemplate<String, String> paymentTemplate, final ObjectMapper objectMapper) {
        this.paymentClient = paymentClient;
        this.paymentTemplate = paymentTemplate;
        this.objectMapper = objectMapper;
    }

    public void processPayments(final Payment payment) {
        final var paymentRequest = new PaymentClient.PaymentRequest(payment.correlationId().toString(), payment.amount(), Instant.now());
        
        paymentClient.processPayment(paymentRequest)
            .subscribe(paymentResult -> {
                final var correlationId = paymentRequest.correlationId();
                final var requestedAt = paymentResult.requestedAt();
                final var paymentScore = new PaymentScore(correlationId, payment.amount().doubleValue());

                if (paymentResult.integrationType() == PaymentIntegrationType.DEFAULT) {
                    paymentTemplate.opsForZSet().add("payments_default:sorted", toJson(paymentScore), requestedAt.toEpochMilli());
                } else {
                    paymentTemplate.opsForZSet().add("payments_fallback:sorted", toJson(paymentScore), requestedAt.toEpochMilli());
                }
            });
    }

    private String toJson(final PaymentScore obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
