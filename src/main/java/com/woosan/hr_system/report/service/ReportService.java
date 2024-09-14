package com.woosan.hr_system.report.service;

import com.woosan.hr_system.report.model.ReportFileLink;
import com.woosan.hr_system.report.model.ReportStat;
import com.woosan.hr_system.search.PageRequest;
import com.woosan.hr_system.search.PageResult;
import org.springframework.web.multipart.MultipartFile;
import com.woosan.hr_system.report.model.Report;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public interface ReportService {
//======================================================생성============================================================
    // 보고서 생성
    List<Integer> createReport(Report report);
    // 보고서 + 파일 생성
    void createReportWithFile(Report report, List<MultipartFile> reportDocuments);
    // 요청 들어온 보고서 생성
    int createReportFromRequest(Report report);
    // 요청 들어온 보고서 + 파일 생성
    int createReportFromRequestWithFile(Report report, List<MultipartFile> reportDocuments);
//======================================================생성============================================================
//======================================================조회============================================================
    // 모든 리포트 조회
    List<Report> getAllReports(String employeeId);
    // 특정 리포트 조회
    Report getReportById(int reportId);
    // 최근 5개 보고서 조회
    List<Report> getRecentReports(String writerId);
    // 내가 쓴 보고서 페이징, 검색 + 보고서 조회
    PageResult<Report> searchReports(PageRequest pageRequest, String writerId, Integer searchType, String approvalStatus, LocalDate startDate, LocalDate endDate);
    // 결재 할 보고서 페이징, 검색 + 보고서 조회 (MANAGER)
    PageResult<Report> toApproveSearchReports(PageRequest pageRequest, String approverId, Integer searchType, String approvalStatus, LocalDate startDate, LocalDate endDate);
    // 결재 미처리 보고서 조회(MANAGER)
    List<Report> getUnprocessedReports(String approverId);
    // 보고서 통계 조회
    List<ReportStat> getReportStats(LocalDate startDate, LocalDate endDate, List<String> writerIdList);
//======================================================조회============================================================
//======================================================수정============================================================
    // 보고서 수정 관련 메소드
    void updateReport(Report report, List<MultipartFile> toUploadFileList, List<Integer> registeredFileIdList);
    // 보고서 결재 처리
    void updateApprovalStatus(int reportId, String status, String rejectionReasont);
//======================================================수정============================================================
//======================================================삭제============================================================
    // 보고서 삭제
    void deleteReport(int reportId);
//======================================================삭제============================================================


}
