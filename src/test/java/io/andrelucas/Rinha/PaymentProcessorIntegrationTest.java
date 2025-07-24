package io.andrelucas.Rinha;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import io.andrelucas.Rinha.payment.CreatePayment;
import io.andrelucas.Rinha.payment.MongoPaymentRepository;
import io.andrelucas.Rinha.payment.Payment;
import io.andrelucas.Rinha.payment.PaymentProcessor;
import io.andrelucas.Rinha.payment.PaymentStatus;
import io.andrelucas.Rinha.paymentsummary.MongoPaymentSummaryRepository;
import io.andrelucas.Rinha.paymentsummary.PaymentIntegrationType;
import reactor.core.publisher.Mono;

@SpringBootTest
public class PaymentProcessorIntegrationTest {

    @Autowired
    private PaymentProcessor paymentProcessor;

    @Autowired
    private CreatePayment createPayment;

    @Autowired
    private MongoPaymentRepository paymentRepository;

    @Autowired
    private MongoPaymentSummaryRepository paymentSummaryRepository;

    @MockitoBean(name = "paymentClient")
    private PaymentClient paymentClient;

    @Test
    void shouldProcessPayment() {
        when(paymentClient.processPayment(any(Payment.class)))
        .thenReturn(Mono.just(new PaymentClient.PaymentResult(PaymentIntegrationType.DEFAULT, Instant.now())));

        createPayment.execute(UUID.randomUUID(), BigDecimal.valueOf(100)).block();

        paymentProcessor.processPayments();

        final var payment = paymentRepository.findAll().blockFirst();

        Assertions.assertThat(payment).isNotNull();
        Assertions.assertThat(payment.status()).isEqualTo(PaymentStatus.APPROVED);
        Assertions.assertThat(payment.amount()).isEqualTo(BigDecimal.valueOf(100));

        final var paymentSummary = paymentSummaryRepository.findAll().blockFirst();
        Assertions.assertThat(paymentSummary).isNotNull();
        Assertions.assertThat(paymentSummary.correlationId()).isEqualTo(payment.correlationId());
        Assertions.assertThat(paymentSummary.amount()).isEqualTo(payment.amount());
        Assertions.assertThat(paymentSummary.integrationType()).isEqualTo(PaymentIntegrationType.DEFAULT);
        Assertions.assertThat(paymentSummary.requestedAt()).isBefore(Instant.now());
    }
    
}
