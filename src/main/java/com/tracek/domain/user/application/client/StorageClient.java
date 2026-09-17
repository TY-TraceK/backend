package com.tracek.domain.user.application.client;

import org.springframework.web.multipart.MultipartFile;

public interface StorageClient {

    String upload(MultipartFile file, String fileName);

    void delete(String fileName);
}
