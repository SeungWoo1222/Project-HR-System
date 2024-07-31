package com.woosan.hr_system.report.service;

import com.woosan.hr_system.auth.CustomUserDetails;
import com.woosan.hr_system.report.dao.ReportDAO;
import com.woosan.hr_system.report.model.FileMetadata;
import com.woosan.hr_system.report.model.Report;
import com.woosan.hr_system.report.model.ReportStat;
import com.woosan.hr_system.report.model.Request;
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
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportDAO reportDAO;

    @Override // 보고서 생성
    public void createReport(String title, String content, List<String> approverIds, List<String> approverNames, LocalDate completeDate, MultipartFile file, String writerId) {
        LocalDateTime createdDate = LocalDateTime.now(); // 현재 기준 생성시간 설정
        List<Report> reports = new ArrayList<>();

        for (int i = 0; i < approverIds.size(); i++) {
            Report report = new Report();
            report.setTitle(title);
            report.setContent(content);
            report.setApproverId(approverIds.get(i));
            report.setApproverName(approverNames.get(i));
            report.setCompleteDate(completeDate);
            report.setWriterId(writerId);
            report.setCreatedDate(createdDate);
            report.setStatus("미처리"); // 기본 결재 상태 설정

            reports.add(report);
            // 파일 업로드
//            if (!file.isEmpty()) {
//                reportDAO.uploadFiles(report.getReportId(), new MultipartFile[]{file});
//            }
        }

        reportDAO.createReport(reports);
    }

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
    public List<ReportStat> getReportStats(String statisticStart, String statisticEnd, List<String> writerIds) {

        // 입력된 날짜를 파싱하기 위한 DateTimeFormatter
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        YearMonth startYearMonth;
        YearMonth endYearMonth;

        // 현재 연월 가져오기
        YearMonth currentYearMonth = YearMonth.now();

        // startDate와 endDate가 null인지 확인하고 현재 연월로 설정
        if (statisticStart == null) {
            startYearMonth = currentYearMonth;
        } else {
            startYearMonth = YearMonth.parse(statisticStart, formatter);
        }

        if (statisticEnd == null) {
            endYearMonth = currentYearMonth;
        } else {
            endYearMonth = YearMonth.parse(statisticEnd, formatter);
        }

        return reportDAO.getReportStats(startYearMonth, endYearMonth, writerIds);
    }

    @Override // 날짜범위 내 결재할 보고서 조회
    public List<Report> getPendingApprovalReports(String approverId, String approvalStart, String approvalEnd) {
        // 입력된 날짜를 파싱하기 위한 DateTimeFormatter
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        YearMonth startYearMonth;
        YearMonth endYearMonth;

        // 현재 연월 가져오기
        YearMonth currentYearMonth = YearMonth.now();

        // startDate와 endDate가 null인지 확인하고 현재 연월로 설정
        if (approvalStart == null) {
            startYearMonth = currentYearMonth;
        } else {
            startYearMonth = YearMonth.parse(approvalStart, formatter);
        }

        if (approvalEnd == null) {
            endYearMonth = currentYearMonth;
        } else {
            endYearMonth = YearMonth.parse(approvalEnd, formatter);
        }

        // 파싱된 날짜를 DAO로 전달하여 호출
        return reportDAO.getPendingApprovalReports(approverId, startYearMonth, endYearMonth);
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
