package com.woosan.hr_system.report.dao;

import com.woosan.hr_system.employee.model.Employee;
import com.woosan.hr_system.report.model.FileMetadata;
import com.woosan.hr_system.report.model.Report;
import com.woosan.hr_system.report.model.ReportStat;
import com.woosan.hr_system.report.model.Request;
import com.woosan.hr_system.search.PageResult;
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

//=====================================================생성 메소드======================================================
    // 보고서 생성
//    public void createReport(Map<String, Object> params, MultipartFile file) {
    public void createReport(Map<String, Object> params) {
        sqlSession.insert(NAMESPACE + ".createReport", params);
    }

    // 파일 생성
    public void insertFile(Map<String, Object> fileParams) {
        sqlSession.insert(NAMESPACE + ".insertFile", fileParams);
    }

//=====================================================생성 메소드======================================================
//=====================================================조회 메소드======================================================

    // 모든 보고서 조회
    public List<Report> getAllReports(String employeeId, YearMonth startYearMonth, YearMonth endYearMonth) {
        Map<String, Object> params = new HashMap<>();
        params.put("writerId", employeeId);
        params.put("startYearMonth", startYearMonth);
        params.put("endYearMonth", endYearMonth);
        return sqlSession.selectList(NAMESPACE + ".getAllReports", params);
    }


    // 보고서 세부 조회
    public Report getReportById(Long reportId) {
        return sqlSession.selectOne(NAMESPACE + ".getReportById", reportId);
    }


    // 보고서 통계 조회
    public List<ReportStat> getReportStats(YearMonth startYearMonth, YearMonth endYearMonth, List<String> writerIdList) {
        Map<String, Object> params = new HashMap<>();
        params.put("startYearMonth", startYearMonth);
        params.put("endYearMonth", endYearMonth);
        if (writerIdList != null && !writerIdList.isEmpty()) {
            params.put("writerIds", writerIdList);
        } else {
            params.put("writerIds", null);  // writerIds가 null이면 임원 전체 선택
        }
        return sqlSession.selectList(NAMESPACE + ".getReportStats", params);
    }


    // 최근 보고서 5개 조회
    public List<Report> getRecentReports(String writerId) {
        return sqlSession.selectList(NAMESPACE + ".getRecentReports", writerId);
    }

    // 검색과 페이징 로직
    public List<Report> search(String keyword, int pageSize, int offset, String writerId, int searchType, String reportStart, String reportEnd) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("keyword", keyword);
        params.put("pageSize", pageSize);
        params.put("offset", offset);
        params.put("writerId", writerId);
        params.put("searchType", searchType);
        params.put("reportStart", reportStart);
        params.put("reportEnd", reportEnd);

        return sqlSession.selectList(NAMESPACE + ".search", params);
    }

    // 검색어에 해당하는 전체 데이터의 개수 세는 로직
    public int count(String keyword, String writerId, int searchType, String reportStart, String reportEnd) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("keyword", keyword);
        params.put("searchType", searchType);
        params.put("writerId", writerId);
        params.put("reportStart", reportStart);
        params.put("reportEnd", reportEnd);
        return sqlSession.selectOne(NAMESPACE + ".count", params);
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
//=====================================================조회 메소드======================================================
//=====================================================수정 메소드======================================================

    // 보고서 수정
    public void updateReport(Map<String, Object> params) {
        sqlSession.update(NAMESPACE + ".updateReport", params);
    }


//=====================================================수정 메소드======================================================
//=====================================================삭제 메소드======================================================

    // 보고서 삭제
    public void deleteReport(Long reportId) {
        sqlSession.delete(NAMESPACE + ".deleteReport", reportId);
    }

    // shared_trash(휴지통)에 삭제 데이터들 삽입
    public void insertReportIntoSharedTrash(Long reportId) {
        sqlSession.insert(NAMESPACE + ".insertReportIntoSharedTrash", reportId);
    }

//=====================================================삭제 메소드======================================================



}
