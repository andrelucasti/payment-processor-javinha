package io.andrelucas.Rinha.payment;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
public class CreatePayment {

    private final RedisTemplate<String, String> paymentTemplate;


    public CreatePayment(final RedisTemplate<String, String> paymentTemplate) {
        this.paymentTemplate = paymentTemplate;
    }

    @PostConstruct
    public void init() {
       try {
        paymentTemplate.opsForStream().createGroup("payments_stream", "payments_group");
       } catch (Exception e) {
        // TODO: handle exception
       }
    }

    public void execute(UUID correlationId, BigDecimal amount) {
        final var payment = Payment.create(correlationId, amount);
        final var mapRecord = MapRecord.create("payments_stream", payment.toMap());
        
        paymentTemplate.opsForStream().add(mapRecord);
    }

}
