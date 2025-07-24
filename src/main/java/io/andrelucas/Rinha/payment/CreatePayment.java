package io.andrelucas.Rinha.payment;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

@Service
public class CreatePayment {

    private final MongoPaymentRepository repository;

    public CreatePayment(MongoPaymentRepository repository) {
        this.repository = repository;
    }

    public Mono<Payment> execute(UUID correlationId, BigDecimal amount) {
        final var payment = Payment.create(correlationId, amount);

        return repository.save(payment);
    }

}
