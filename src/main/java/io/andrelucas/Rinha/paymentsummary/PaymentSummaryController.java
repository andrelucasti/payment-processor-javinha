package io.andrelucas.Rinha.paymentsummary;

import java.time.Instant;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments-summary")
public class PaymentSummaryController {

    private final GetSummary getSummary;

    public PaymentSummaryController(final GetSummary getSummary) {
        this.getSummary = getSummary;
    }

    @GetMapping
    public ResponseEntity<PaymentSummaryResponse> getPaymentSummary(@RequestParam("from") Instant from, @RequestParam("to") Instant to) {
        final var summary = getSummary.getPaymentSummary(from, to);

        return ResponseEntity.ok(summary);
    }

}
