package com.woosan.hr_system.report.service;

import com.woosan.hr_system.employee.model.Employee;
import com.woosan.hr_system.report.dao.ReportDAO;
import com.woosan.hr_system.report.model.FileMetadata;
import com.woosan.hr_system.report.model.Report;
import com.woosan.hr_system.report.model.ReportStat;
import com.woosan.hr_system.search.PageRequest;
import com.woosan.hr_system.search.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportDAO reportDAO;

    @Override // 보고서 생성
    public void createReport(Report report) {
//    public void createReport(Report report, MultipartFile file) {
        LocalDateTime createdDate = LocalDateTime.now(); // 현재 기준 생성시간 설정
        report.setCreatedDate(createdDate);
        report.setStatus("미처리"); // 결재상태 설정

        for (int i = 0; i < report.getNameList().size(); i++) {
            Map<String, Object> params = new HashMap<>();
            params.put("writerId", report.getWriterId());
            params.put("approverId", report.getIdList().get(i));
            params.put("approverName", report.getNameList().get(i));
            params.put("title", report.getTitle());
            params.put("content", report.getContent());
            params.put("createdDate", report.getCreatedDate());
            params.put("status", report.getStatus());
            params.put("completeDate", report.getCompleteDate());

//            reportDAO.createReport(params, file);
            reportDAO.createReport(params);
        }
//            reports.add(report);
            // 파일 업로드
//            if (!file.isEmpty()) {
//                reportDAO.uploadFiles(report.getReportId(), new MultipartFile[]{file});
//            }

    }

    @Override // 모든 보고서 조회
    public List<Report> getAllReports(String reportStart, String reportEnd, String employeeId) {
        // 입력된 날짜를 파싱하기 위한 DateTimeFormatter
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        YearMonth startYearMonth;
        YearMonth endYearMonth;

        // 현재 연월 가져오기
        YearMonth currentYearMonth = YearMonth.now();

        // startDate와 endDate가 null인지 확인하고 현재 연월로 설정
        if (reportStart == null) {
            startYearMonth = currentYearMonth;
        } else {
            startYearMonth = YearMonth.parse(reportStart, formatter);
        }

        if (reportEnd == null) {
            endYearMonth = currentYearMonth;
        } else {
            endYearMonth = YearMonth.parse(reportEnd, formatter);
        }
        return reportDAO.getAllReports(employeeId, startYearMonth, endYearMonth);
    }

    @Override // 최근 5개 보고서 조회
    public List<Report> getRecentReports(String reportStart, String reportEnd, String employeeId) {
        // 입력된 날짜를 파싱하기 위한 DateTimeFormatter
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        YearMonth startYearMonth;
        YearMonth endYearMonth;

        // 현재 연월 가져오기
        YearMonth currentYearMonth = YearMonth.now();

        // startDate와 endDate가 null인지 확인하고 현재 연월로 설정
        if (reportStart == null) {
            startYearMonth = currentYearMonth;
        } else {
            startYearMonth = YearMonth.parse(reportStart, formatter);
        }

        if (reportEnd == null) {
            endYearMonth = currentYearMonth;
        } else {
            endYearMonth = YearMonth.parse(reportEnd, formatter);
        }
        return reportDAO.getRecentReports(employeeId, startYearMonth, endYearMonth);
    }

    @Override
    public PageResult<Report> searchReports(PageRequest pageRequest, String writerId, int searchType) {
        int offset = pageRequest.getPage() * pageRequest.getSize();
        List<Report> reports = reportDAO.search(pageRequest.getKeyword(), pageRequest.getSize(), offset, writerId, searchType);
        int total = reportDAO.count(pageRequest.getKeyword(), searchType);

        System.out.println(total);

        return new PageResult<>(reports, (int) Math.ceil((double)total / pageRequest.getSize()), total, pageRequest.getPage());
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
    public List<ReportStat> getReportStats(String statisticStart, String statisticEnd, List<String> writerIdList) {

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

        return reportDAO.getReportStats(startYearMonth, endYearMonth, writerIdList);
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



//    @Override // 파일 업로드
//    public List<FileMetadata> uploadFiles(Long reportId, MultipartFile[] files) throws IOException {
//        List<FileMetadata> uploadedFiles = new ArrayList<>();
//        for (MultipartFile file : files) {
//            if (!file.isEmpty()) {
//                String originalFilename = file.getOriginalFilename();
//                String uuidFilename = UUID.randomUUID().toString();
//                String filePath = saveFile(file, uuidFilename);
//                FileMetadata fileMetadata = saveMetadata(reportId, originalFilename, uuidFilename, filePath, file.getSize());
//                uploadedFiles.add(fileMetadata);
//            }
//        }
//        return uploadedFiles;
//    }

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
