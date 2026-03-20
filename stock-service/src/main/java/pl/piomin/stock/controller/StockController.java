package pl.piomin.stock.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.piomin.stock.domain.Product;
import pl.piomin.stock.repository.ProductRepository;

import java.util.List;

@RestController
@RequestMapping("/stock")
public class StockController {

    private static final Logger LOG = LoggerFactory.getLogger(StockController.class);

    @Autowired
    private ProductRepository productRepository;

    @PostMapping
    public Product addStock(@RequestBody Product product) {
        LOG.info("Adding stock for product: {}", product);
        return productRepository.save(product);
    }

    @GetMapping
    public List<Product> getAllStock() {
        LOG.info("Fetching all stock");
        return (List<Product>) productRepository.findAll();
    }

    @GetMapping("/{id}")
    public Product getStockById(@PathVariable Long id) {
        LOG.info("Fetching stock with id: {}", id);
        return productRepository.findById(id).orElse(null);
    }

    @PostMapping("/reserve")
    public boolean reserveStock(@RequestBody Product product) {
        LOG.info("Reserving stock for product: {}", product.getId());
        return true;
    }

}
