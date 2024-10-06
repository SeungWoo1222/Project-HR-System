package com.woosan.hr_system.report.dao;

import com.woosan.hr_system.report.model.Report;
import com.woosan.hr_system.report.model.ReportFileLink;
import com.woosan.hr_system.report.model.ReportStat;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

@Slf4j
@Repository
public class ReportDAO {
    @Autowired
    private SqlSession sqlSession;
    private static final String NAMESPACE = "com.woosan.hr_system.report.dao.ReportDAO.";

//=====================================================생성 메소드======================================================
    // 보고서 생성
    public int createReport(Map<String, Object> params) {

        sqlSession.insert(NAMESPACE + "createReport", params);
        BigInteger reportIdBigInt = (BigInteger) params.get("reportId");
        return reportIdBigInt.intValue();
    }

//=====================================================생성 메소드======================================================
//=====================================================조회 메소드======================================================

    // 모든 보고서 조회
    public List<Report> getAllReports(String employeeId) {
        return sqlSession.selectList(NAMESPACE + "getAllReports", employeeId);
    }


    // 보고서 세부 조회
    public Report getReportById(int reportId) {
        return sqlSession.selectOne(NAMESPACE + "getReportById", reportId);
    }


    // 보고서 통계 조회
    public List<ReportStat> getReportStats(LocalDate statisticStart, LocalDate statisticEnd, List<String> writerIdList) {
        Map<String, Object> params = new HashMap<>();
        params.put("statisticStart", statisticStart);
        params.put("statisticEnd", statisticEnd);
        if (writerIdList != null && !writerIdList.isEmpty()) {
            params.put("writerIds", writerIdList);
        } else {
            params.put("writerIds", null);  // writerIds가 null이면 임원 전체 선택
        }
        return sqlSession.selectList(NAMESPACE + "getReportStats", params);
    }


    // 최근 보고서 5개 조회
    public List<Report> getRecentReports(String writerId) {
        return sqlSession.selectList(NAMESPACE + "getRecentReports", writerId);
    }

    // 결재 미처리 보고서 조회(MANAGER)
    public List<Report> getUnprocessedReports(String approverId) {
        return sqlSession.selectList(NAMESPACE + "getUnprocessedReports", approverId);
    }


    // 내가 쓴 보고서 검색
    public List<Report> search(String keyword, int pageSize, int offset, String writerId, Integer searchType, String approvalStatus, LocalDate startDate, LocalDate endDate) {
        log.info("DAO searchType: {}", searchType);
        log.info("DAO keyword: {}", keyword);

        HashMap<String, Object> params = new HashMap<>();
        params.put("keyword", keyword);
        params.put("pageSize", pageSize);
        params.put("offset", offset);
        params.put("writerId", writerId);
        params.put("searchType", searchType);
        params.put("approvalStatus", approvalStatus);
        params.put("startDate", startDate);
        params.put("endDate", endDate);

        return sqlSession.selectList(NAMESPACE + "search", params);
    }

    // 내가 쓴 보고서 검색
    public int count(String keyword, String writerId, Integer searchType, String approvalStatus, LocalDate startDate, LocalDate endDate) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("keyword", keyword);
        params.put("searchType", searchType);
        params.put("approvalStatus", approvalStatus);
        params.put("writerId", writerId);
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        return sqlSession.selectOne(NAMESPACE + "count", params);
    }

    // 결재할 보고서 검색
    public List<Report> toApproveSearch(String keyword, int pageSize, int offset, String approverId, Integer searchType, String approvalStatus, LocalDate startDate, LocalDate endDate) {
        log.info("DAO keyword : {}", keyword);
        log.info("DAO searchType : {}", searchType);
        HashMap<String, Object> params = new HashMap<>();
        params.put("keyword", keyword);
        params.put("pageSize", pageSize);
        params.put("offset", offset);
        params.put("approverId", approverId);
        params.put("searchType", searchType);
        params.put("approvalStatus", approvalStatus);
        params.put("startDate", startDate);
        params.put("endDate", endDate);

        return sqlSession.selectList(NAMESPACE + "toApproveSearch", params);
    }

    // 결재할 보고서 검색
    public int toApproveCount(String keyword, String approverId, Integer searchType, String approvalStatus, LocalDate startDate, LocalDate endDate) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("keyword", keyword);
        params.put("approverId", approverId);
        params.put("searchType", searchType);
        params.put("approvalStatus", approvalStatus);
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        return sqlSession.selectOne(NAMESPACE + "toApproveCount", params);
    }

//=====================================================조회 메소드======================================================
//=====================================================수정 메소드======================================================
    // 보고서 수정
    public void updateReport(Report report) {
        sqlSession.update(NAMESPACE + "updateReport", report);
    }
    // 보고서 결재 처리
    public void updateApprovalStatus(Report report) {
        sqlSession.update(NAMESPACE + "updateApprovalStatus", report);
    }
//=====================================================수정 메소드======================================================
//=====================================================삭제 메소드======================================================
    // 보고서 삭제
    public void deleteReport(int reportId) {
        sqlSession.delete(NAMESPACE + "deleteReport", reportId);
    }
    // shared_trash(휴지통)에 삭제 데이터들 삽입
    public void insertReportIntoSharedTrash(int reportId) {
        sqlSession.insert(NAMESPACE + "insertReportIntoSharedTrash", reportId);
    }
//=====================================================삭제 메소드======================================================



}
