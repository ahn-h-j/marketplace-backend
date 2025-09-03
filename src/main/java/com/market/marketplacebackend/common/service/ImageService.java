package com.market.marketplacebackend.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final S3Client s3Client;

    @Value("${r2.bucket-name}")
    private String bucketName;

    @Value("${r2.public-url}")
    private String publicUrl;

    public String uploadImage(MultipartFile file) throws IOException{
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(fileName)
                        .contentType(file.getContentType())
                        .build(),
                RequestBody.fromInputStream(file.getInputStream(), file.getSize())
        );
        return publicUrl + "/" + fileName;
    }

    public void deleteImage(String imageUrl){
        if(imageUrl != null && imageUrl.startsWith(publicUrl)){
            String fileName = imageUrl.substring(publicUrl.length() + 1 );
            s3Client.deleteObject(
                    DeleteObjectRequest.builder()
                            .bucket(bucketName)
                            .key(fileName)
                            .build()
            );
        }
    }
}
