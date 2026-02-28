package es.urjc.daw04.service;

import org.springframework.stereotype.Service;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import es.urjc.daw04.model.Cart;
import es.urjc.daw04.model.CartItem;
import org.springframework.beans.factory.annotation.Value;
import java.util.ArrayList;
import java.util.List;

@Service
public class StripeService {

    @Value("${stripe.api.key}")
    private String secretKey;

    public Session createCheckoutSession(Cart cart, String baseUrl) throws StripeException {
        Stripe.apiKey = secretKey;

        List<SessionCreateParams.LineItem> lineItems = new ArrayList<>();

        // We convert each CartItem to a Stripe LineItem
        for (CartItem item : cart.getItems()) {
            lineItems.add(
                SessionCreateParams.LineItem.builder() //Builder pattern
                    .setQuantity((long) item.getQuantity())
                    .setPriceData(
                        SessionCreateParams.LineItem.PriceData.builder()
                            .setCurrency("eur")
                            .setUnitAmount((long) (item.getProduct().getPrice() * 100)) // Cents conversion (Stripe required)
                            .setProductData(
                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                    .setName(item.getProduct().getName())
                                    .build()
                            )
                            .build()
                    )
                    .build()
            );
        }

        SessionCreateParams params = SessionCreateParams.builder()
            .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
            .setMode(SessionCreateParams.Mode.PAYMENT)
            .setSuccessUrl(baseUrl + "/order/success?session_id={CHECKOUT_SESSION_ID}")
            .setCancelUrl(baseUrl + "/cart")
            .addAllLineItem(lineItems)
            .build();

        return Session.create(params);
    }
}
