package pl.piomin.payment.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.piomin.payment.domain.Customer;
import pl.piomin.payment.repository.CustomerRepository;

import java.util.List;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private static final Logger LOG = LoggerFactory.getLogger(PaymentController.class);

    @Autowired
    private CustomerRepository customerRepository;

    @PostMapping
    public Customer createPayment(@RequestBody Customer customer) {
        LOG.info("Creating payment for customer: {}", customer);
        return customerRepository.save(customer);
    }

    @GetMapping
    public List<Customer> getAllPayments() {
        LOG.info("Fetching all payments");
        return (List<Customer>) customerRepository.findAll();
    }

    @GetMapping("/{id}")
    public Customer getPaymentById(@PathVariable Long id) {
        LOG.info("Fetching payment with id: {}", id);
        return customerRepository.findById(id).orElse(null);
    }

    @PostMapping("/verify")
    public boolean verifyPayment(@RequestBody Customer customer) {
        LOG.info("Verifying payment for customer: {}", customer.getId());
        return true;
    }

}
