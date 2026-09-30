# B2B/EDI Transaction Processing Simulator with AWS S3

A beginner-friendly, clean B2B/EDI Transaction Simulator built with **Java 17, Core Java, OOP, 4 classic Design Patterns, and minimal AWS S3 integration** designed for college placement interviews (e.g. Infosys SP3).

---

## 1. Project Overview

This project simulates automated transaction exchange between two independent companies:
* **Business A (Supplier):** Takes local transactions and uploads them to a shared AWS S3 bucket.
* **Business B (Customer/Retailer):** Downloads transactions from S3, parses them using a **Factory**, validates them using a **Chain of Responsibility**, processes them using the **Strategy Pattern** behind a **Facade Gateway**, writes acknowledgements, and uploads the results back to S3.

AWS S3 acts simply as the shared cloud file storage between the two companies.

---

## 2. Architecture & Complete Flow

```text
Business A
    |
    | AWS SDK upload (PutObjectRequest)
    ↓
AWS S3 (b2b-edi-bucket/incoming/transactions.txt)
    |
    | AWS SDK download (GetObjectRequest)
    ↓
Business B
    |
    ↓
B2BGateway (Facade)
    ├── EDITransactionFactory (Factory Pattern)
    │       ↓
    │   EDITransaction [Order / Invoice / Shipment]
    │
    ├── Validation Chain (Chain of Responsibility)
    │   TypeValidator → SupplierValidator → AmountValidator
    │
    └── Strategy (Strategy Pattern)
        OrderStrategy / InvoiceStrategy / ShipmentStrategy
    |
    ↓
acknowledgements.txt
    |
    | AWS SDK upload (PutObjectRequest)
    ↓
AWS S3 (b2b-edi-bucket/outgoing/acknowledgements.txt)
```

---

## 3. S3 Bucket Structure

```text
b2b-edi-bucket/
│
├── incoming/
│   └── transactions.txt          (Uploaded by Business A, downloaded by Business B)
│
└── outgoing/
    └── acknowledgements.txt      (Uploaded by Business B)
```

---

## 4. Project Structure

```text
B2B-EDI-S3-Simulator/
│
├── pom.xml                                ← Single dependency: AWS SDK for S3
│
├── businessA/
│   ├── input/
│   │   └── transactions.txt               ← Source transactions
│   ├── cloud/
│   │   └── S3FileService.java             ← S3 upload helper
│   └── Main.java                          ← Business A entry point
│
├── businessB/
│   ├── src/
│   │   ├── Main.java                      ← Business B entry point
│   │   │
│   │   ├── model/
│   │   │   ├── EDITransaction.java        ← Abstract parent
│   │   │   ├── OrderTransaction.java      ← Concrete subclass
│   │   │   ├── InvoiceTransaction.java    ← Concrete subclass
│   │   │   ├── ShipmentTransaction.java   ← Concrete subclass
│   │   │   └── TransactionStatus.java     ← Enum (RECEIVED, VALIDATED, PROCESSED, REJECTED)
│   │   │
│   │   ├── factory/
│   │   │   └── EDITransactionFactory.java ← Factory Pattern
│   │   │
│   │   ├── validation/
│   │   │   ├── ValidationHandler.java     ← Abstract chain link
│   │   │   ├── TransactionTypeValidator.java
│   │   │   ├── SupplierValidator.java
│   │   │   └── AmountValidator.java
│   │   │
│   │   ├── strategy/
│   │   │   ├── TransactionProcessingStrategy.java
│   │   │   ├── OrderProcessingStrategy.java
│   │   │   ├── InvoiceProcessingStrategy.java
│   │   │   └── ShipmentProcessingStrategy.java
│   │   │
│   │   ├── facade/
│   │   │   └── B2BGateway.java            ← Facade Pattern
│   │   │
│   │   ├── cloud/
│   │   │   └── S3FileService.java         ← Minimal S3 helper
│   │   │
│   │   └── exception/
│   │       └── ValidationException.java   ← Custom exception
│   │
│   ├── input/                             ← Downloaded from S3
│   └── output/                            ← Generated acknowledgements
│
├── AWS_SIMPLE_NOTES.md                    ← 1-page guide on S3Client, Put, Get
├── INTERVIEW_NOTES.md                     ← 25+ short Q&A for interview prep
└── README.md
```

---

