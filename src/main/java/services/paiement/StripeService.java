package services.paiement;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

public class StripeService {
        private static final String STRIPE_SECRET_KEY = "sk_test_51QxRZdE2wQ15wowz6UkH3rzyxZV5mGTKQieic68ctjS917lx8hmZlg9aYdusT6ZFvTay0WQKlLhPHkAYNNqAODFl008tOCoXrS"; // Remplacez par votre clé secrète


    public StripeService() {
        Stripe.apiKey = STRIPE_SECRET_KEY;
    }

    public String createPaymentIntent(double amount, String currency, String description) throws StripeException {
        // Convertir le montant en centimes (Stripe travaille en sous-unités)
        long amountInCents = (long) (amount * 100);

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amountInCents)
                .setCurrency(currency.toLowerCase())
                .setDescription(description)
                .build();

        PaymentIntent paymentIntent = PaymentIntent.create(params);
        return paymentIntent.getClientSecret();
    }
}