package com.woosan.hr_system.file.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3StorageService {

    private final S3Client s3;
    private final S3Presigner presigner;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.s3.prefix:}")
    private String prefix; // 예: "uploads/" (없어도 OK)

    private String makeKey(String originalFilename) {
        String safe = (originalFilename == null ? "file" : originalFilename).replaceAll("\\s+", "_");
        return (prefix == null ? "" : prefix) + UUID.randomUUID() + "_" + safe;
    }

    /** 간단 업로드 (소용량) */
    public String upload(MultipartFile file) throws IOException {
        String key = makeKey(file.getOriginalFilename());
        PutObjectRequest req = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(file.getContentType() != null ? file.getContentType() : MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .build();

        PutObjectResponse resp = s3.putObject(req, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        SdkHttpResponse http = resp.sdkHttpResponse();
        if (http != null && http.isSuccessful()) return key;

        // SDK는 실패 시 예외를 던지므로 여기까지 오면 보통 성공
        return key;
    }

    /** 다운로드(스트림 반환) */
    public ResponseInputStream<GetObjectResponse> download(String key) {
        return s3.getObject(GetObjectRequest.builder().bucket(bucket).key(key).build());
    }

    /** 삭제 */
    public void delete(String key) {
        s3.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(key).build());
    }

    /** 읽기 프리사인드 URL (예: 10분) */
    public URL presignedGetUrl(String key, Duration ttl) {
        GetObjectPresignRequest preq = GetObjectPresignRequest.builder()
                .signatureDuration(ttl)
                .getObjectRequest(GetObjectRequest.builder().bucket(bucket).key(key).build())
                .build();
        return presigner.presignGetObject(preq).url();
    }

    /** 업로드 프리사인드 URL (클라이언트가 직접 PUT) */
    public URL presignedPutUrl(String filename, Duration ttl) {
        String key = makeKey(filename);
        PutObjectPresignRequest preq = PutObjectPresignRequest.builder()
                .signatureDuration(ttl)
                .putObjectRequest(PutObjectRequest.builder().bucket(bucket).key(key).build())
                .build();
        return presigner.presignPutObject(preq).url();
    }
}
