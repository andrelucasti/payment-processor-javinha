package io.andrelucas.Rinha.payment;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

record CreatePaymentRequest(UUID correlationId, BigDecimal amount) {
}

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final CreatePayment createPayment;

    public PaymentController(final CreatePayment createPayment) {
        this.createPayment = createPayment;
    }
    
    @PostMapping
    public Mono<ResponseEntity<Void>> createPayment(@RequestBody CreatePaymentRequest request) {
        return createPayment.execute(request.correlationId(), request.amount())
        .map(_ -> ResponseEntity.status(HttpStatus.CREATED).build());
    }

}