## 5. AWS Local Credential Setup

Credentials are **never hardcoded in Java code**.

Configure credentials once on your machine using standard AWS CLI:
```bash
aws configure
```
Enter:
* AWS Access Key ID
* AWS Secret Access Key
* Default region name (e.g. `us-east-1`)

The AWS SDK automatically reads credentials from `~/.aws/credentials` or environment variables (`AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY`).

*(Optional)* Set custom bucket name:
```powershell
$env:B2B_S3_BUCKET = "your-bucket-name"
```

---

## 6. How to Run

### Option 1: Using Maven

**Terminal 1 — Run Business A:**
```bash
mvn compile exec:java -Dexec.mainClass="Main" -Dexec.sourcepath="businessA"
```

**Terminal 2 — Run Business B:**
```bash
mvn compile exec:java -Dexec.mainClass="Main" -Dexec.sourcepath="businessB/src"
```

### Option 2: In any IDE (IntelliJ / Eclipse / VS Code)
1. Open `B2B-EDI-S3-Simulator` as a Maven project (dependencies download automatically).
2. Right click `businessA/Main.java` → **Run**.
3. Right click `businessB/src/Main.java` → **Run**.

---

## 7. Sample Input & Output

### Input (`businessA/input/transactions.txt`)
```text
ORDER,TXN1001,ABC Supplies,XYZ Retail,5000
ORDER,TXN1002,,XYZ Retail,3000
ORDER,TXN1003,ABC Supplies,XYZ Retail,-100
SHIPMENT,TXN1004,ABC Supplies,XYZ Retail,1200
INVOICE,TXN1005,ABC Supplies,XYZ Retail,7500
REFUND,TXN1006,ABC Supplies,XYZ Retail,900
ORDER,TXN1007,ABC Supplies,XYZ Retail,abc
```

### Output (`businessB/output/acknowledgements.txt`)
```text
TXN1001,PROCESSED,Accepted
TXN1002,REJECTED,Supplier cannot be empty
TXN1003,REJECTED,Amount must be greater than 0
TXN1004,PROCESSED,Accepted
TXN1005,PROCESSED,Accepted
TXN1006,REJECTED,Unsupported transaction type
TXN1007,REJECTED,Invalid amount
```

---

## 8. Four Design Patterns

| Pattern | Class | Purpose |
|---|---|---|
| **Factory** | `EDITransactionFactory` | Creates concrete `Order`, `Invoice`, or `Shipment` object based on type string. |
| **Chain of Responsibility** | `ValidationHandler` + 3 validators | Validates sequential business rules (Type → Supplier → Amount). |
| **Strategy** | `TransactionProcessingStrategy` + 3 strategies | Separates processing logic per transaction type without huge `if-else` blocks. |
| **Facade** | `B2BGateway` | Exposes a single `process(line)` method hiding Factory, Chain, and Strategy. |

---

## 9. OOP Concepts Demonstrated

* **Abstraction:** `abstract class EDITransaction` defines the template without being instantiable.
* **Encapsulation:** Private fields in `EDITransaction` with public getters/setters.
* **Inheritance:** `OrderTransaction`, `InvoiceTransaction`, `ShipmentTransaction` extend `EDITransaction`.
* **Polymorphism:** `EDITransaction transaction` reference holding concrete subclasses; `TransactionProcessingStrategy strategy` executing runtime implementations.
* **Interfaces:** `TransactionProcessingStrategy` enforces contract for processors.

---

## 10. Honest Limitations

1. **Simplified CSV:** Real B2B uses ANSI X12 or UN/EDIFACT formats.
2. **S3 as File Exchange:** S3 is used strictly for file transport, not as a distributed transactional database.
3. **Minimal AWS SDK:** Uses only standard synchronous S3Client upload/download.
4. **No Database:** Transactions are stored in flat files.
5. **No Polling Daemon:** Execution is trigger-based per run.

---

## 11. Future Improvements (Interview Discussion Points)

* Add AWS S3 Event Notifications + SQS to trigger Business B automatically when a file arrives.
* Integrate AWS Lambda or Spring Boot REST endpoints.
* Parse standard ANSI X12 EDI (850 Purchase Order, 810 Invoice, 856 ASN).
* Add a relational database (PostgreSQL/MySQL) for transaction history.
* Add unit testing using JUnit 5 and Mockito.
