package com.woosan.hr_system.report.dao;

import com.woosan.hr_system.report.model.Report;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ReportDAOImpl implements ReportDAO {

    @Autowired
    private SqlSession sqlSession;

    private static final String NAMESPACE = "com.woosan.hr_system.report.dao.ReportDAO";

    @Override
    public List<Report> getAllReports() {
        return sqlSession.selectList(NAMESPACE + ".getAllReports");
    }

    @Override
    public Report getReportById(int reportId) {
        return sqlSession.selectOne(NAMESPACE + ".getReportById", reportId);
    }

    @Override
    public void insertReport(Report report) {
        sqlSession.insert(NAMESPACE + ".insertReport", report);
    }

    @Override
    public void updateReport(Report report) {
        sqlSession.update(NAMESPACE + ".updateReport", report);
    }

    @Override
    public void deleteReport(int reportId) {
        sqlSession.delete(NAMESPACE + ".deleteReport", reportId);
    }
}
