package com.woosan.hr_system.resignation.service;

import com.woosan.hr_system.auth.model.UserSessionInfo;
import com.woosan.hr_system.common.service.CommonService;
import com.woosan.hr_system.exception.employee.ResignationNotFoundException;
import com.woosan.hr_system.resignation.dao.ResignationDAO;
import com.woosan.hr_system.resignation.model.Resignation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class ResignationServiceImpl implements ResignationService {

    @Autowired
    private CommonService commonService;
    @Autowired
    private ResignationDAO resignationDAO;

    @Transactional
    @Override // 모든 퇴사 정보 조회
    public List<Resignation> getAllResignation() {
        return resignationDAO.getAllResignedEmployees();
    }

    @Transactional
    @Override // id를 이용하여 특정 사원의 퇴사 정보 조회
    public Resignation getResignation(String employeeId) {
        return findResignationById(employeeId);
    }

    // 사원 퇴사 정보 조회 후 null 확인
    private Resignation findResignationById(String employeeId) {
        Resignation resignation = resignationDAO.getResignedEmployee(employeeId);
        if (resignation == null) throw new ResignationNotFoundException(employeeId);
        return resignation;
    }

    @Transactional
    @Override // 사원 퇴사 처리 로직
    public void resignEmployee(String employeeId, Resignation resignation) {
        // 퇴사 처리 할 resignation 객체 초기화
        UserSessionInfo processInfo = new UserSessionInfo();
        resignation.initializeResignationDetails(employeeId, resignation, processInfo.getCurrentEmployeeId(), processInfo.getNow());

        // 퇴사 정보 등록
        resignationDAO.insertResignation(resignation);
    }

    @Transactional
    @Override // 사원 퇴사 정보 수정
    public void updateResignationInfo(String employeeId, Resignation updatedResignation) {
        // 퇴사 정보 원본 확인 및 조회
        Resignation originalResignation = findResignationById(employeeId);

        // 퇴사 정보 수정할 updatedResignation 객체 초기화
        UserSessionInfo processInfo = new UserSessionInfo();
        updatedResignation.initializeResignationDetails(employeeId, updatedResignation, processInfo.getCurrentEmployeeId(), processInfo.getNow());

        // 변경 사항 확인
        checkForResignationChanges(originalResignation, updatedResignation);

        // 사원 퇴사 정보 수정
        resignationDAO.updateResignation(updatedResignation);
    }

    @Transactional
    @Override // 사원 퇴사 정보 삭제
    public void deleteResignation(String employeeId) {
        // 퇴사 정보 확인 및 조회
        findResignationById(employeeId);

        // 퇴사 정보 삭제
        resignationDAO.deleteResignation(employeeId);
    }

    // Resignation의 특정 필드만 비교하도록 필드 이름을 Set으로 전달하는 메소드
    private void checkForResignationChanges(Resignation original, Resignation updated) {
        Set<String> fieldsToCompare = new HashSet<>(Arrays.asList(
                "resignationReason", "codeNumber", "specificReason", "resignationDate", "resignationDocuments"
        ));

        commonService.verifyChanges(original, updated, fieldsToCompare);
    }

    @Transactional
    @Override // 퇴사 문서 업로드
    public void uploadResignationDocuments(Resignation resignation, MultipartFile[] resignationDocuments) {

    }
}
