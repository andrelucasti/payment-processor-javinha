package io.andrelucas.Rinha;


import java.time.Instant;

import org.springframework.stereotype.Service;

import io.andrelucas.Rinha.paymentsummary.PaymentIntegrationType;

@Service
public class PaymentClientImpl implements PaymentClient {

    public PaymentClient.PaymentResult processPayment(PaymentClient.PaymentRequest payment){
        return new PaymentClient.PaymentResult(PaymentIntegrationType.DEFAULT, Instant.now());
    }

}
