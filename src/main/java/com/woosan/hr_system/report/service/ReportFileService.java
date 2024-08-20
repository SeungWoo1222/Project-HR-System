package com.woosan.hr_system.report.service;

import com.woosan.hr_system.report.model.Report;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ReportFileService {
    void createReportFile(int reportId, int fileId);
    // reportId로 fileId 리스트를 가져옴
    List<Integer> getFileIdsByReportId(int reportId);
    // fileId로 reportId를 가져옴
    int getReportIdByFileId(int fileId);
    void updateReportFile(Report report, List<MultipartFile> toUploadFileList, List<Integer> registeredFileIdList, List<Integer> reportIdList);
    void deleteReportFileByReportId(int reportId);
    void deleteReportFile(int reportId, int fileId);

}
