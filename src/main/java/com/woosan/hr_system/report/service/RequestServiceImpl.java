package com.woosan.hr_system.report.service;

import com.woosan.hr_system.report.dao.RequestDAO;
import com.woosan.hr_system.report.model.Report;
import com.woosan.hr_system.report.model.Request;
import com.woosan.hr_system.search.PageRequest;
import com.woosan.hr_system.search.PageResult;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class RequestServiceImpl implements RequestService {

    @Autowired
    private RequestDAO requestDAO;

//===================================================생성 메소드=======================================================

    @Override // 요청 생성
    public void createRequest(Request request) {
        LocalDateTime requestDate = LocalDateTime.now();

        for (int i = 0; i < request.getNameList().size(); i++) {
            Map<String, Object> params = new HashMap<>();
            params.put("requesterId", request.getRequesterId());
            params.put("writerId", request.getIdList().get(i));
            params.put("writerName", request.getNameList().get(i));
            params.put("dueDate", request.getDueDate());
            params.put("requestNote", request.getRequestNote());
            params.put("requestDate", requestDate);

            requestDAO.createRequest(params);
        }
    }

//===================================================생성 메소드=======================================================
//===================================================조회 메소드=======================================================

    @Override // 요청 세부 조회
    public Request getRequestById(int requestId) {
        return requestDAO.getRequestById(requestId);
    }

    @Override  // 내가 쓴 요청 리스트 조회
    public List<Request> getMyRequests(String requesterId) {
        return requestDAO.getMyRequests(requesterId);
    }

    @Override  // 내게 온 요청 리스트 조회
    public List<Request> getMyPendingRequests(String writerId) {
        return requestDAO.getMyPendingRequests(writerId);
    }

    @Override // reportId로 요청 조회
    public int getRequestByReportId(int reportId) {
        return requestDAO.getRequestByReportId(reportId);
    }

    @Override // 요청 검색
    public PageResult<Request> searchRequests(PageRequest pageRequest, String writerId, int searchType, String requestStart, String requestEnd) {

        // 설정된 보고서 날짜범위가 없다면 현재 달을 기준으로 보여줌
        if (requestStart == null || requestEnd == null) {
            LocalDate currentMonth = LocalDate.now();
            // 날짜 형태를 yyyy-mm으로 바꿔줌
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
            String formattedDate = currentMonth.format(formatter);
            requestStart = formattedDate;
            requestEnd = formattedDate;
        }

        int offset = pageRequest.getPage() * pageRequest.getSize();
        List<Request> requests = requestDAO.search(pageRequest.getKeyword(), pageRequest.getSize(), offset, writerId, searchType, requestStart, requestEnd);
        int total = requestDAO.count(pageRequest.getKeyword(), writerId, searchType, requestStart, requestEnd);

        return new PageResult<>(requests, (int) Math.ceil((double) total / pageRequest.getSize()), total, pageRequest.getPage());
    }

    @Override // 요청 검색
    public PageResult<Request> searchMyRequests(PageRequest pageRequest, String requesterId, int searchType, String requestStart, String requestEnd) {

        // 설정된 보고서 날짜범위가 없다면 현재 달을 기준으로 보여줌
        if (requestStart == null || requestEnd == null) {
            LocalDate currentMonth = LocalDate.now();
            // 날짜 형태를 yyyy-mm으로 바꿔줌
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
            String formattedDate = currentMonth.format(formatter);
            requestStart = formattedDate;
            requestEnd = formattedDate;
        }

        int offset = pageRequest.getPage() * pageRequest.getSize();
        List<Request> requests = requestDAO.searchMyRequests(pageRequest.getKeyword(), pageRequest.getSize(), offset, requesterId, searchType, requestStart, requestEnd);
        int total = requestDAO.countMyRequests(pageRequest.getKeyword(), requesterId, searchType, requestStart, requestEnd);

        return new PageResult<>(requests, (int) Math.ceil((double) total / pageRequest.getSize()), total, pageRequest.getPage());
    }



//===================================================조회 메소드=======================================================

//===================================================수정 메소드=======================================================

    @Override // 요청 수정
    public void updateRequest(Request request) {
        //request 객체 설정
        LocalDateTime modifiedDate = LocalDateTime.now(); //현재 기준 수정 시간 설정

        Map<String, Object> params = new HashMap<>();
        params.put("requestId", request.getRequestId());
        params.put("writerId", request.getIdList().get(0));
        params.put("writerName", request.getNameList().get(0));
        params.put("dueDate", request.getDueDate());
        params.put("requestNote", request.getRequestNote());
        params.put("modifiedDate", modifiedDate);

        requestDAO.updateRequest(params);
    }

    // 요청에 의한 보고서 생성 후 요청에 reportId 삽입
    @Override
    public void updateReportId(int requestId, int reportId) {
        Map<String, Object> params = new HashMap<>();
        params.put("requestId", requestId);
        params.put("reportId", reportId);

        requestDAO.updateReportId(params);
    }


    @Override // 보고서 결재 처리
    public void updateApprovalStatus(int reportId, String status, String rejectionReason) {
        // report 객체 설정
        Report report = new Report();
        report.setReportId(reportId);
        report.setStatus(status);
        report.setRejectReason(rejectionReason);

        requestDAO.updateApprovalStatus(report);
    }

//===================================================수정 메소드=======================================================

//===================================================삭제 메소드=======================================================

    @Override // 요청 삭제
    public void deleteRequest(int requestId) {
        // shared_trash 테이블에 삭제될 데이터들 삽입
        requestDAO.insertRequestIntoSharedTrash(requestId);
        requestDAO.deleteRequest(requestId);
    }

    @Override // 요청에 의한 보고서 삭제시 reportId 삭제
    public void deleteReportId(Integer reportId) {
        requestDAO.deleteReportId(reportId);
    }


//===================================================삭제 메소드=======================================================


}
