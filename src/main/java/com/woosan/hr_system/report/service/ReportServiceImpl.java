package com.woosan.hr_system.report.service;

import com.woosan.hr_system.auth.CustomUserDetails;
import com.woosan.hr_system.report.dao.ReportDAO;
import com.woosan.hr_system.report.model.FileMetadata;
import com.woosan.hr_system.report.model.Report;
import com.woosan.hr_system.report.model.ReportStat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportDAO reportDAO;

    @Override // 모든 보고서 조회
    public List<Report> getAllReports() {
        return reportDAO.getAllReports();
    }

    @Override // 특정 보고서 조회
    public Report getReportById(Long reportId) {
        return reportDAO.getReportById(reportId);
    }

    @Override // 특정 파일 조회
    public FileMetadata getReportFileById(Long fileId) {
        return reportDAO.getReportFileById(fileId);
    }

    @Override // 보고서 통계 조회
    public List<ReportStat> getReportStats(String startDate, String endDate) {

        // 기본 통계 데이터를 최근 1개월로 조회
        if (startDate == null && endDate == null) {
            LocalDate end = LocalDate.now();
            LocalDate start = end.minusMonths(1);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            startDate = start.format(formatter);
            endDate = end.format(formatter);
        }

        return reportDAO.getReportStats(startDate, endDate);
    }

    @Override // 결재할 보고서 조회
    public List<Report> getPendingApprovalReports() {
        // admin에서 보고서 조회할 경우
        // 로그인한 계정 기준 employee_id를 approvalId로 설정 -> Mapper에서 결재목록을 반환해줌
        String approvalId = null;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            approvalId = userDetails.getUsername();
        }

        return reportDAO.getPendingApprovalReports(approvalId);

    }

    @Override // 보고서 생성
    public void createReport(Report report, MultipartFile file) throws IOException {
        LocalDateTime createdDate = LocalDateTime.now(); // 현재 기준 생성시간 설정
        report.setCreatedDate(createdDate);
        report.setStatus("미처리"); // 기본 결재 상태 설정

        // 현재 로그인 한 사용자 employeeId를 보고서 작성자(employee_id)로 설정
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            report.setEmployeeId(userDetails.getUsername());
        }

        reportDAO.createReport(report);

        // 파일 업로드
        if (!file.isEmpty()) {
            reportDAO.uploadFiles(report.getReportId(), new MultipartFile[]{file});
        }
    }

    @Override // 파일 업로드
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

    // 파일 변수 설정
    private String saveFile(MultipartFile file, String uuidFilename) throws IOException {
        String uploadDir = "/path/to/upload";
        File dest = new File(uploadDir + "/" + uuidFilename);
        file.transferTo(dest);
        return dest.getAbsolutePath();
    }

    // 파일 정보 저장
    private FileMetadata saveMetadata(Long reportId, String originalFilename, String uuidFilename, String filePath, long size) {
        FileMetadata metadata = new FileMetadata();
        LocalDate now = LocalDate.now();

        metadata.setCloudServerFileUrl("Cloud URL"); // 클라우드 URL 추후 설정
        metadata.setOriginalFilename(originalFilename);
        metadata.setUuidFilename(uuidFilename);
        metadata.setSize(size);
        metadata.setUploadDate(now);

        reportDAO.insertFileMetadata(metadata);

        return metadata;
    }

    @Override // 보고서 수정
    public void updateReport(Report report) {
        LocalDateTime modified_date = LocalDateTime.now(); // 현재 기준 수정 시간 설정
        report.setModifiedDate(modified_date);

        reportDAO.updateReport(report);
    }


    @Override // 보고서 삭제
    public void deleteReport(Long id) {
        reportDAO.deleteReport(id);
    }




}
