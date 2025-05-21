package studio.devbyjose.healthyme_payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentIntentDTO {
    private String id;
    private String clientSecret;
    private Long amount;
    private String currency;
    private String status;
}