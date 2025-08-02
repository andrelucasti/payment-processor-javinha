package io.andrelucas.Rinha.paymentsummary;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.andrelucas.Rinha.payment.PaymentScore;

@Service
public class GetSummary {

    private final Logger logger = Logger.getLogger(GetSummary.class.getName());
    private final RedisTemplate<String, String> paymentTemplate;
    private final ObjectMapper objectMapper;

    public GetSummary(final RedisTemplate<String, String> paymentTemplate, final ObjectMapper objectMapper) {
        this.paymentTemplate = paymentTemplate;
        this.objectMapper = objectMapper;
    }

    public PaymentSummaryResponse getPaymentSummary(final Instant from, final Instant to) {
        final var defaultPayments = paymentTemplate.opsForZSet().rangeByScore("payments_default:sorted", from.toEpochMilli(), to.toEpochMilli());
        final var fallbackPayments = paymentTemplate.opsForZSet().rangeByScore("payments_fallback:sorted", from.toEpochMilli(), to.toEpochMilli());

        logger.warning("Default payments: " + defaultPayments);
        logger.warning("Fallback payments: " + fallbackPayments);

        final var defaultSummary = defaultPayments.stream().map(this::fromJson).collect(Collectors.toSet());
        final var fallbackSummary = fallbackPayments.stream().map(this::fromJson).collect(Collectors.toSet());


        final var defaultStats = new PaymentSummaryResponse.IntegrationSummary(String.valueOf(defaultSummary.size()), BigDecimal.valueOf(sum(defaultSummary)));
        final var fallbackStats = new PaymentSummaryResponse.IntegrationSummary(String.valueOf(fallbackSummary.size()), BigDecimal.valueOf(sum(fallbackSummary)));

        return new PaymentSummaryResponse(defaultStats, fallbackStats);
    }


    private PaymentScore fromJson(final String json) {
        try {
            return objectMapper.readValue(json, PaymentScore.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private double sum(final Set<PaymentScore> payments) {
        return payments.stream()
        .mapToDouble(PaymentScore::amount)
        .sum();
    }
}
