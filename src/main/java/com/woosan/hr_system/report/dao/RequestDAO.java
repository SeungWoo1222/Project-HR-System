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

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class RequestDAO {
    @Autowired
    private SqlSession sqlSession;
    private static final String NAMESPACE = "com.woosan.hr_system.report.dao.RequestDAO";

    // 요청 생성
    public void createRequest(Map<String, Object> params) {
        sqlSession.insert(NAMESPACE + ".createRequest", params);
    }

    // 요청 전체 조회
    public List<Request> getAllRequests() {
        return sqlSession.selectList(NAMESPACE + ".getAllRequests");
    }

    // 특정 요청 조회
    public Request getRequestById(Long requestId) {
        return sqlSession.selectOne(NAMESPACE + ".getRequestById", requestId);
    }

    // 내가 작성한 요청 조회
    public List<Request> getMyRequests(String employeeId, YearMonth startYearMonth, YearMonth endYearMonth) {
        // Map 설정 (Mapper에서 각 요소의 유무를 빠르게 파악하고 가독성, 재사용성을 위해)
        Map<String, Object> params = new HashMap<>();
        params.put("requesterId", employeeId);
        params.put("startYearMonth", startYearMonth);
        params.put("endYearMonth", endYearMonth);
        return sqlSession.selectList(NAMESPACE + ".getMyRequests", params);
    }

    // 내게 온 요청 조회
    public List<Request> getMyPendingRequests(String employeeId, YearMonth startYearMonth, YearMonth endYearMonth) {
        // Map 설정 (Mapper에서 각 요소의 유무를 빠르게 파악하고 가독성, 재사용성을 위해)
        Map<String, Object> params = new HashMap<>();
        params.put("writerId", employeeId);
        params.put("startYearMonth", startYearMonth);
        params.put("endYearMonth", endYearMonth);
        return sqlSession.selectList(NAMESPACE + ".getMyRequests", params);
    }

    // 요청 수정
    public void updateRequest(Map<String, Object> params) {
        sqlSession.update(NAMESPACE + ".updateRequest", params);
    }

    // 보고서 결재 처리
    public void updateApprovalStatus(Report report) {
        sqlSession.update(NAMESPACE + ".updateApprovalStatus", report);
    }

    // 요청 삭제
    public void deleteRequest(Long requestId) {
        sqlSession.delete(NAMESPACE + ".deleteRequest", requestId);
    }

    // shared_trash(휴지통)에 삭제 데이터들 삽입
    public void insertRequestIntoSharedTrash(Long requestId) {
        sqlSession.insert(NAMESPACE + ".insertRequestIntoSharedTrash", requestId);
    }

}
