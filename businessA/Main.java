import cloud.S3FileService;

import java.io.File;

/**
 * Business A Entry Point.
 *
 * Represents Company A (the supplier/sender).
 * Responsibilities:
 * 1. Locate local input/transactions.txt
 * 2. Create S3FileService
 * 3. Upload transactions.txt to AWS S3 bucket under "incoming/transactions.txt"
 * 4. Print confirmation and exit.
 *
 * Notice: Business A contains NO business logic, NO validation, and NO processing.
 * It strictly simulates an external trading partner sending business data.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("               Business A");
        System.out.println("========================================");

        // Path to local input file
        String localFilePath = "input/transactions.txt";
        File file = new File(localFilePath);

        // Fallback check if running from parent directory
        if (!file.exists()) {
            localFilePath = "businessA/input/transactions.txt";
            file = new File(localFilePath);
        }

        if (!file.exists()) {
            System.err.println("[ERROR] Local file not found: " + localFilePath);
            return;
        }

        // S3 bucket and key
        String bucketName = System.getenv("B2B_S3_BUCKET");
        if (bucketName == null || bucketName.trim().isEmpty()) {
            bucketName = "b2b-edi-bucket";
        }
        String s3Key = "incoming/transactions.txt";

        System.out.println("Uploading transaction file...");
        System.out.println("Target: s3://" + bucketName + "/" + s3Key);

        try (S3FileService s3Service = new S3FileService(bucketName)) {
            s3Service.uploadFile(file.getAbsolutePath(), s3Key);
            System.out.println("Upload successful.");
        } catch (Exception e) {
            System.err.println("\n[AWS S3 ERROR] Could not upload file to S3: " + e.getMessage());
            System.err.println("Tip: Ensure AWS credentials are configured via `aws configure` or environment variables,");
            System.err.println("and ensure S3 bucket '" + bucketName + "' exists.");
        }
    }
}
