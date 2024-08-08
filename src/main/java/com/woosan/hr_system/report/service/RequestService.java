package com.woosan.hr_system.report.service;

import com.woosan.hr_system.employee.model.Employee;
import com.woosan.hr_system.report.model.Report;
import com.woosan.hr_system.report.model.Request;
import com.woosan.hr_system.search.PageRequest;
import com.woosan.hr_system.search.PageResult;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface RequestService {
    // 요청 생성
    void createRequest(Request request);
    // 모든 요청 조회
//    List<Request> getAllRequests();
    // 특정 요청 조회
    Request getRequestById(Long requestId);
    // 내가 쓴 요청 리스트 조회 (manager)
    List<Request> getMyRequests(String employeeId, String requestStart , String requestEnd);
    // 보고서 결재 처리
    void updateApprovalStatus(Long reportId, String status, String rejectionReasont);
    // 요청 수정
    void updateRequest(Request request);
    // 요청 삭제
    void deleteRequest(Long requestId);
    // 나에게 온 요청 조회 (STAFF)
    List<Request> getMyPendingRequests(String writerId);
    // 페이지, 서칭 + 보고서 리스트 (STAFF)
    PageResult<Request> searchRequests(PageRequest pageRequest, String writerId, int searchType, String requestStart, String requestEnd);
}
