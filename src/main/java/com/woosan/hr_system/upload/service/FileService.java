package com.woosan.hr_system.upload.service;

import com.woosan.hr_system.auth.aspect.RequireManagerPermission;
import com.woosan.hr_system.auth.model.ModificationInfo;
import com.woosan.hr_system.auth.service.AuthService;
import com.woosan.hr_system.exception.file.FileBadRequestException;
import com.woosan.hr_system.exception.file.FileInfoNotFoundException;
import com.woosan.hr_system.exception.file.FileProcessingException;
import com.woosan.hr_system.upload.dao.FileDAO;
import com.woosan.hr_system.upload.model.File;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Service
public class FileService {

    @Autowired
    private S3Service s3Service;

    @Autowired
    private FileDAO fileDAO;

    private static final String S3_URL_PREFIX = "https://haruharu-hrsystem-bucket.s3.amazonaws.com/";
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    @Autowired
    private AuthService authService;

    // 사진의 주소를 반환하는 메소드
    public String getUrl(String fileName) {
        return S3_URL_PREFIX + fileName;
    }


    // 파일 삭제
    @Transactional
    @RequireManagerPermission
    public void deleteFile(int fileId) {
        // 파일 정보 조회
        File file = fileDAO.selectFileById(fileId);
        validateFileInfo(file, fileId);

        // DB에서 해당 파일 삭제
        fileDAO.deleteFile(fileId);

        // S3에서 제거
        deleteFileFromS3(file);
    }

    private void deleteFileFromS3(File file) {
        String storedFileName = file.getStoredFileName();
        s3Service.deleteFileFromS3(storedFileName);
    }

    // 파일 확인 후 업로드
    public void uploadingFile(MultipartFile file, String usage) {
        // 파일 검증
        validateFile(file);

        // 파일 업로드
        String storedFileName = uploadToS3(file);

        try {
            // 트랜잭션 시작
            saveFileTransactionally(file, storedFileName, usage);
        } catch (Exception e) {
            // 트랜잭션 실패 시 파일 삭제
            s3Service.deleteFileFromS3(storedFileName);
            throw e;
        }
    }

    // 트랜잭션 내부에서 파일 정보 저장
    @Transactional
    protected void saveFileTransactionally(MultipartFile file, String storedFileName, String usage) {
        // 파일 정보 생성
        File fileInfo = createFileInfo(file, storedFileName, usage);

        // DB에 파일 정보 입력
        fileDAO.insertFile(fileInfo);

        // 트랜잭션 리스너 등록
        registerRollbackHandler(storedFileName);
    }

    // 트랜잭션 리스너 등록
    private void registerRollbackHandler(String storedFileName) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
            @Override
            public void afterCompletion(int status) {
                if (status == STATUS_ROLLED_BACK) {
                    s3Service.deleteFileFromS3(storedFileName);
                }
            }
        });
    }

    // S3에 파일 업로드
    private String uploadToS3(MultipartFile file) {
        try {
            return s3Service.uploadFile(file);
        } catch (IOException e) {
            log.error("파일 업로드 작업 중 예외가 발생하였습니다. 파일명: {}, 크기: {}", file.getOriginalFilename(), file.getSize(), e);
            throw new FileProcessingException("파일 업로드 중 문제가 발생했습니다. 다시 시도해 주세요.");
        }
    }

    // 파일 정보 생성
    private File createFileInfo(MultipartFile file, String storedFileName, String usage) {
        ModificationInfo modificationInfo = new ModificationInfo();
        return new File(
                file.getOriginalFilename(),
                storedFileName,
                file.getSize(),
                modificationInfo.getCurrentEmployeeId(),
                modificationInfo.getNow(),
                usage
        );
    }

    // 파일 유효성 검사
    private void validateFile(MultipartFile file) {
        checkFileNull(file);
        checkFileSize(file);
    }

    // 파일 Null 확인
    private void checkFileNull(MultipartFile file) {
        if (file.isEmpty()) {
            throw new FileBadRequestException("파일이 비어있습니다.\n파일을 확인해주세요.");
        }
    }

    // 파일 사이즈 확인
    private void checkFileSize(MultipartFile file) {
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new FileBadRequestException("파일 크기가 10MB를 초과합니다.\n파일을 확인해주세요.");
        }
    }

    // 업로드 파일 개수 확인
    private void checkFilesLength(MultipartFile[] files) {
        if (files.length > 3) {
            throw new FileBadRequestException("최대 3개의 파일만 업로드할 수 있습니다.");
        }
    }

    // 파일들 확인 후 업로드하는 메소드
    public String checkAndUploadFiles(MultipartFile[] files) {
        // 업로드 파일 개수 확인
        checkFilesLength(files);

        // 각각의 파일 확인
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < files.length; i++) {
            checkFileNull(files[i]);
            checkFileSize(files[i]);
            try {
                sb.append(s3Service.uploadFile(files[i]));
                if (i != files.length - 1) { sb.append("/"); }
            } catch (IOException e) {
                log.error("다중 파일 업로드 작업 중 예외가 발생하였습니다.", e);
                throw new FileProcessingException("파일 업로드 중 문제가 발생했습니다. 다시 시도해 주세요.");
            }
        }
        return sb.toString();
    }

    // 파일 정보 확인
    private void validateFileInfo(File fileInfo, int fileId) {
        if (fileInfo == null) throw new FileInfoNotFoundException(fileId);
    }
}

