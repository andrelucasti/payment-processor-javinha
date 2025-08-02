package io.andrelucas.Rinha.paymentsummary;

import java.time.Instant;
import java.util.logging.Logger;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments-summary")
public class PaymentSummaryController {

    private final Logger logger = Logger.getLogger(PaymentSummaryController.class.getName());
    private final RedisTemplate<String, String> paymentTemplate;

    public PaymentSummaryController(final RedisTemplate<String, String> paymentTemplate) {
        this.paymentTemplate = paymentTemplate;
    }

    @GetMapping
    public ResponseEntity<PaymentSummary> getPaymentSummary(@RequestParam("from") Instant from, @RequestParam("to") Instant to) {
        final var defaultPayments = paymentTemplate.opsForZSet().rangeByScore("payments_default:sorted", from.toEpochMilli(), to.toEpochMilli());
        final var fallbackPayments = paymentTemplate.opsForZSet().rangeByScore("payments_fallback:sorted", from.toEpochMilli(), to.toEpochMilli());

        logger.warning("Default payments: " + defaultPayments);
        logger.warning("Fallback payments: " + fallbackPayments);

        return ResponseEntity.ok().build();
    }

}
