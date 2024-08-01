package com.woosan.hr_system.report.dao;

import com.woosan.hr_system.report.model.FileMetadata;
import com.woosan.hr_system.report.model.Report;
import com.woosan.hr_system.report.model.ReportStat;
import com.woosan.hr_system.report.model.Request;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public class ReportDAO {
    @Autowired
    private SqlSession sqlSession;
    private static final String NAMESPACE = "com.woosan.hr_system.report.dao.ReportDAO";

    // 보고서 생성
    public void createReport(List<Report> reports) {
        sqlSession.insert(NAMESPACE + ".createReport", reports);
    }

    // 보고서 전체 조회
    public List<Report> getAllReports() {
        return sqlSession.selectList(NAMESPACE + ".getAllReports");
    }

    // 특정 보고서 조회
    public Report getReportById(Long reportId) {
        return sqlSession.selectOne(NAMESPACE + ".getReportById", reportId);
    }

    // 특정 파일 조회
    public FileMetadata getReportFileById(Long fileId) {
        return sqlSession.selectOne(NAMESPACE + ".getReportFileById", fileId);
    }

    // 결재할 보고서 조회
    public List<Report> getPendingApprovalReports(String approverId, YearMonth startYearMonth, YearMonth endYearMonth) {
        // Map 설정 (Mapper에서 각 요소의 유무를 빠르게 파악하고 가독성, 재사용성을 위해)
        Map<String, Object> params = new HashMap<>();
        params.put("approverId", approverId);
        params.put("startYearMonth", startYearMonth);
        params.put("endYearMonth", endYearMonth);

        return sqlSession.selectList(NAMESPACE + ".getAllReports", params);
    }

    // 보고서 통계 조회
    public List<ReportStat> getReportStats(YearMonth startYearMonth, YearMonth endYearMonth, List<String> writerIds) {
        Map<String, Object> params = new HashMap<>();
        params.put("startYearMonth", startYearMonth);
        params.put("endYearMonth", endYearMonth);
        if (writerIds != null && !writerIds.isEmpty()) {
            params.put("writerIds", writerIds);
        } else {
            params.put("writerIds", null);  // writerIds가 null이면 임원 전체 선택
        }
        return sqlSession.selectList(NAMESPACE + ".getReportStats", params);
    }


    // 파일 DB 정보 생성
    public void insertFileMetadata(FileMetadata fileMetadata) {
        sqlSession.insert(NAMESPACE + ".insertFileMetadata", fileMetadata);
    }

    // 파일 업로드
    public void uploadFiles(Long reportId, MultipartFile[] files) throws IOException {
        for (MultipartFile file : files) {
            // 파일 메타데이터 저장
            FileMetadata metadata = new FileMetadata();
            metadata.setOriginalFilename(file.getOriginalFilename());
            metadata.setUuidFilename(UUID.randomUUID().toString());
            metadata.setSize(file.getSize());
            metadata.setUploadDate(LocalDate.now());

            // 메타데이터 DB에 저장
            sqlSession.insert(NAMESPACE + ".insertFileMetadata", metadata);
        }
    }

    // 보고서 수정
    public void updateReport(Report report) {
        sqlSession.update(NAMESPACE + ".updateReport", report);
    }

    // 보고서 삭제
    public void deleteReport(Long id) {
        sqlSession.delete(NAMESPACE + ".deleteReport", id);
    }

    // 파일 삭제
    public void deleteFileMetadataByReportId(Long reportId) {
        sqlSession.delete(NAMESPACE + ".deleteFileMetadataByReportId", reportId);
    }



}
