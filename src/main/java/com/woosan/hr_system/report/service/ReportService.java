package com.woosan.hr_system.report.service;

import com.woosan.hr_system.report.model.ReportFileLink;
import com.woosan.hr_system.report.model.ReportStat;
import com.woosan.hr_system.search.PageRequest;
import com.woosan.hr_system.search.PageResult;
import org.springframework.web.multipart.MultipartFile;
import com.woosan.hr_system.report.model.Report;

import java.util.List;

public interface ReportService {
//======================================================생성============================================================
    // 보고서 생성
    List<Integer> createReport(Report report);

    // 보고서 + 파일 생성
    void createReportWithFile(Report report, List<MultipartFile> reportDocuments);

    //요청들어온 보고서 생성
    int createReportFromRequest(Report report, String approverId);

//======================================================생성============================================================
//======================================================조회============================================================
    // 모든 리포트 조회
    List<Report> getAllReports(String reportStart, String reportEnd, String employeeId);

    // 특정 리포트 조회
    Report getReportById(int reportId);

    // 최근 5개 보고서 조회
    List<Report> getRecentReports(String writerId);

    // 페이징, 검색 + 보고서 조회
    PageResult<Report> searchReports(PageRequest pageRequest, String writerId, int searchType, String reportStart, String reportEnd); // 페이징, 서칭 + 보고서 리스트

    // 결재할 보고서 목록 조회
    List<Report> getPendingApprovalReports(String approverId, String approvalStart, String approvalEnd);

    // 보고서 통계 조회
    List<ReportStat> getReportStats(String statisticStart, String statisticEnd, List<String> writerIdList);

//======================================================조회============================================================
//======================================================수정============================================================

    // 보고서 수정 관련 메소드
    void updateReport(Report report, List<MultipartFile> reportFileList, List<Integer> registeredFileIdList);

//======================================================수정============================================================
//======================================================삭제============================================================
    // 보고서 삭제
    void deleteReport(int reportId);

//======================================================삭제============================================================


}
