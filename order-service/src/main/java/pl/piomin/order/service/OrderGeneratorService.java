package pl.piomin.order.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pl.piomin.base.domain.Order;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class OrderGeneratorService {

    private static final Logger LOG = LoggerFactory.getLogger(OrderGeneratorService.class);

    private static final Random RAND = new Random();
    private final AtomicLong id = new AtomicLong();

    private final KafkaTemplate<Long, Order> template;

    public OrderGeneratorService(KafkaTemplate<Long, Order> template) {
        this.template = template;
    }

    @Async
    public void generate() {
        for (int i = 0; i < 10000; i++) {

            int x = RAND.nextInt(5) + 1;

            Order o = new Order(
                    id.incrementAndGet(),
                    (long) (RAND.nextInt(100) + 1),   // ✅ safer
                    (long) (RAND.nextInt(100) + 1),   // ✅ safer
                    Order.NEW                         // ✅ fixed
            );

            o.setPrice(100 * x);
            o.setProductCount(x);

            template.send("orders", o.getId(), o);

            LOG.info("Generated order: {}", o); // ✅ useful
        }
    }
}