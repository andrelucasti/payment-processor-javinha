package io.andrelucas.Rinha;

import java.time.Instant;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.andrelucas.Rinha.paymentsummary.PaymentIntegrationType;
import reactor.core.publisher.Mono;

@Service
public class PaymentClientImpl implements PaymentClient {

    private final Logger logger = Logger.getLogger(PaymentClientImpl.class.getName());

    private final WebClient paymentProcessorDefault;
    private final WebClient paymentProcessorFallback;
    private final ObjectMapper objectMapper;

    public PaymentClientImpl(final WebClient paymentProcessorDefault, final WebClient paymentProcessorFallback, final ObjectMapper objectMapper) {
        this.paymentProcessorDefault = paymentProcessorDefault;
        this.paymentProcessorFallback = paymentProcessorFallback;
        this.objectMapper = objectMapper;
    }

    public Mono<PaymentClient.PaymentResult> processPayment(PaymentClient.PaymentRequest payment){
        return processPaymentDefault(payment);
    }

    private Mono<PaymentClient.PaymentResult> processPaymentDefault(PaymentClient.PaymentRequest payment){
        logger.info("Processing payment with default processor: " + toJson(payment));
        return paymentProcessorDefault.post()
        .uri("/payments")
        .bodyValue(toJson(payment))
        .retrieve()
        .onStatus(status -> status.is4xxClientError(), _ -> 
            Mono.error(new RuntimeException("4xx error - trying fallback")))
        .bodyToMono(PaymentClient.PaymentResult.class)
        .map(_ -> new PaymentClient.PaymentResult(PaymentIntegrationType.DEFAULT, Instant.now()))
        .doOnSuccess(result -> logger.info("Default processor success: " + toJson(result)))
        .onErrorResume(_ -> processPaymentFallback(payment));
    }


    private Mono<PaymentClient.PaymentResult> processPaymentFallback(PaymentClient.PaymentRequest payment){
        logger.info("Processing payment with fallback processor: " + toJson(payment));
        return paymentProcessorFallback.post()
        .uri("/payments")
        .bodyValue(toJson(payment))
        .retrieve()
        .onStatus(status -> !status.is2xxSuccessful(), _ -> 
            Mono.error(new RuntimeException("Payment processor fallback failed")))
        .bodyToMono(PaymentClient.PaymentResult.class)
        .map(_ -> new PaymentClient.PaymentResult(PaymentIntegrationType.FALLBACK, Instant.now()))
        .doOnSuccess(result -> logger.info("Fallback processor success: " + toJson(result)));
    }

    // Método utilitário para converter qualquer objeto para JSON
    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            logger.warning("Error converting to JSON: " + e.getMessage());
            return obj.toString(); // fallback para toString()
        }
    }

    // Método utilitário para converter JSON string para objeto
    public <T> T fromJson(String json, Class<T> valueType) {
        try {
            return objectMapper.readValue(json, valueType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error parsing JSON: " + e.getMessage(), e);
        }
    }

}
