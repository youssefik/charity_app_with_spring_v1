package com.example.charity_app_v1.service;

import com.paypal.api.payments.RedirectUrls;

import com.paypal.api.payments.Payment;
import com.paypal.api.payments.PaymentExecution;
import com.paypal.api.payments.Payer;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PayPalService {

    @Value("${paypal.client.id}")
    private String clientId;

    @Value("${paypal.client.secret}")
    private String clientSecret;

    @Value("${paypal.mode}")
    private String mode;

    public Payment createPayment(
            Double amount,
            String currency,
            String method,
            String intent,
            String description,
            String cancelUrl,
            String successUrl) throws PayPalRESTException {

        com.paypal.api.payments.Amount total = new com.paypal.api.payments.Amount();
        total.setCurrency(currency);
        total.setTotal(amount.toString());

        com.paypal.api.payments.Transaction transaction = new com.paypal.api.payments.Transaction();
        transaction.setDescription(description);
        transaction.setAmount(total);

        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl(cancelUrl);
        redirectUrls.setReturnUrl(successUrl);

        Payment payment = new Payment();
        payment.setIntent(intent);
        payment.setPayer(new Payer());
        payment.getPayer().setPaymentMethod(method);
        payment.setTransactions(List.of(transaction));
        payment.setRedirectUrls(redirectUrls);

        return payment.create(getAPIContext());
    }

    public Payment executePayment(String paymentId, String payerId) throws PayPalRESTException {
        Payment payment = Payment.get(getAPIContext(), paymentId);
        PaymentExecution paymentExecution = new PaymentExecution();
        paymentExecution.setPayerId(payerId);
        return payment.execute(getAPIContext(), paymentExecution);
    }

    private APIContext getAPIContext() {
        return new APIContext(clientId, clientSecret, mode);
    }
}
