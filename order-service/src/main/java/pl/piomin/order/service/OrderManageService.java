package pl.piomin.order.service;

import org.springframework.stereotype.Service;
import pl.piomin.base.domain.Order;

@Service
public class OrderManageService {

    public Order confirm(Order orderPayment, Order orderStock) {

        Order o = new Order(
                orderPayment.getId(),
                orderPayment.getCustomerId(),
                orderPayment.getProductId(),
                orderPayment.getProductCount(),
                orderPayment.getPrice()
        );

        String paymentStatus = safe(orderPayment.getStatus());
        String stockStatus = safe(orderStock.getStatus());

        if (Order.ACCEPT.equals(paymentStatus) &&
                Order.ACCEPT.equals(stockStatus)) {

            o.setStatus(Order.CONFIRMED);

        } else if (Order.REJECT.equals(paymentStatus) &&
                Order.REJECT.equals(stockStatus)) {

            o.setStatus("REJECTED"); // keeping original logic

        } else if (Order.REJECT.equals(paymentStatus) ||
                Order.REJECT.equals(stockStatus)) {

            String source = Order.REJECT.equals(paymentStatus)
                    ? "PAYMENT"
                    : "STOCK";

            o.setStatus(Order.ROLLBACK);
            o.setSource(source);

        } else {
            // ✅ fallback (important for debugging)
            o.setStatus("UNKNOWN");
        }

        return o;
    }

    // ✅ Prevents NullPointerException
    private String safe(String status) {
        return status == null ? "" : status.toUpperCase();
    }
}