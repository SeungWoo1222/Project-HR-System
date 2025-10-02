package com.woosan.hr_system.file.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "s3.enabled", havingValue = "true", matchIfMissing = false)
public class S3Service {

    private final S3Client s3Client;  // ✅ v2 클라이언트만 사용

    @Value("${aws.s3.bucket}")
    private String bucketName;

    /** 업로드 (임시 파일 없이 스트림으로 바로 업로드) */
    protected String uploadFile(MultipartFile file) throws IOException {
        String key = System.currentTimeMillis() + "." + file.getOriginalFilename();

        PutObjectRequest putReq = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(file.getContentType())
                .build();

        try (InputStream in = file.getInputStream()) {
            s3Client.putObject(putReq, RequestBody.fromInputStream(in, file.getSize()));
        }
        return key;
    }

    /** 다운로드 (바이트 반환) */
    protected byte[] downloadFile(String storedFileName) {
        GetObjectRequest getReq = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(storedFileName)
                .build();

        ResponseBytes<GetObjectResponse> bytes = s3Client.getObjectAsBytes(getReq);
        return bytes.asByteArray();
    }

    /** 삭제 */
    protected void deleteFileFromS3(String storedFileName) {
        try {
            DeleteObjectRequest delReq = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(storedFileName)
                    .build();
            s3Client.deleteObject(delReq);
            log.info("S3에서 '{}' 파일이 삭제되었습니다.", storedFileName);
        } catch (Exception e) {
            log.error("S3 삭제 오류: {}", storedFileName, e);
            throw new com.woosan.hr_system.exception.file.FileProcessingException("파일 삭제 중 문제가 발생했습니다.");
        }
    }
}
