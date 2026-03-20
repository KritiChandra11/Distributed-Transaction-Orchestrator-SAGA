package pl.piomin.order.controller;

import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import pl.piomin.base.domain.Order;
import pl.piomin.order.service.OrderGeneratorService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private static final Logger LOG = LoggerFactory.getLogger(OrderController.class);

    private final AtomicLong id = new AtomicLong();
    private final KafkaTemplate<Long, Order> template;
    private final StreamsBuilderFactoryBean kafkaStreamsFactory;
    private final OrderGeneratorService orderGeneratorService;

    public OrderController(KafkaTemplate<Long, Order> template,
                           StreamsBuilderFactoryBean kafkaStreamsFactory,
                           OrderGeneratorService orderGeneratorService) {
        this.template = template;
        this.kafkaStreamsFactory = kafkaStreamsFactory;
        this.orderGeneratorService = orderGeneratorService;
    }

    @PostMapping
    public Order create(@RequestBody Order order) {
        order.setId(id.incrementAndGet());
        order.setStatus("NEW");
        template.send("orders", order.getId(), order);
        LOG.info("Sent: {}", order);
        return order;
    }

    @PostMapping("/generate")
    public boolean generate() {
        orderGeneratorService.generate();
        return true;
    }

    @GetMapping
    public List<Order> all() {
        List<Order> orders = new ArrayList<>();

        try {
            if (kafkaStreamsFactory.getKafkaStreams() == null) {
                LOG.warn("Kafka Streams not ready yet");
                return orders;
            }

            ReadOnlyKeyValueStore<Long, Order> store =
                    kafkaStreamsFactory.getKafkaStreams()
                            .store(StoreQueryParameters.fromNameAndType(
                                    "orders",
                                    QueryableStoreTypes.keyValueStore()));

            try (KeyValueIterator<Long, Order> it = store.all()) {
                it.forEachRemaining(kv -> {
                    LOG.info("Found in state store: key={}, value={}", kv.key, kv.value);
                    orders.add(kv.value);
                });
            }
            
            LOG.info("Total orders in state store: {}", orders.size());

        } catch (Exception e) {
            LOG.error("Error fetching orders", e);
        }

        return orders;
    }

    @GetMapping("/debug")
    public String debug() {
        StringBuilder sb = new StringBuilder();
        sb.append("Kafka Streams Status: ");
        
        try {
            if (kafkaStreamsFactory.getKafkaStreams() == null) {
                sb.append("NOT INITIALIZED\n");
            } else {
                sb.append(kafkaStreamsFactory.getKafkaStreams().state()).append("\n");
            }
        } catch (Exception e) {
            sb.append("ERROR: ").append(e.getMessage()).append("\n");
        }
        
        return sb.toString();
    }
}