package com.woosan.hr_system.report.service;

import com.woosan.hr_system.report.model.Report;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ReportFileService {
    void createReportFile(int reportId, int fileId);
    // reportId로 fileId 리스트를 가져옴
    List<Integer> getFileIdsByReportId(int reportId);
    // fileId로 reportId 리스트를 가져옴
    List<Integer> getReportIdsByFileId(int fileId);
    void updateReportFile(Report report, List<MultipartFile> toUploadFileList, List<Integer> userSelectedFileIdList, List<Integer> existingFileIdList, List<Integer> createdReportIdList);
    void deleteReportFileByReportId(int reportId);
    void deleteReportFile(int reportId, int fileId);

}
