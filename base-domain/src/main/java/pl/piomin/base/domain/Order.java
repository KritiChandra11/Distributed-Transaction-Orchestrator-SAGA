package pl.piomin.base.domain;

public class Order {

    private Long id;
    private Long customerId;
    private Long productId;
    private int productCount;
    private int price;
    private String status;
    private String source;

    // ✅ Constants to avoid magic strings
    public static final String NEW = "NEW";
    public static final String ACCEPT = "ACCEPT";
    public static final String REJECT = "REJECT";
    public static final String CONFIRMED = "CONFIRMED";
    public static final String ROLLBACK = "ROLLBACK";

    public Order() {
    }

    public Order(Long id, Long customerId, Long productId, String status) {
        this.id = id;
        this.customerId = customerId;
        this.productId = productId;
        this.status = normalize(status);
    }

    public Order(Long id, Long customerId, Long productId, int productCount, int price) {
        this.id = id;
        this.customerId = customerId;
        this.productId = productId;
        this.productCount = productCount;
        this.price = price;
        this.status = NEW; // ✅ fixed
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getStatus() {
        return status;
    }

    // ✅ Safe setter with validation
    public void setStatus(String status) {
        String normalized = normalize(status);
        if (!isValidStatus(normalized)) {
            throw new IllegalArgumentException("Invalid status: " + status);
        }
        this.status = normalized;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public int getProductCount() {
        return productCount;
    }

    public void setProductCount(int productCount) {
        this.productCount = productCount;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    // ✅ Normalize input (important for Kafka / APIs)
    private String normalize(String status) {
        return status == null ? null : status.toUpperCase();
    }

    // ✅ Validation logic
    private boolean isValidStatus(String status) {
        return NEW.equals(status) ||
               ACCEPT.equals(status) ||
               REJECT.equals(status) ||
               CONFIRMED.equals(status) ||
               ROLLBACK.equals(status);
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", customerId=" + customerId +
                ", productId=" + productId +
                ", productCount=" + productCount +
                ", price=" + price +
                ", status='" + status + '\'' +
                ", source='" + source + '\'' +
                '}';
    }
}