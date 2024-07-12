package com.woosan.hr_system.report.service;

import com.woosan.hr_system.report.dao.ReportDAO;
import com.woosan.hr_system.report.model.Report;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.woosan.hr_system.report.model.FileMetadata;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Date;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportDAO reportDAO;

    @Override // 모든 보고서 조회
    public List<Report> getAllReports() {
        return reportDAO.getAllReports();
    }

    @Override // id를 이용한 특정 보고서 조회
    public Report getReportById(int reportId) {
        return reportDAO.getReportById(reportId);
    }

    @Override // 보고서 등록
    public void insertReport(Report report) {
        reportDAO.insertReport(report);
    }

    @Override // 보고서 전체 수정
    public void updateReport(Report report) {
        reportDAO.updateReport(report);
    }

    @Override // 보고서 일부 수정 (제목, 내용, 결재자, 결재상태(결재자만 가능), 거절사유(결재자만 가능), 업무완료날짜, 파일첨부)
    public void updateReportPartial(int reportId, Map<String, Object> updates) {
        Report report = reportDAO.getReportById(reportId);
        if (report != null) {
            if (updates.containsKey("title")) {
                report.setTitle((String)updates.get("title"));
            }
            if (updates.containsKey("content")) {
                report.setContent((String)updates.get("content"));
            }
            if (updates.containsKey("approver_id")) {
                report.setApproverId((String)updates.get("approver_id"));
            }
            if (updates.containsKey("status")) {
                report.setStatus((String)updates.get("status"));
            }
            if (updates.containsKey("reject_reason")) {
                report.setRejectReason((String)updates.get("reject_reason"));
            }
            if (updates.containsKey("complete_date")) {
                report.setCompleteDate((Timestamp) updates.get("complete_date"));
            }
            if (updates.containsKey("file_path")) {
                report.setFilePath((String)updates.get("file_path"));
            }
            reportDAO.updateReport(report);
        }
    }

    @Override // 보고서 삭제
    public void deleteReport(int reportId) {
        reportDAO.deleteReport(reportId);
    }

    @Override // 파일 첨부
    public List<FileMetadata> uploadFiles(Long reportId, MultipartFile[] files) throws IOException {
        List<FileMetadata> uploadedFiles = new ArrayList<>();
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                String originalFilename = file.getOriginalFilename();
                String uuidFilename = UUID.randomUUID().toString();
                String filePath = saveFile(file, uuidFilename);
                FileMetadata fileMetadata = saveMetadata(reportId, originalFilename, uuidFilename, filePath, file.getSize());
                uploadedFiles.add(fileMetadata);
            }
        }
        return uploadedFiles;
    }

    private String saveFile(MultipartFile file, String uuidFilename) throws IOException {
        String uploadDir = "/path/to/upload";
        File dest = new File(uploadDir + "/" + uuidFilename);
        file.transferTo(dest);
        return dest.getAbsolutePath();
    }

    private FileMetadata saveMetadata(Long reportId, String originalFilename, String uuidFilename, String filePath, long size) {
        FileMetadata metadata = new FileMetadata();
        metadata.setReportId(reportId);
        metadata.setCloudServerFileUrl("Cloud URL"); // 클라우드 URL 추후 설정
        metadata.setOriginalFilename(originalFilename);
        metadata.setUuidFilename(uuidFilename);
        metadata.setSize(size);
        metadata.setUploadDate(new Date());

        reportDAO.insertFileMetadata(metadata);

        return metadata;
    }
}
