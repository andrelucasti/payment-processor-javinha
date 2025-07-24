package io.andrelucas.Rinha.paymentsummary;

import java.util.UUID;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface MongoPaymentSummaryRepository extends ReactiveMongoRepository<PaymentSummary, UUID> {

}
