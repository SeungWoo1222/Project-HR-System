package com.woosan.hr_system.report.dao;

import com.woosan.hr_system.report.model.Request;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class RequestDAO {
    @Autowired
    private SqlSession sqlSession;
    private static final String NAMESPACE = "com.woosan.hr_system.report.dao.RequestDAO.";
//===================================================생성 메소드=======================================================
    // 요청 생성
    public int createRequest(Map<String, Object> params) {
        log.debug(params.toString());
        return sqlSession.insert(NAMESPACE + "createRequest", params);
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
        return sqlSession.selectList(NAMESPACE + "getRecentRequests", writerId);
    }

    // 내게 온 요청 검색(STAFF)
    public List<Request> search(String keyword, int pageSize, int offset, String writerId,  Integer searchType, String startDate, String endDate) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("keyword", keyword);
        params.put("pageSize", pageSize);
        params.put("offset", offset);
        params.put("writerId", writerId);
        params.put("searchType", searchType);
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        return sqlSession.selectList(NAMESPACE + "search", params);
    }

    // 내게 온 요청 검색(STAFF)
    public int count(String keyword, String writerId, Integer searchType, String startDate, String endDate) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("keyword", keyword);
        params.put("searchType", searchType);
        params.put("writerId", writerId);
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        return sqlSession.selectOne(NAMESPACE + "count", params);
    }

    // 내가 작성한 요청 검색
    public List<Request> searchMyRequests(String keyword, int pageSize, int offset, String requesterId,  Integer searchType, String startDate, String endDate) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("keyword", keyword);
        params.put("pageSize", pageSize);
        params.put("offset", offset);
        params.put("requesterId", requesterId);
        params.put("searchType", searchType);
        params.put("startDate", startDate);
        params.put("endDate", endDate);

        return sqlSession.selectList(NAMESPACE + "searchMyRequests", params);
    }

    // 내가 작성한 요청 검색
    public int countMyRequests(String keyword, String requesterId, Integer searchType, String startDate, String endDate) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("keyword", keyword);
        params.put("searchType", searchType);
        params.put("requesterId", requesterId);
        params.put("startDate", startDate);
        params.put("endDate", endDate);

        return sqlSession.selectOne(NAMESPACE + "countMyRequests", params);
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
