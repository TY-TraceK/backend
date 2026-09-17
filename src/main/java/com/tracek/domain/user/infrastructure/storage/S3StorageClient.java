package com.tracek.domain.user.infrastructure.storage;

import com.tracek.domain.user.application.client.StorageClient;
import com.tracek.global.config.S3Properties;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Component
@Profile({"dev", "prod"})
@RequiredArgsConstructor
public class S3StorageClient implements StorageClient {

    private final S3Client s3Client;
    private final S3Properties s3Properties;

    @Override
    public String upload(MultipartFile file, String fileName) {
        try {
            PutObjectRequest request =
                    PutObjectRequest.builder()
                            .bucket(s3Properties.bucket())
                            .key(fileName)
                            .contentType(file.getContentType())
                            .build();

            s3Client.putObject(
                    request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            return createImageUrl(fileName);

        } catch (IOException e) {
            throw new IllegalStateException("S3 이미지 업로드에 실패했습니다. fileName=" + fileName, e);
        }
    }

    @Override
    public void delete(String fileName) {

        DeleteObjectRequest request =
                DeleteObjectRequest.builder().bucket(s3Properties.bucket()).key(fileName).build();

        s3Client.deleteObject(request);
    }

    private String createImageUrl(String fileName) {
        return String.format(
                "https://%s.s3.%s.amazonaws.com/%s",
                s3Properties.bucket(), s3Properties.region(), fileName);
    }
}
