package cloud;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.nio.file.Paths;

/**
 * Minimal S3 file service helper for Business B.
 *
 * Uses only 3 fundamental AWS SDK concepts:
 * 1. S3Client         - Client for S3 operations
 * 2. GetObjectRequest - Request to download an object from S3
 * 3. PutObjectRequest - Request to upload an object to S3
 *
 * AWS Credentials are automatically retrieved from standard local AWS configuration:
 *   - ~/.aws/credentials (configured using `aws configure`)
 *   - Environment variables (AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY)
 * Zero credentials in source code.
 */
public class S3FileService implements AutoCloseable {

    private final String bucketName;
    private final S3Client s3Client;

    public S3FileService(String bucketName) {
        this.bucketName = bucketName;
        // Default builder automatically resolves credentials and region from the local environment
        this.s3Client = S3Client.builder()
                .region(Region.US_EAST_1)
                .build();
    }

    /**
     * Downloads a file from S3 to a local destination path.
     *
     * @param s3Key           Path in S3 (e.g. "incoming/transactions.txt")
     * @param destinationPath Local destination path
     */
    public void downloadFile(String s3Key, String destinationPath) {
        GetObjectRequest getRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .build();

        s3Client.getObject(getRequest, Paths.get(destinationPath));
    }

    /**
     * Uploads a local file to S3 at the specified key.
     *
     * @param localFilePath Path to local file
     * @param s3Key         Destination path in S3 (e.g. "outgoing/acknowledgements.txt")
     */
    public void uploadFile(String localFilePath, String s3Key) {
        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .build();

        s3Client.putObject(putRequest, Paths.get(localFilePath));
    }

    @Override
    public void close() {
        if (s3Client != null) {
            s3Client.close();
        }
    }
}
