package com.woosan.hr_system.report.service;

import com.woosan.hr_system.report.model.ReportStat;
import com.woosan.hr_system.search.PageRequest;
import com.woosan.hr_system.search.PageResult;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import com.woosan.hr_system.report.model.Report;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import com.woosan.hr_system.report.model.FileMetadata;

public interface ReportService {
    // 모든 리포트 조회
    List<Report> getAllReports(String reportStart, String reportEnd, String employeeId);
    // 특정 리포트 조회
    Report getReportById(Long reportId);
    // 보고서 생성
//    void createReport(Report report, MultipartFile file);
    void createReport(Report report, List<MultipartFile> reportDocuments);
    //    List<FileMetadata> uploadFiles(Long reportId, MultipartFile[] files) throws IOException;
    // 보고서 수정 관련 메소드
    void updateReport(Report report);
    // 보고서 삭제
    void deleteReport(Long reportId);
    //요청들어온 보고서 작성
    Long createReportFromRequest(Report report, String approverId);
    // 최근 5개 보고서 조회
    List<Report> getRecentReports(String writerId);
    // 페이징, 검색 + 보고서 조회
    PageResult<Report> searchReports(PageRequest pageRequest, String writerId, int searchType, String reportStart, String reportEnd); // 페이징, 서칭 + 보고서 리스트
    // 파일 조회
//    FileMetadata getReportFileById(Long fileId);
    // 결재할 보고서 목록 조회
    List<Report> getPendingApprovalReports(String approverId, String approvalStart, String approvalEnd);
    // 보고서 통계 조회
    List<ReportStat> getReportStats(String statisticStart, String statisticEnd, List<String> writerIdList);

}
