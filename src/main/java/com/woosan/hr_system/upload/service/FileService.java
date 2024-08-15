package com.woosan.hr_system.upload.service;

import com.woosan.hr_system.upload.model.File;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {
    // == 조회 ==
    String getUrl(int fileId);
    File getFileInfo(int fileId);
    List<File> getFileListById(List<Integer> fileIdList);

    // == 업로드 ==
    int uploadingFile(MultipartFile file, String fileIdUsage);

    // == 다운로드와 삭제
    byte[] downloadFile(int fileId);
    void deleteFile(int fileId);
    void deleteFileByFileIdList(List<Integer> fileIdList);
}
