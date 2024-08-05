package com.woosan.hr_system.upload;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileService {

    @Autowired
    private S3Service s3Service;

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
    public String checkFile(MultipartFile file) {
        // 파일 확인
        checkNull(file);
        checkFileSize(file);

        // 파일 업로드
        try {
            return s3Service.uploadFile(file);
        } catch (Exception e) {
            throw new IllegalStateException("파일 업로드 중 오류가 발생하였습니다.\n파일 확인 후 재업로드 또는 관리자에게 문의해주세요.", e);
        }
    }

    // 파일들 확인 후 업로드하는 메소드
    public String checkFiles(MultipartFile[] files) {
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
            } catch (Exception e) {
                throw new IllegalStateException("파일 업로드 중 오류가 발생하였습니다.\n파일 확인 후 재업로드 또는 관리자에게 문의해주세요.", e);
            }
        }
        return sb.toString();
    }

    // 사진의 주소를 반환하는 메소드
    public String getUrl(String fileName) {
        String prefix = "https://haruharu-hrsystem-bucket.s3.amazonaws.com/";
        return prefix + fileName;
    }
}
