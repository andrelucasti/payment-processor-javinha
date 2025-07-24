package io.andrelucas.Rinha.payment;

import java.util.UUID;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface MongoPaymentRepository extends ReactiveMongoRepository<Payment, UUID> {

}
