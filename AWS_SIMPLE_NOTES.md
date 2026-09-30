# AWS SDK Simple Notes (Learn in 30 Minutes)

> **Core Philosophy:** In this project, AWS S3 is used strictly as a shared cloud mailbox between two companies. We use only **3 basic AWS SDK classes** and zero advanced cloud services.

---

## 1. The 3 AWS SDK Classes Used

### 1. `S3Client`
* **What it is:** The main Java object used to communicate with Amazon S3.
* **Why we need it:** It acts like a remote controller or network connection to S3. All S3 actions (upload, download) are called through this client.
* **How it's created:**
  ```java
  S3Client s3Client = S3Client.builder()
          .region(Region.US_EAST_1)
          .build();
  ```
  The builder automatically finds your credentials and connects to S3.

---

### 2. `PutObjectRequest`
* **What it is:** The request object containing instructions for **uploading** a file into S3.
* **What it contains:** The target **bucket name** and the **S3 key** (file path in S3).
* **How it's created:**
  ```java
  PutObjectRequest request = PutObjectRequest.builder()
          .bucket("b2b-edi-bucket")
          .key("incoming/transactions.txt")
          .build();
  ```

---

### 3. `GetObjectRequest`
* **What it is:** The request object containing instructions for **downloading** a file from S3.
* **What it contains:** The source **bucket name** and the **S3 key** to retrieve.
* **How it's created:**
  ```java
  GetObjectRequest request = GetObjectRequest.builder()
          .bucket("b2b-edi-bucket")
          .key("incoming/transactions.txt")
          .build();
  ```

---

## 2. Uploading a File (Only 2 Lines of Code)

```java
// 1. Describe what and where to upload
PutObjectRequest request = PutObjectRequest.builder()
        .bucket(bucketName)
        .key(s3Key)
        .build();

// 2. Perform the upload from local disk path
s3Client.putObject(request, Paths.get(localFilePath));
```

* **In English:** *"Hey S3 client, take the file at `localFilePath` on my computer, and upload it into bucket `bucketName` with the name `s3Key`."*

---

## 3. Downloading a File (Only 2 Lines of Code)

```java
// 1. Describe what to download from S3
GetObjectRequest request = GetObjectRequest.builder()
        .bucket(bucketName)
        .key(s3Key)
        .build();

// 2. Download directly to local disk path
s3Client.getObject(request, Paths.get(destinationPath));
```

* **In English:** *"Hey S3 client, grab the object named `s3Key` from `bucketName`, and save it onto my local disk at `destinationPath`."*

---

## 4. How AWS Credentials Work (Zero Code)

* **No Hardcoding:** There are **NO secret keys, passwords, or access tokens** in the Java code.
* **Standard AWS Provider Chain:** When `S3Client.builder().build()` runs, the AWS SDK automatically looks for credentials in standard locations:
  1. Environment variables (`AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY`)
  2. Local credentials file created when you run `aws configure` in terminal (saved at `~/.aws/credentials`).
* **Why this is best practice:** Security! If you push code to GitHub or share it with an interviewer, your credentials are never leaked.

---

## 5. 30-Second Interview Summary

> *"In our project, we only used three core AWS SDK components: `S3Client` to establish the connection, `PutObjectRequest` to upload files, and `GetObjectRequest` to download files. Credentials are never hardcoded; the SDK automatically resolves them from the local environment."*
