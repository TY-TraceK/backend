package com.tracek.domain.user.infrastructure.storage;

import com.tracek.domain.user.application.client.StorageClient;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@Profile("local")
public class LocalStorageClient implements StorageClient {

    private final Path rootPath;

    public LocalStorageClient(@Value("${storage.local.path}") String storagePath) {
        this.rootPath = Paths.get(storagePath).toAbsolutePath().normalize();

        createDirectory();
    }

    @Override
    public String upload(MultipartFile file, String fileName) {
        try {
            Path targetPath = rootPath.resolve(fileName).normalize();

            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            return "/uploads/" + fileName;

        } catch (IOException e) {
            throw new IllegalStateException("파일 저장에 실패했습니다. fileName=" + fileName, e);
        }
    }

    @Override
    public void delete(String fileName) {
        try {
            Path targetPath = rootPath.resolve(fileName).normalize();

            Files.deleteIfExists(targetPath);

        } catch (IOException e) {
            throw new IllegalStateException("파일 삭제에 실패했습니다. fileName=" + fileName, e);
        }
    }

    private void createDirectory() {
        try {
            Files.createDirectories(rootPath);
        } catch (IOException e) {
            throw new IllegalStateException("로컬 저장 디렉터리 생성에 실패했습니다.", e);
        }
    }
}
