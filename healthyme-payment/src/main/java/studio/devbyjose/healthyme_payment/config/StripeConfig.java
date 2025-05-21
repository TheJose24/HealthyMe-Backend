package studio.devbyjose.healthyme_payment.config;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class StripeConfig {

    @Value("${stripe.key.secret}")
    private String stripeApiKey;

    @PostConstruct
    public void init() {
        log.info("Inicializando configuración de Stripe");
        Stripe.apiKey = stripeApiKey;
    }
}