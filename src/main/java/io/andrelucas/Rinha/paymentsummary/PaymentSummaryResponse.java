package io.andrelucas.Rinha.paymentsummary;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PaymentSummaryResponse(
    @JsonProperty("default") IntegrationSummary defaultStatus, 
    @JsonProperty("fallback") IntegrationSummary fallbackStatus) {
    public record IntegrationSummary(
        String totalRequests, 
        BigDecimal totalAmount) {}
}
