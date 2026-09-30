package cloud;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.nio.file.Paths;

/**
 * Minimal S3 file service helper.
 *
 * Demonstrates basic AWS SDK for Java v2 usage:
 * 1. S3Client         - Client interface for accessing Amazon S3
 * 2. PutObjectRequest - Request object to upload a file to S3
 * 3. GetObjectRequest - Request object to download a file from S3
 *
 * AWS Credentials are automatically retrieved by the AWS SDK from:
 * - Environment variables (AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY)
 * - AWS credentials file (~/.aws/credentials configured via `aws configure`)
 * No credentials are hard-coded in Java!
 */
public class S3FileService implements AutoCloseable {

    private final String bucketName;
    private final S3Client s3Client;

    /**
     * Initializes the S3 client with a specified bucket and region.
     * Default region is US_EAST_1 (or system default).
     */
    public S3FileService(String bucketName) {
        this.bucketName = bucketName;
        // Standard S3Client builder automatically resolves credentials from the environment
        this.s3Client = S3Client.builder()
                .region(Region.US_EAST_1)
                .build();
    }

    /**
     * Uploads a local file to the specified S3 key.
     *
     * @param localFilePath Path to the local file to upload
     * @param s3Key         Destination path in S3 (e.g. "incoming/transactions.txt")
     */
    public void uploadFile(String localFilePath, String s3Key) {
        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .build();

        s3Client.putObject(putRequest, Paths.get(localFilePath));
    }

    /**
     * Downloads an S3 object to a local destination file.
     *
     * @param s3Key           Path in S3 to download (e.g. "incoming/transactions.txt")
     * @param destinationPath Local path to save the downloaded file
     */
    public void downloadFile(String s3Key, String destinationPath) {
        GetObjectRequest getRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .build();

        s3Client.getObject(getRequest, Paths.get(destinationPath));
    }

    @Override
    public void close() {
        if (s3Client != null) {
            s3Client.close();
        }
    }
}
