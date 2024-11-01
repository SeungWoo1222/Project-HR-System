package com.woosan.hr_system.report.service;

import com.woosan.hr_system.auth.model.UserSessionInfo;
import com.woosan.hr_system.auth.service.AuthService;
import com.woosan.hr_system.employee.service.EmployeeService;
import com.woosan.hr_system.notification.service.NotificationService;
import com.woosan.hr_system.report.dao.RequestDAO;
import com.woosan.hr_system.report.model.Request;
import com.woosan.hr_system.search.PageRequest;
import com.woosan.hr_system.search.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@Slf4j
public class RequestServiceImpl implements RequestService {
    @Autowired
    private RequestDAO requestDAO;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private AuthService authService;

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

            int requestId = requestDAO.createRequest(params);
            // 보고서 생성 후 결재자에게 알림 생성
            String requesterName = employeeService.getEmployeeNameById(request.getRequesterId());
            notificationService.createNotification(request.getIdList().get(i), "보고서 작성 요청이 있습니다. <br>요청자 : " + requesterName, "/report/writeFromRequest?requestId=" + requestId);
        }
    }

//===================================================생성 메소드=======================================================
//===================================================조회 메소드=======================================================

    @Override // 요청 세부 조회
    public Request getRequestById(int requestId) {
        Request request = checkRequestAuthorization(requestId);
        return request;
    }

    @Override  // 내가 쓴 요청 리스트 조회
    public List<Request> getMyRequests(String requesterId) {
        return requestDAO.getMyRequests(requesterId);
    }

    @Override  // 나에게 온 최근 5개 요청 조회(STAFF)
    public List<Request> getMyPendingRequests(String writerId) {
        return requestDAO.getMyPendingRequests(writerId);
    }

    @Override // reportId로 요청 조회
    public int getRequestByReportId(int reportId) {
        return requestDAO.getRequestByReportId(reportId);
    }

    @Override // 내게 온 요청 검색 (STAFF)
    public PageResult<Request> searchRequests(PageRequest pageRequest, String writerId, Integer searchType, String startDate, String endDate) {

        int offset = pageRequest.getPage() * pageRequest.getSize();
        List<Request> requests = requestDAO.search(pageRequest.getKeyword(), pageRequest.getSize(), offset, writerId, searchType, startDate, endDate);
        int total = requestDAO.count(pageRequest.getKeyword(), writerId, searchType, startDate, endDate);

        return new PageResult<>(requests, (int) Math.ceil((double) total / pageRequest.getSize()), total, pageRequest.getPage());
    }

    @Override // 내가 작성한 요청 검색
    public PageResult<Request> searchMyRequests(PageRequest pageRequest, String requesterId, Integer searchType, String startDate, String endDate) {

        // 보여줄 리스트의 범위를 지정
        int offset = pageRequest.getPage() * pageRequest.getSize();
        // 범위에 속하는 보고서를 검색함
        List<Request> requests = requestDAO.searchMyRequests(pageRequest.getKeyword(), pageRequest.getSize(), offset, requesterId, searchType, startDate, endDate);
        // 범위에 속하는 보고서 개수를 세서 페이징
        int total = requestDAO.countMyRequests(pageRequest.getKeyword(), requesterId, searchType, startDate, endDate);

        return new PageResult<>(requests, (int) Math.ceil((double) total / pageRequest.getSize()), total, pageRequest.getPage());
    }

    @Override // 요청 수신자의 요청 조회
    public Request getRequestByWriter(int requestId) {
        // 요청 조회
        Request request = requestDAO.getRequestById(requestId);

        // 로그인한 사원 아이디 조회
        String employeeId = authService.getAuthenticatedUser().getUsername();

        // 요청 수신자 ID와 로그인 사원의 ID 비교
        if (!request.getWriterId().equals(employeeId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "보고서 작성자가 아닙니다.");
        }

        return request;
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
//===================================================기타 메소드=======================================================
    // 요청에 대한 접근 권한, 요청이 있는지 확인
    public Request checkRequestAuthorization(int requestId) {
        UserSessionInfo userSessionInfo = new UserSessionInfo();
        String currentEmployeeId = userSessionInfo.getCurrentEmployeeId();
            Request request = requestDAO.getRequestById(requestId); // 요청 세부 정보 가져오기
            if (request == null) {
                throw new IllegalArgumentException("해당 요청이 존재하지 않습니다.\nReport ID : " + requestId);
            }

            // 작성자와 로그인한 사용자가 동일하지 않으면 권한 오류 발생
            if (!request.getRequesterId().equals(currentEmployeeId)) {
                throw new AccessDeniedException("접근 권한이 없습니다");
            }

            return request; // 요청 정보 반환
        }
//===================================================기타 메소드=======================================================


}
