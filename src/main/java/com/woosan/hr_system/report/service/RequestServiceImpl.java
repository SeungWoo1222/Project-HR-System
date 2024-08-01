package com.woosan.hr_system.report.service;

import com.woosan.hr_system.auth.service.AuthService;
import com.woosan.hr_system.report.dao.RequestDAO;
import com.woosan.hr_system.report.model.Report;
import com.woosan.hr_system.report.model.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class RequestServiceImpl implements RequestService {

    @Autowired
    private RequestDAO requestDAO;
    @Autowired
    private AuthService authService;

    @Override // 요청 생성
    public void createRequest(List<String> writerIds, List<String> writerNames, LocalDate dueDate, String requestNote, String requesterId) {

        LocalDateTime requestDate = LocalDateTime.now(); // 현재 기준 생성 시간 설정
        List<Request> requests = new ArrayList<>();

        // requests 객체 설정
        for (int i = 0; i < writerIds.size(); i++) {
            Request request = new Request();
            request.setRequesterId(requesterId);
            request.setWriterId(writerIds.get(i));
            request.setWriterName(writerNames.get(i));
            request.setDueDate(dueDate);
            request.setRequestNote(requestNote);
            request.setRequestDate(requestDate);
            requests.add(request);
        }

        requestDAO.createRequest(requests);
    }

    @Override // 모든 요청 조회
    public List<Request> getAllRequests() {
        return requestDAO.getAllRequests();
    }

    @Override  // 로그인한 계정 기준 요청 리스트 조회(내가 쓴 요청 리스트 조회)
    public List<Request> getMyRequests(String requesterId, String requestStart , String requestEnd) {
        // 입력된 날짜를 파싱하기 위한 DateTimeFormatter
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        YearMonth startYearMonth;
        YearMonth endYearMonth;

        // 현재 연월 가져오기
        YearMonth currentYearMonth = YearMonth.now();

        // startDate와 endDate가 null인지 확인하고 현재 연월로 설정
        if (requestStart == null) {
            startYearMonth = currentYearMonth;
        } else {
            startYearMonth = YearMonth.parse(requestStart, formatter);
        }

        if (requestEnd == null) {
            endYearMonth = currentYearMonth;
        } else {
            endYearMonth = YearMonth.parse(requestEnd, formatter);
        }
        return requestDAO.getMyRequests(requesterId, startYearMonth, endYearMonth);
    }

    @Override // 특정 요청 조회
    public Request getRequestById(Long requestId) {
        return requestDAO.getRequestById(requestId);
    }


    @Override // 보고서 결재 처리
    public void updateApprovalStatus(Long reportId, String status, String rejectionReason) {
        // report 객체 설정
        Report report = new Report();
        report.setReportId(reportId);
        report.setStatus(status);
        report.setRejectReason(rejectionReason);

        requestDAO.updateApprovalStatus(report);
    }

    @Override // 요청 수정
    public void updateRequest(Long requestId, List<String> writerIds, List<String> writerNames, String requestNote, LocalDate dueDate) {

        //request 객체 설정
        LocalDateTime modifiedDate = LocalDateTime.now(); //현재 기준 수정 시간 설정
        List<Request> requests = new ArrayList<>();

        // 작성자가 한명인 경우 => 요청을 수정
        if (writerIds.size() == 1) {
            Request request = new Request();
            request.setRequestId(requestId);
            request.setWriterId(writerIds.get(0));
            request.setRequestNote(requestNote);
            request.setDueDate(dueDate);
            request.setModifiedDate(modifiedDate);
            requestDAO.updateRequest(request);
        }

        // 작성자가 여러명인 경우 => 요청 삭제 후 새로운 요청 생성
        else if (writerIds.size() > 1) {
            requestDAO.deleteRequest(requestId);
            String requesterId = null;
            requesterId = authService.getAuthenticatedUser().getUsername();

            for (int i = 0; i < writerIds.size(); i++) {
                Request request = new Request();
                request.setRequesterId(requesterId);
                request.setWriterId(writerIds.get(i));
                request.setWriterName(writerNames.get(i));
                request.setRequestNote(requestNote);
                request.setDueDate(dueDate);
                request.setRequestDate(modifiedDate);
                requests.add(request);
            }
            requestDAO.createRequest(requests);
        }
    }

    @Override // 요청 삭제
    public void deleteRequest(Long requestId) {
        requestDAO.deleteRequest(requestId);

        // shared_trash 테이블에 삭제될 데이터들 삽입
        requestDAO.insertRequestIntoSharedTrash(requestId);
    }

}













