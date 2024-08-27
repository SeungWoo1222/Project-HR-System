package com.woosan.hr_system.report.dao;

import com.woosan.hr_system.employee.model.Employee;
import com.woosan.hr_system.report.model.Report;
import com.woosan.hr_system.report.model.Request;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class RequestDAO {
    @Autowired
    private SqlSession sqlSession;
    private static final String NAMESPACE = "com.woosan.hr_system.report.dao.RequestDAO.";
//===================================================생성 메소드=======================================================
    // 요청 생성
    public void createRequest(Map<String, Object> params) {
        sqlSession.insert(NAMESPACE + "createRequest", params);
    }

//===================================================생성 메소드=======================================================
// ==================================================조회 메소드=======================================================

    // 요청 전체 조회
    public List<Request> getAllRequests() {
        return sqlSession.selectList(NAMESPACE + "getAllRequests");
    }

    // 요청 세부 조회
    public Request getRequestById(int requestId) {
        return sqlSession.selectOne(NAMESPACE + "getRequestById", requestId);
    }

    // 내가 작성한 요청 조회
    public List<Request> getMyRequests(String requesterId) {
        return sqlSession.selectList(NAMESPACE + "getMyRequests", requesterId);
    }

    // reportId로 요청 조회
    public int getRequestByReportId(int reportId) {
        return sqlSession.selectOne(NAMESPACE + "getRequestByReportId", reportId);
    }

    // 내게 온 요청 조회
    public List<Request> getMyPendingRequests(String writerId) {
        log.info("DAO writerId {}", writerId);
        return sqlSession.selectList(NAMESPACE + "getRecentRequests", writerId);
    }

    // 검색과 페이징 로직
    public List<Request> search(String keyword, int pageSize, int offset, String writerId,  int searchType, String requestStart, String requestEnd) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("keyword", keyword);
        params.put("pageSize", pageSize);
        params.put("offset", offset);
        params.put("writerId", writerId);
        params.put("searchType", searchType);
        params.put("requestStart", requestStart);
        params.put("requestEnd", requestEnd);

        return sqlSession.selectList(NAMESPACE + "search", params);
    }

    // 검색어에 해당하는 전체 데이터의 개수 세는 로직
    public int count(String keyword, String writerId, int searchType, String requestStart, String requestEnd) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("keyword", keyword);
        params.put("searchType", searchType);
        params.put("writerId", writerId);
        params.put("requestStart", requestStart);
        params.put("requestEnd", requestEnd);
        return sqlSession.selectOne(NAMESPACE + "count", params);
    }

    // 검색과 페이징 로직
    public List<Request> searchMyRequests(String keyword, int pageSize, int offset, String requesterId,  int searchType, String requestStart, String requestEnd) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("keyword", keyword);
        params.put("pageSize", pageSize);
        params.put("offset", offset);
        params.put("requesterId", requesterId);
        params.put("searchType", searchType);
        params.put("requestStart", requestStart);
        params.put("requestEnd", requestEnd);

        return sqlSession.selectList(NAMESPACE + "searchMyRequests", params);
    }

    // 검색어에 해당하는 전체 데이터의 개수 세는 로직
    public int countMyRequests(String keyword, String requesterId, int searchType, String requestStart, String requestEnd) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("keyword", keyword);
        params.put("searchType", searchType);
        params.put("requesterId", requesterId);
        params.put("requestStart", requestStart);
        params.put("requestEnd", requestEnd);
        return sqlSession.selectOne(NAMESPACE + "countMyRequests", params);
    }

    // 보고서 결재 처리
    public void updateApprovalStatus(Report report) {
        sqlSession.update(NAMESPACE + "updateApprovalStatus", report);
    }

// ==================================================조회 메소드=======================================================

// ==================================================수정 메소드=======================================================

    // 요청 수정
    public void updateRequest(Map<String, Object> params) {
        sqlSession.update(NAMESPACE + "updateRequest", params);
    }

    // 요청에 의한 보고서 생성 후 요청에 reportId 삽입
    public void updateReportId(Map<String, Object> params) {
        sqlSession.update(NAMESPACE + "updateReportId", params);
    }



// ==================================================수정 메소드=======================================================

// ==================================================삭제 메소드=======================================================

    // 요청 삭제
    public void deleteRequest(int requestId) {
        sqlSession.delete(NAMESPACE + "deleteRequest", requestId);
    }

    // shared_trash(휴지통)에 삭제 데이터들 삽입
    public void insertRequestIntoSharedTrash(int requestId) {
        sqlSession.insert(NAMESPACE + "insertRequestIntoSharedTrash", requestId);
    }

    // 요청에의한 보고서 삭제시 reportId 삭제
    public void deleteReportId(Integer reportId) {
        sqlSession.delete(NAMESPACE + "deleteReportId", reportId);
    }


// ==================================================삭제 메소드=======================================================
}
