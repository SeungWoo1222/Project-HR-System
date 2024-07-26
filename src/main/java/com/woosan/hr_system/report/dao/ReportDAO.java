package com.woosan.hr_system.report.dao;

import com.woosan.hr_system.report.model.FileMetadata;
import com.woosan.hr_system.report.model.Report;
import com.woosan.hr_system.report.model.ReportStat;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public class ReportDAO {
    @Autowired
    private SqlSession sqlSession;
    private static final String NAMESPACE = "com.woosan.hr_system.report.dao.ReportDAO";

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
    public List<Report> getPendingApprovalReports(String approverId) {
        // approverId 설정(Mapper에서 approverId의 유무를 가리기 위함)
        Map<String, Object> params = new HashMap<>();
        params.put("approverId", approverId);

        return sqlSession.selectList(NAMESPACE + ".getAllReports");
    }

    // 보고서 통계 조회
    public List<ReportStat> getReportStats(String startDate, String endDate) {
        Map<String, Object> params = new HashMap<>();
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        return sqlSession.selectList(NAMESPACE + ".getReportStats", params);
    }

    // 보고서 생성
    public void createReport(Report report) {
        sqlSession.insert(NAMESPACE + ".createReport", report);
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
