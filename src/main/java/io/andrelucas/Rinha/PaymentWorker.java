package io.andrelucas.Rinha;

import java.time.Duration;
import java.util.logging.Logger;

import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.connection.stream.StreamReadOptions;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import io.andrelucas.Rinha.payment.Payment;
import io.andrelucas.Rinha.payment.PaymentProcessor;

@Service
public class PaymentWorker {
    private final Logger logger = Logger.getLogger(PaymentWorker.class.getName());
    private static final String STREAM = "payments_stream";
    private static final String GROUP = "payments_group";


    private final RedisTemplate<String, String> paymentTemplate;
    private final PaymentProcessor paymentProcessor;

    public PaymentWorker(final RedisTemplate<String, String> paymentTemplate, final PaymentProcessor paymentProcessor) {
        this.paymentTemplate = paymentTemplate;
        this.paymentProcessor = paymentProcessor;
    }

    @Scheduled(fixedDelay = 5000)
    public void run() {
        logger.info("Reading payments from stream");
       
        final var hostName = System.getenv("HOSTNAME") != null ? System.getenv("HOSTNAME") : "default";
        final var consumer = Consumer.from(GROUP, hostName);
        final var options = StreamReadOptions.empty().count(1).block(Duration.ofSeconds(1));
        final var offset = StreamOffset.create(STREAM, ReadOffset.lastConsumed());
        
        final var records = paymentTemplate.opsForStream().read(consumer, options, offset);

        if (records == null || records.isEmpty()) {
            logger.info("No payments to process");
            return;
        }
       
        records.forEach(r -> {
            logger.info("Processing payment: " + r.getValue());
            
            final var payment = Payment.from(r.getValue());
            paymentProcessor.processPayments(payment);
            paymentTemplate.opsForStream().acknowledge(STREAM, GROUP, r.getId());
            paymentTemplate.opsForStream().delete(STREAM, r.getId());

            logger.info("Payment processed: " + payment);
        });
    }
}
