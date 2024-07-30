package com.woosan.hr_system.report.service;

import com.woosan.hr_system.employee.model.Employee;
import com.woosan.hr_system.report.model.Report;
import com.woosan.hr_system.report.model.Request;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface RequestService {
    // 요청 생성
    void createRequest(List<String> writerIds, List<String> writerNames, LocalDate dueDate, String requestNote, String requesterId);

    // 모든 요청 조회
    List<Request> getAllRequests();
    // 특정 요청 조회
    Request getRequestById(Long requestId);
    // 로그인한 계정 기준 요청 리스트 조회(내가 쓴 요청 리스트 조회)
    List<Request> getMyRequests(String requesterId, String requestStart , String requestEnd);

    // 보고서 결재 처리
    void updateApprovalStatus(Long reportId, String status, String rejectionReasont);
    // 요청 수정
    void updateRequest(Long requestId, List<String> writerIds, List<String> writerNames, String requestNote, LocalDate dueDate);

    // 요청 삭제
    void deleteRequest(Long requestId);

}
