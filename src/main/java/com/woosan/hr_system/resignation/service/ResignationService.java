package com.woosan.hr_system.resignation.service;

import com.woosan.hr_system.resignation.model.Resignation;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ResignationService {
    List<Resignation> getAllResignation();
    Resignation getResignation(String employeeId);
    void resignEmployee(String employeeId, Resignation resignation);
    void updateResignationInfo(String employeeId, Resignation resignation);
    void deleteResignation(String employeeId);
    void uploadResignationDocuments(Resignation resignation, MultipartFile[] resignationDocuments);
}
