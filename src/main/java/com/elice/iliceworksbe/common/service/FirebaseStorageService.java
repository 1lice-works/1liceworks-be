package com.elice.iliceworksbe.common.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FirebaseStorageService {
    String uploadImage(MultipartFile file) throws IOException;
    void deleteImage(String imageUrl);
}
