package com.woosan.hr_system.report.dao;

import com.woosan.hr_system.report.model.FileMetadata;
import com.woosan.hr_system.report.model.Report;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Repository
public class ReportDAO {
    @Autowired
    private SqlSession sqlSession;
    private static final String NAMESPACE = "com.woosan.hr_system.report.dao.ReportDAO";

    public List<Report> getAllReports() {
        return sqlSession.selectList(NAMESPACE + ".getAllReports");
    }

    public Report getReportById(Long reportId) {
        return sqlSession.selectOne(NAMESPACE + ".getReportById", reportId);
    }

    public FileMetadata getReportFileById(Long fileId) {
        return sqlSession.selectOne(NAMESPACE + ".getReportFileById", fileId);
    }

    public void insertReport(Report report) {
        sqlSession.insert(NAMESPACE + ".insertReport", report);
    }

    public void uploadFiles(Long reportId, MultipartFile[] files) throws IOException {
        for (MultipartFile file : files) {
            // 파일 저장 로직 구현
            // 파일 메타데이터 저장
            FileMetadata metadata = new FileMetadata();
            metadata.setReportId(reportId);
            metadata.setOriginalFilename(file.getOriginalFilename());
            metadata.setUuidFilename(UUID.randomUUID().toString());
            metadata.setSize(file.getSize());
            metadata.setUploadDate(new java.sql.Timestamp(System.currentTimeMillis()));

            // 메타데이터 DB에 저장
            sqlSession.insert(NAMESPACE + ".insertFileMetadata", metadata);
        }
    }

    public void updateReport(Report report) {
        sqlSession.update(NAMESPACE + ".updateReport", report);
    }

    public void deleteReport(Long reportId) {
        sqlSession.delete(NAMESPACE + ".deleteReport", reportId);
    }

    public void insertFileMetadata(FileMetadata fileMetadata) {
        sqlSession.insert(NAMESPACE + ".insertFileMetadata", fileMetadata);
    }
}
