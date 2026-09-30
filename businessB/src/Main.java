import cloud.S3FileService;
import facade.B2BGateway;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Business B Entry Point.
 *
 * Represents Company B (the customer/processor).
 *
 * Workflow:
 *   1. Download transactions.txt from S3 ("incoming/transactions.txt")
 *   2. Read downloaded file line by line
 *   3. Send each transaction to B2BGateway (Facade)
 *   4. Write acknowledgement results to output/acknowledgements.txt
 *   5. Upload acknowledgements.txt back to S3 ("outgoing/acknowledgements.txt")
 *   6. Print execution summary
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("               Business B");
        System.out.println("========================================");

        // Configure S3 bucket and keys
        String bucketName = System.getenv("B2B_S3_BUCKET");
        if (bucketName == null || bucketName.trim().isEmpty()) {
            bucketName = "b2b-edi-bucket";
        }
        String s3IncomingKey = "incoming/transactions.txt";
        String s3OutgoingKey = "outgoing/acknowledgements.txt";

        // Determine base directories (handles running from project root or businessB/)
        File inputDir = new File("input");
        File outputDir = new File("output");
        if (!inputDir.exists() && new File("businessB/input").exists()) {
            inputDir = new File("businessB/input");
            outputDir = new File("businessB/output");
        }
        if (!inputDir.exists()) {
            inputDir.mkdirs();
        }
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        File localInputFile = new File(inputDir, "transactions.txt");
        File localOutputFile = new File(outputDir, "acknowledgements.txt");

        // ── STEP 1: Download transactions.txt from S3 ─────────────────────────
        System.out.println("Downloading transaction file...");
        System.out.println("Source: s3://" + bucketName + "/" + s3IncomingKey);

        boolean s3Success = false;
        try (S3FileService s3Service = new S3FileService(bucketName)) {
            s3Service.downloadFile(s3IncomingKey, localInputFile.getAbsolutePath());
            System.out.println("Download successful.\n");
            s3Success = true;
        } catch (Exception e) {
            System.err.println("[AWS S3 NOTICE] Could not download from S3: " + e.getMessage());
            System.err.println("Checking for local fallback file at: " + localInputFile.getPath());
        }

        // Verify input file exists locally (either downloaded or pre-existing)
        if (!localInputFile.exists()) {
            // Check fallback from businessA input
            File fallback = new File("businessA/input/transactions.txt");
            if (fallback.exists()) {
                System.out.println("Using local businessA test data as fallback.");
                localInputFile = fallback;
            } else {
                System.err.println("[ERROR] No transactions file available to process.");
                return;
            }
        }

        // ── STEP 2: Process Transactions via B2BGateway (Facade) ──────────────
        System.out.println("Processing transactions...\n");

        B2BGateway gateway = new B2BGateway();
        List<String> acknowledgements = new ArrayList<>();

        int total = 0;
        int processed = 0;
        int rejected = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(localInputFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }

                String ack = gateway.process(line);
                acknowledgements.add(ack);

                total++;
                if (ack.contains("PROCESSED")) {
                    processed++;
                } else {
                    rejected++;
                }
            }
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to read transactions: " + e.getMessage());
            return;
        }

        // ── STEP 3: Write Acknowledgements File ───────────────────────────────
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(localOutputFile))) {
            for (String ack : acknowledgements) {
                writer.write(ack);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to write acknowledgements: " + e.getMessage());
            return;
        }

        System.out.println("\nTotal Transactions: " + total);
        System.out.println("Processed: " + processed);
        System.out.println("Rejected: " + rejected);
        System.out.println("\nAcknowledgement created at: " + localOutputFile.getPath());

        // ── STEP 4: Upload Acknowledgements to S3 ─────────────────────────────
        System.out.println("Uploading acknowledgement...");
        System.out.println("Target: s3://" + bucketName + "/" + s3OutgoingKey);

        try (S3FileService s3Service = new S3FileService(bucketName)) {
            s3Service.uploadFile(localOutputFile.getAbsolutePath(), s3OutgoingKey);
            System.out.println("Upload successful.");
        } catch (Exception e) {
            System.err.println("[AWS S3 NOTICE] S3 upload skipped or failed: " + e.getMessage());
            System.err.println("Local output is preserved at: " + localOutputFile.getAbsolutePath());
        }

        System.out.println("========================================");
    }
}
