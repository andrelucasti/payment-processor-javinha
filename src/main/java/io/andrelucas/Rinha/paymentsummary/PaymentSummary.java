package io.andrelucas.Rinha.paymentsummary;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentSummary(UUID correlationId, BigDecimal amount, PaymentIntegrationType integrationType, Instant requestedAt ) {

}
