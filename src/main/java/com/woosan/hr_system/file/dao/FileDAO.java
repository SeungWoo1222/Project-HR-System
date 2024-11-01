package com.woosan.hr_system.file.dao;

import com.woosan.hr_system.file.model.File;
import java.util.List;
import java.util.Map;

public interface FileDAO {
    List<File> getAllFiles();
    File getFileById(int fileId);
    List<File> getFileListById(List<Integer> fileIdList);
    String getFileStoredNameById(int fileId);
    int insertFile(File file);
    int updateFile(File file);
    int deleteFile(int fileId);
    void deleteFileByFileIdList(List<Integer> fileIdList);
    int isDuplicateExist(Map<String, Object> map);
}
