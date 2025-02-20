package com.elice.iliceworksbe.common.service.impl;

import com.elice.iliceworksbe.common.config.property.FirebaseProperty;
import com.elice.iliceworksbe.common.exception.BaseException;
import com.elice.iliceworksbe.common.exception.ErrorCode;
import com.elice.iliceworksbe.common.service.FirebaseStorageService;
import com.google.cloud.storage.Blob;
import com.google.firebase.cloud.StorageClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FirebaseStorageServiceImpl implements FirebaseStorageService {

    private final FirebaseProperty firebaseProperty;

    public String uploadImage(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        String bucketName = firebaseProperty.getBucketName();

        Blob blob = StorageClient.getInstance().bucket(bucketName).create(fileName, file.getInputStream(), file.getContentType());

        return "https://firebasestorage.googleapis.com/v0/b/" + bucketName + "/o/" + fileName + "?alt=media";
    }

    @Override
    public void deleteImage(String imageUrl) {
        try {
            String fileName = imageUrl.substring(imageUrl.indexOf("/o/") + 3, imageUrl.indexOf("?alt=media"));
            StorageClient.getInstance().bucket(firebaseProperty.getBucketName()).get(fileName).delete();
        } catch (Exception e) {
            throw new BaseException(ErrorCode.IMAGE_DELETE_FAILED);
        }
    }
}
