package com.woosan.hr_system.upload;

import com.woosan.hr_system.exception.file.FileException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Service
public class FileService {

    @Autowired
    private S3Service s3Service;

    private static final String S3_URL_PREFIX = "https://haruharu-hrsystem-bucket.s3.amazonaws.com/";
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    // 파일 Null 확인하는 메소드
    private void checkNull(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어있습니다.\n파일을 확인 후 재업로드해주세요.");
        }
    }

    // 파일 사이즈 확인하는 메소드
    private void checkFileSize(MultipartFile file) {
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("파일 크기가 10MB를 초과합니다.\n파일을 확인 후 재업로드해주세요.");
        }
    }

    // 업로드 파일 개수 확인하는 메소드
    private void checkFilesLength(MultipartFile[] files) {
        if (files.length > 3) {
            throw new IllegalArgumentException("최대 3개의 파일만 업로드할 수 있습니다.");
        }
    }

    // 파일 확인 후 업로드하는 메소드
    public String checkAndUploadFile(MultipartFile file) {
        // 파일 확인
        checkNull(file);
        checkFileSize(file);

        // 파일 업로드
        try {
            return s3Service.uploadFile(file);
        } catch (IOException e) {
            log.error("단일 파일 업로드 작업 중 예외가 발생하였습니다.", e);
            throw new FileException("파일 업로드 중 문제가 발생했습니다. 다시 시도해 주세요.");
        }
    }

    // 파일들 확인 후 업로드하는 메소드
    public String checkAndUploadFiles(MultipartFile[] files) {
        // 업로드 파일 개수 확인
        checkFilesLength(files);

        // 각각의 파일 확인
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < files.length; i++) {
            checkNull(files[i]);
            checkFileSize(files[i]);
            try {
                sb.append(s3Service.uploadFile(files[i]));
                if (i != files.length - 1) { sb.append("/"); }
            } catch (IOException e) {
                log.error("다중 파일 업로드 작업 중 예외가 발생하였습니다.", e);
                throw new FileException("파일 업로드 중 문제가 발생했습니다. 다시 시도해 주세요.");
            }
        }
        return sb.toString();
    }

    // 사진의 주소를 반환하는 메소드
    public String getUrl(String fileName) {
        return S3_URL_PREFIX + fileName;
    }
}
