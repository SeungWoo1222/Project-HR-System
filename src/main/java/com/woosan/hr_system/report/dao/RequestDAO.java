package com.woosan.hr_system.report.dao;

import com.woosan.hr_system.employee.model.Employee;
import com.woosan.hr_system.report.model.Report;
import com.woosan.hr_system.report.model.Request;

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
    public void createRequest(List<Request> requests) {
        sqlSession.insert(NAMESPACE + ".createRequest", requests);
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
    public List<Request> getMyRequests(String requesterId, YearMonth startYearMonth, YearMonth endYearMonth) {
        // Map 설정 (Mapper에서 각 요소의 유무를 빠르게 파악하고 가독성, 재사용성을 위해)
        Map<String, Object> params = new HashMap<>();
        params.put("requesterId", requesterId);
        params.put("startYearMonth", startYearMonth);
        params.put("endYearMonth", endYearMonth);
        return sqlSession.selectList(NAMESPACE + ".getAllRequests", params);
    }

    // 모든 사원 조회
    public List<Employee> getAllEmployees() {
        return sqlSession.selectList(NAMESPACE + ".getAllEmployees");
    }

    // 요청 수정
    public void updateRequest(Request request) {
        sqlSession.update(NAMESPACE + ".updateRequest", request);
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










//    // 작성 요청 생성
//    public void createReportRequest(Request request) {
//        sqlSession.insert(NAMESPACE + ".createReportRequest", request);
//    }
//    // 보고서 기반 작성 요청 조회
//    public Request getReportRequestById(int requestId) {
//        return sqlSession.selectOne(NAMESPACE + ".getReportRequestById", requestId);
//    }
//    // 작성 요청 수정
//    public void updateReportRequest(Request request) {
//        sqlSession.update(NAMESPACE + ".updateReportRequest", request);
//    }
//    // 특정 작성 요청 삭제
//    public void deleteReportRequest(int requestId) {
//        sqlSession.delete(NAMESPACE + ".deleteReportRequest", requestId);
//    }
//
//    // 사원 기반 보고서 조회
//    public List<Request> getReportRequestsByEmployeeId(String employeeId) {
//        return sqlSession.selectList(NAMESPACE + ".getReportRequestsByEmployeeId", employeeId);
//    }


}
