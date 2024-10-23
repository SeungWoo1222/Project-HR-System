package com.woosan.hr_system.report.service;

import com.woosan.hr_system.report.model.Request;
import com.woosan.hr_system.search.PageRequest;
import com.woosan.hr_system.search.PageResult;

import java.util.List;

public interface RequestService {
//=====================================================생성 메소드======================================================
    // 요청 생성
    void createRequest(Request request);
//=====================================================생성 메소드======================================================
//=====================================================조회 메소드======================================================
    // 모든 요청 조회
    //    List<Request> getAllRequests();
    // 특정 요청 조회
    Request getRequestById(int requestId);
    // 내가 쓴 요청 리스트 조회
    List<Request> getMyRequests(String requesterId);
    // reportId로 요청 조회
    int getRequestByReportId(int reportId);
    // 페이지, 서칭 + 보고서 리스트 (MANAGER)
    PageResult<Request> searchMyRequests(PageRequest pageRequest, String requesterId, Integer searchType, String startDate, String endDate);
    // 나에게 온 최근 5개 요청 조회 (STAFF)
    List<Request> getMyPendingRequests(String writerId);
    // 페이지, 서칭 + 보고서 리스트 (STAFF)
    PageResult<Request> searchRequests(PageRequest pageRequest, String writerId, Integer searchType,
                                       String startDate, String endDate);
    Request getRequestByWriter(int requestId);
//=====================================================조회 메소드======================================================
//=====================================================수정 메소드======================================================
    // 요청 수정
    void updateRequest(Request request);
    // 요청에 의한 보고서 생성 후 요청에 reportId 삽입
    void updateReportId(int requestId, int reportId);
//=====================================================수정 메소드======================================================
//=====================================================삭제 메소드======================================================
    // 요청 삭제
    void deleteRequest(int requestId);
    // 요청에 의한 보고서 삭제 시 reportId삭제
    void deleteReportId(Integer reportId);
//=====================================================삭제 메소드======================================================
}
