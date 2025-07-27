package io.andrelucas.Rinha.payment;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public record Payment(UUID correlationId, BigDecimal amount, PaymentStatus status) {

    public static Payment create(UUID correlationId, BigDecimal amount) {
        return new Payment(correlationId, amount, PaymentStatus.PENDING);
    }


    public Map<String, String> toMap() {
        return Map.of(
            "correlationId", correlationId.toString(),
            "amount", amount.toString(),
            "status", status.name()
        );
    }

    public static Payment from(final Map<Object, Object> value) {
        return new Payment(UUID.fromString(value.get("correlationId").toString()), new BigDecimal(value.get("amount").toString()), PaymentStatus.valueOf(value.get("status").toString()));
    }

}
