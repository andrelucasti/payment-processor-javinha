package io.andrelucas.Rinha;

import java.math.BigDecimal;
import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import io.andrelucas.Rinha.payment.Payment;
import io.andrelucas.Rinha.payment.PaymentStatus;

public class PaymentTest {

    @Test
    void shouldReturnPendingPaymentWhenCreated() {
        final var payment = Payment.create(UUID.randomUUID(), BigDecimal.valueOf(1000));
        
        Assertions.assertThat(payment.status()).isEqualTo(PaymentStatus.PENDING);
    }
}
    