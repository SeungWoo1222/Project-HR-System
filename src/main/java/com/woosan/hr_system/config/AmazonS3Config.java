package com.woosan.hr_system.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
@Configuration
@ConditionalOnProperty(name = "s3.enabled", havingValue = "true")
public class AmazonS3Config {

    @Bean
    public S3Client s3Client(
            @Value("${spring.cloud.aws.region.static}") String region,
            @Value("${cloud.aws.s3.endpoint:}") String endpoint // 로컬이면 값 존재, 운영은 비어있음
    ) {
        var builder = S3Client.builder()
                .region(software.amazon.awssdk.regions.Region.of(region))
                .credentialsProvider(software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider.create());

        if (!endpoint.isBlank()) {
            builder.endpointOverride(java.net.URI.create(endpoint));
        }
        return builder.build();
    }

    @Bean
    public S3Presigner s3Presigner(
            @Value("${spring.cloud.aws.region.static}") String region,
            @Value("${cloud.aws.s3.endpoint:}") String endpoint
    ) {
        var builder = S3Presigner.builder()
                .region(software.amazon.awssdk.regions.Region.of(region))
                .credentialsProvider(software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider.create());
        if (!endpoint.isBlank()) {
            builder.endpointOverride(java.net.URI.create(endpoint));
        }
        return builder.build();
    }
}

