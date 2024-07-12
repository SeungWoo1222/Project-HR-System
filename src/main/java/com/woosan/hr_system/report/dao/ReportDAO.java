package com.woosan.hr_system.report.dao;

import com.woosan.hr_system.report.model.Report;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.woosan.hr_system.report.model.FileMetadata;

import java.util.List;

@Repository
public class ReportDAO {
    @Autowired
    private SqlSession sqlSession;
    private static final String NAMESPACE = "com.woosan.hr_system.report.dao.ReportDAO";

    // 모든 보고서 조회
    public List<Report> getAllReports() {
        return sqlSession.selectList(NAMESPACE + ".getAllReports");
    }
    // 특정 보고서 조회
    public Report getReportById(int reportId) {
        return sqlSession.selectOne(NAMESPACE + ".getReportById", reportId);
    }
    // 보고서 작성
    public void insertReport(Report report) {
        sqlSession.insert(NAMESPACE + ".insertReport", report);
    }
    // 보고서 수정
    public void updateReport(Report report) {
        sqlSession.update(NAMESPACE + ".updateReport", report);
    }
    // 보고서 삭제
    public void deleteReport(int reportId) {
        sqlSession.delete(NAMESPACE + ".deleteReport", reportId);
    }
    // 보고서 파일 첨부
    public void insertFileMetadata(FileMetadata fileMetadata) {
        sqlSession.insert(NAMESPACE + ".insertFileMetadata", fileMetadata);
    }
}
