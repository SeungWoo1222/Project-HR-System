package com.woosan.hr_system.upload.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.woosan.hr_system.exception.file.FileProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
public class S3Service {
    private static final Logger logger = LoggerFactory.getLogger(S3Service.class);

    @Autowired
    private AmazonS3 amazonS3;

    @Autowired
    private S3Client s3Client; // AWS SDK v2 S3Client

    private String bucketName = "haruharu-hrsystem-bucket";

    // S3에 파일 업로드
    protected String uploadFile(MultipartFile file) throws IOException {
        File convertedFile = convertMultiPartToFile(file);
        String fileName = System.currentTimeMillis() + "." + file.getOriginalFilename();
        amazonS3.putObject(new PutObjectRequest(bucketName, fileName, convertedFile));
        logger.info("S3에 '{}' 파일이 등록되었습니다.", fileName);
        convertedFile.delete();
        return fileName;
    }

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(file.getBytes());
        }
        return convFile;
    }

    // S3에서 파일 다운로드
    protected byte[] downloadFile(String keyName) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .build();

        ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(getObjectRequest);
        return objectBytes.asByteArray();
    }

    // S3에서 파일 삭제
    protected void deleteFileFromS3(String storedFileName) {
        try {
            amazonS3.deleteObject(new DeleteObjectRequest(bucketName, storedFileName));
            logger.info("S3에서 '{}' 파일이 삭제되었습니다.", storedFileName);
        } catch (Exception e) {
            logger.error("S3에서 '{}' 파일 삭제 중 오류가 발생하였습니다.", storedFileName, e);
            throw new FileProcessingException("파일 삭제 중 문제가 발생했습니다.");
        }
    }
}
