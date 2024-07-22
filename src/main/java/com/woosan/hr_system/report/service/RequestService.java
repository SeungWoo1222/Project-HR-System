package com.woosan.hr_system.report.service;

import com.woosan.hr_system.report.model.Request;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface RequestService {
    List<Request> getAllRequests();
    Request getRequestById(Long requestId);
    void updateRequest(Request request);
    void deleteRequest(Long requestId);
    void createRequest(Request request);

}
