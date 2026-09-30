package model;

/**
 * Abstract base class for all EDI business transactions.
 *
 * Demonstrates:
 *   - Abstraction   : abstract class defining the common structure
 *   - Encapsulation : all fields are private; accessed via getters/setters
 *   - Inheritance   : OrderTransaction, InvoiceTransaction, ShipmentTransaction extend this
 */
public abstract class EDITransaction {

    // ── Private fields (Encapsulation) ──────────────────────────────────────
    private String id;
    private String type;
    private String supplier;
    private String customer;
    private double amount;
    private TransactionStatus status;
    private String reason; // Failure reason or confirmation message

    // ── Constructor ─────────────────────────────────────────────────────────
    public EDITransaction(String id, String type, String supplier, String customer, double amount) {
        this.id = id;
        this.type = type;
        this.supplier = supplier;
        this.customer = customer;
        this.amount = amount;
        this.status = TransactionStatus.RECEIVED;
        this.reason = "";
    }

    // ── Getters ─────────────────────────────────────────────────────────────
    public String getId() { return id; }
    public String getType() { return type; }
    public String getSupplier() { return supplier; }
    public String getCustomer() { return customer; }
    public double getAmount() { return amount; }
    public TransactionStatus getStatus() { return status; }
    public String getReason() { return reason; }

    // ── Setters ─────────────────────────────────────────────────────────────
    public void setStatus(TransactionStatus status) { this.status = status; }
    public void setReason(String reason) { this.reason = reason; }

    @Override
    public String toString() {
        return "EDITransaction{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", supplier='" + supplier + '\'' +
                ", customer='" + customer + '\'' +
                ", amount=" + amount +
                ", status=" + status +
                ", reason='" + reason + '\'' +
                '}';
    }
}
