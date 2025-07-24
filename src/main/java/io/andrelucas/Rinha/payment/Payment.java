package io.andrelucas.Rinha.payment;

import java.math.BigDecimal;
import java.util.UUID;

public record Payment(UUID correlationId, BigDecimal amount, PaymentStatus status) {

    public static Payment create(UUID correlationId, BigDecimal amount) {
        return new Payment(correlationId, amount, PaymentStatus.PENDING);
    }

}
