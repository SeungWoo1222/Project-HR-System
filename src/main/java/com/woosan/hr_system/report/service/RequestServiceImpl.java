package com.woosan.hr_system.report.service;

import com.woosan.hr_system.auth.service.AuthService;
import com.woosan.hr_system.report.dao.RequestDAO;
import com.woosan.hr_system.report.model.Report;
import com.woosan.hr_system.report.model.Request;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;



@Service
public class RequestServiceImpl implements RequestService {

    @Autowired
    private RequestDAO requestDAO;
    @Autowired
    private AuthService authService;

    @Override // 요청 생성
    public void createRequest(Request request) {
        LocalDateTime requestDate = LocalDateTime.now();

        for (int i = 0; i < request.getWriterIdList().size(); i++) {
            Map<String, Object> params = new HashMap<>();
            params.put("requesterId", request.getRequesterId());
            params.put("writerId", request.getWriterIdList().get(i));
            params.put("writerName", request.getWriterNameList().get(i));
            params.put("dueDate", request.getDueDate());
            params.put("requestNote", request.getRequestNote());
            params.put("requestDate", requestDate);

            requestDAO.createRequest(params);
        }

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
    public void updateRequest(Request request) {
        System.out.println("서비스 : " + request.getRequestNote());
        //request 객체 설정
        LocalDateTime modifiedDate = LocalDateTime.now(); //현재 기준 수정 시간 설정

        for (int i = 0; i < request.getWriterIdList().size(); i++) {
            Map<String, Object> params = new HashMap<>();
            params.put("requestId", request.getRequestId());
            params.put("writerId", request.getWriterIdList().get(i));
            params.put("writerName", request.getWriterNameList().get(i));
            params.put("dueDate", request.getDueDate());
            params.put("requestNote", request.getRequestNote());
            params.put("modifiedDate", modifiedDate);

            System.out.println("Params: " + params);

            requestDAO.updateRequest(params);
        }
    }

    @Override // 요청 삭제
    public void deleteRequest(Long requestId) {
        requestDAO.deleteRequest(requestId);

        // shared_trash 테이블에 삭제될 데이터들 삽입
        requestDAO.insertRequestIntoSharedTrash(requestId);
    }

}
