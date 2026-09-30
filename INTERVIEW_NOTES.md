# INTERVIEW NOTES — 25+ Questions & Answers (Infosys SP3)

> Each answer is kept to **2–4 lines**, clear and easy to memorize for your placement interview.

---

## Part 1: B2B & EDI

### 1. What is B2B?
**Business-to-Business (B2B)** refers to commercial transactions conducted between two separate corporate entities, such as a manufacturer and a retailer, rather than between a company and an individual consumer.

### 2. What is EDI?
**Electronic Data Interchange (EDI)** is a standardized electronic format that replaces paper-based business communication (like postal mail, paper invoices, or faxes), allowing companies to exchange structured business documents automatically.

### 3. What does this project do?
It simulates an end-to-end B2B transaction pipeline where Business A uploads EDI transactions to an AWS S3 bucket, and Business B downloads, validates, processes them, and uploads back an acknowledgement file.

### 4. Explain the complete flow.
Business A uploads `transactions.txt` to S3 bucket `incoming/`. Business B downloads the file, passes each line through a Facade (`B2BGateway`) which coordinates Factory, Validation Chain, and Strategy, generates `acknowledgements.txt`, and uploads it to S3 `outgoing/`.

### 5. What is Business A?
Business A represents the supplier or sender company. It acts as an external partner whose only role is to send raw business transactions to the shared cloud storage.

### 6. What is Business B?
Business B represents the receiving customer/retailer. It contains the core system logic: downloading, object creation, sequential validation, business processing, and acknowledgement generation.

---

## Part 2: Object-Oriented Programming (OOP)

### 7. What is abstraction in your project?
`EDITransaction` is an `abstract class` representing the common template of a transaction without allowing direct instantiation. Concrete subclasses (`OrderTransaction`, etc.) provide concrete definitions.

### 8. What is encapsulation in your project?
All fields in `EDITransaction` (`id`, `amount`, `status`, etc.) are declared `private` and accessed strictly through public getters and setters, protecting internal state from direct external tampering.

### 9. Where is inheritance used?
`OrderTransaction`, `InvoiceTransaction`, and `ShipmentTransaction` all `extend EDITransaction`, inheriting all 7 state fields and constructor behavior via `super(...)` without duplicating code.

### 10. Where is polymorphism used?
Polymorphism is demonstrated when `B2BGateway` holds an `EDITransaction` parent reference pointing to any subclass instance, and executes `TransactionProcessingStrategy.process()` where the JVM dynamically dispatches to the correct implementation.

### 11. Why is EDITransaction an abstract class instead of an interface?
Because all transaction types share identical state (id, supplier, customer, amount, status) and accessor methods. An abstract class allows sharing common fields and constructor logic, whereas pre-Java interfaces do not hold instance state.

### 12. What is the difference between an abstract class and an interface?
An abstract class is used when classes share identity and state ("is-a" relationship with common fields), while an interface defines a contract of capabilities ("can-do" behavior) without storing instance fields.

---

## Part 3: Design Patterns

### 13. Why did you use the Factory Pattern?
`EDITransactionFactory` centralizes the instantiation logic based on the input type string. It decouples the caller (`B2BGateway`) from knowing which concrete subclass constructor to invoke.

### 14. Why did you use the Chain of Responsibility Pattern?
It decouples validation rules into distinct classes (`TypeValidator` → `SupplierValidator` → `AmountValidator`). Each class handles a single responsibility, and adding new rules requires zero changes to existing validators.

### 15. Why did you use the Strategy Pattern?
`TransactionProcessingStrategy` defines separate processing algorithms (`OrderProcessingStrategy`, etc.) for each transaction type. It eliminates massive switch/if-else blocks and adheres to the Open-Closed Principle.

### 16. Why did you use the Facade Pattern?
`B2BGateway` provides a single, simple method `process(line)` that hides the combined complexity of Factory, Validation Chain, and Strategy execution, keeping `Main` completely clean.

### 17. Why not just use one large if-else block?
A single if-else block violates the Single Responsibility and Open-Closed principles. It becomes difficult to maintain, test, and debug as new transaction types or validation rules are added.

---

## Part 4: AWS & Cloud Integration

### 18. Why did you use AWS S3?
Amazon S3 provides durable, globally accessible object storage that acts as a vendor-neutral cloud mailbox between Business A and Business B without requiring a direct network connection.

### 19. What is S3 and what is an S3 bucket?
Amazon Simple Storage Service (S3) is scalable cloud object storage. A **bucket** is a top-level cloud container (similar to a root folder) where files and data objects are stored under unique keys.

### 20. How does Business A upload files to S3?
Business A creates an `S3Client`, builds a `PutObjectRequest` specifying the bucket and S3 key, and calls `s3Client.putObject(request, Paths.get(localPath))`.

### 21. How does Business B download files from S3?
Business B builds a `GetObjectRequest` with the bucket name and incoming key, then calls `s3Client.getObject(request, Paths.get(destinationPath))`.

### 22. Where are AWS credentials configured?
Credentials are configured outside Java source code in `~/.aws/credentials` using the `aws configure` CLI command or standard environment variables (`AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY`). The SDK picks them up automatically.

### 23. What are S3Client, PutObjectRequest, and GetObjectRequest?
`S3Client` is the service client interface. `PutObjectRequest` encapsulates upload parameters (bucket, key). `GetObjectRequest` encapsulates download parameters.

### 24. Why didn't you use AWS Lambda, SQS, or EventBridge?
To keep the application focused on Core Java, OOP, and Design Patterns for placement interviews, avoiding unnecessary cloud complexity, cloud costs, and external infrastructure setup.

---

## Part 5: Edge Cases & System Evolution

### 25. What happens if S3 download fails?
The application catches the exception in `Main`, logs a clean warning message, checks for a local file fallback, and avoids crashing with an unhandled stack trace.

### 26. What happens if an amount field contains invalid characters (e.g., 'abc')?
`Double.parseDouble()` throws a `NumberFormatException`. `B2BGateway` catches it, sets status to `REJECTED`, assigns reason "Invalid amount", and continues processing subsequent transactions.

### 27. How would you add a new transaction type (e.g. PAYMENT)?
1. Create `PaymentTransaction extends EDITransaction`.
2. Add a `case "PAYMENT"` in `EDITransactionFactory`.
3. Create `PaymentProcessingStrategy implements TransactionProcessingStrategy`.
4. Add a `case "PAYMENT"` in `B2BGateway.selectStrategy()`.

### 28. What are the key limitations of this simulator?
It uses simplified CSV instead of real ANSI X12/EDIFACT standards, runs synchronously on local files rather than continuous real-time streaming, and lacks database persistence.

### 29. How would you improve this system for enterprise production?
Add S3 Event Notifications to automatically trigger an AWS SQS queue or Lambda function upon file upload, parse standard ANSI X12 850/810 documents, and persist transactions into an enterprise SQL database.
