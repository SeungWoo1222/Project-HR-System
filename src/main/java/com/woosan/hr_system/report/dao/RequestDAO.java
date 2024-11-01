package com.woosan.hr_system.report.dao;

import com.woosan.hr_system.report.model.Request;
import java.util.List;
import java.util.Map;

public interface RequestDAO {
    int createRequest(Map<String, Object> params);
    List<Request> getAllRequests();
    Request getRequestById(int requestId);
    List<Request> getMyRequests(String requesterId);
    int getRequestByReportId(int reportId);
    List<Request> getMyPendingRequests(String writerId);
    List<Request> search(String keyword, int pageSize, int offset, String writerId, Integer searchType, String startDate, String endDate);
    int count(String keyword, String writerId, Integer searchType, String startDate, String endDate);
    List<Request> searchMyRequests(String keyword, int pageSize, int offset, String requesterId, Integer searchType, String startDate, String endDate);
    int countMyRequests(String keyword, String requesterId, Integer searchType, String startDate, String endDate);
    void updateRequest(Map<String, Object> params);
    void updateReportId(Map<String, Object> params);
    void deleteRequest(int requestId);
    void insertRequestIntoSharedTrash(int requestId);
    void deleteReportId(Integer reportId);
}
