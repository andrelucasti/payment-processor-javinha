package io.andrelucas.Rinha;

import static org.mockito.ArgumentMatchers.any;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import io.andrelucas.Rinha.payment.CreatePayment;
import io.andrelucas.Rinha.payment.Payment;
import io.andrelucas.Rinha.payment.PaymentProcessor;
import io.andrelucas.Rinha.paymentsummary.PaymentIntegrationType;
import reactor.core.publisher.Mono;

@SpringBootTest
public class PaymentProcessorIntegrationTest {

    @Autowired
    private PaymentProcessor paymentProcessor;

    @Autowired
    private RedisTemplate<String, String> paymentTemplate;

    @Autowired
    private CreatePayment createPayment;

    @MockitoBean(name = "paymentClient")
    private PaymentClient paymentClient;

    @BeforeEach
    public void setUp() {
       paymentTemplate.delete("payments_default:count");
       paymentTemplate.delete("payments_default:total");
       paymentTemplate.delete("payments_fallback:count");
       paymentTemplate.delete("payments_fallback:total");
       paymentTemplate.delete("payments_stream");
       paymentTemplate.delete("payments_group");

       paymentTemplate.opsForStream().createGroup("payments_stream", "payments_group");
    }

    @Test
    void shouldProcessPayment() {
       
        Mockito.when(paymentClient.processPayment(any(PaymentClient.PaymentRequest.class))).thenReturn(Mono.just(new PaymentClient.PaymentResult(PaymentIntegrationType.DEFAULT, Instant.now())));
        createPayment.execute(UUID.randomUUID(), BigDecimal.valueOf(100));

        paymentProcessor.processPayments(Payment.create(UUID.randomUUID(), BigDecimal.valueOf(100)));

        final var paymentDefaultCount = paymentTemplate.opsForValue().get("payments_default:count");    
        final var paymentDefaultTotal = paymentTemplate.opsForValue().get("payments_default:total");
        final var paymentFallbackCount = paymentTemplate.opsForValue().get("payments_fallback:count");
        final var paymentFallbackTotal = paymentTemplate.opsForValue().get("payments_fallback:total");

        Assertions.assertThat(paymentDefaultCount).isNotNull();
        Assertions.assertThat(paymentDefaultTotal).isNotNull();
        Assertions.assertThat(paymentFallbackCount).isNull();
        Assertions.assertThat(paymentFallbackTotal).isNull();

        Assertions.assertThat(paymentDefaultCount).isEqualTo("1");
        Assertions.assertThat(paymentDefaultTotal).isEqualTo("100");

    }
    
}
