package facade;

import exception.ValidationException;
import factory.EDITransactionFactory;
import model.EDITransaction;
import model.TransactionStatus;
import strategy.InvoiceProcessingStrategy;
import strategy.OrderProcessingStrategy;
import strategy.ShipmentProcessingStrategy;
import strategy.TransactionProcessingStrategy;
import validation.AmountValidator;
import validation.SupplierValidator;
import validation.TransactionTypeValidator;
import validation.ValidationHandler;

/**
 * Facade Pattern — B2BGateway.
 *
 * Provides a single, unified entry point for processing business transactions.
 * Hides internal subsystems:
 *   1. EDITransactionFactory (Factory Pattern)
 *   2. ValidationHandler Chain (Chain of Responsibility)
 *   3. TransactionProcessingStrategy (Strategy Pattern)
 *
 * Interview Explanation:
 * "B2BGateway provides one simple entry point to the complete transaction-processing workflow,
 * decoupling the caller (Main) from internal factories, validators, and strategies."
 */
public class B2BGateway {

    private final EDITransactionFactory factory;
    private final ValidationHandler validationChain;

    public B2BGateway() {
        // Initialize Factory
        this.factory = new EDITransactionFactory();

        // Assemble Validation Chain: Type -> Supplier -> Amount
        TransactionTypeValidator typeValidator = new TransactionTypeValidator();
        SupplierValidator supplierValidator = new SupplierValidator();
        AmountValidator amountValidator = new AmountValidator();

        typeValidator.setNext(supplierValidator).setNext(amountValidator);
        this.validationChain = typeValidator;
    }

    /**
     * Processes one raw transaction line and returns an acknowledgement string.
     *
     * @param line Raw CSV transaction line
     * @return Formatted acknowledgement string: "ID,STATUS,REASON"
     */
    public String process(String line) {
        // ── 1. Create Transaction via Factory ────────────────────────────────
        EDITransaction transaction;
        try {
            transaction = factory.createTransaction(line);
        } catch (ValidationException e) {
            String id = extractId(line);
            System.out.println("  [REJECTED] " + id + ": " + e.getMessage());
            return id + "," + TransactionStatus.REJECTED + "," + e.getMessage();
        } catch (NumberFormatException e) {
            String id = extractId(line);
            System.out.println("  [REJECTED] " + id + ": Invalid amount");
            return id + "," + TransactionStatus.REJECTED + ",Invalid amount";
        }

        // ── 2. Validate Transaction via Chain of Responsibility ──────────────
        try {
            validationChain.handle(transaction);
            transaction.setStatus(TransactionStatus.VALIDATED);
        } catch (ValidationException e) {
            transaction.setStatus(TransactionStatus.REJECTED);
            transaction.setReason(e.getMessage());
            System.out.println("  [REJECTED] " + transaction.getId() + ": " + e.getMessage());
            return transaction.getId() + "," + transaction.getStatus() + "," + transaction.getReason();
        }

        // ── 3. Process Transaction via Strategy ──────────────────────────────
        TransactionProcessingStrategy strategy = selectStrategy(transaction.getType());
        strategy.process(transaction);

        // ── 4. Generate and return Acknowledgement ───────────────────────────
        return transaction.getId() + "," + transaction.getStatus() + "," + transaction.getReason();
    }

    /**
     * Strategy selector based on transaction type.
     */
    private TransactionProcessingStrategy selectStrategy(String type) {
        switch (type) {
            case "ORDER":
                return new OrderProcessingStrategy();
            case "INVOICE":
                return new InvoiceProcessingStrategy();
            case "SHIPMENT":
                return new ShipmentProcessingStrategy();
            default:
                return new OrderProcessingStrategy();
        }
    }

    /**
     * Safely extracts transaction ID from raw CSV line when factory fails.
     */
    private String extractId(String line) {
        String[] parts = line.split(",");
        if (parts.length >= 2) {
            return parts[1].trim();
        }
        return "UNKNOWN";
    }
}
