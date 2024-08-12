package com.woosan.hr_system.resignation.service;

import com.woosan.hr_system.auth.model.UserSessionInfo;
import com.woosan.hr_system.common.service.CommonService;
import com.woosan.hr_system.exception.employee.NoChangesDetectedException;
import com.woosan.hr_system.exception.employee.ResignationNotFoundException;
import com.woosan.hr_system.resignation.dao.ResignationDAO;
import com.woosan.hr_system.resignation.dao.ResignationFileDAO;
import com.woosan.hr_system.resignation.model.Resignation;
import com.woosan.hr_system.resignation.model.ResignationFile;
import com.woosan.hr_system.upload.model.File;
import com.woosan.hr_system.upload.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Slf4j
@Service
public class ResignationServiceImpl implements ResignationService {

    @Autowired
    private CommonService commonService;
    @Autowired
    private FileService fileService;
    @Autowired
    private ResignationDAO resignationDAO;
    @Autowired
    private ResignationFileDAO resignationFileDAO;

    private static final String FILE_USAGE = "resignation";

    // ====================================================== 조회 ======================================================
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

    @Override // resignationId(employeeId)의 모든 파일 ID 조회
    public List<Integer> getFileIdList(String employeeId) {
        return resignationFileDAO.selectFileIdsByResignationId(employeeId);
    }

    @Override // resignationId(employeeId)의 모든 파일 정보 조회
    public List<File> getAllFileInfo(String employeeId) {
        return resignationFileDAO.selectAllFileInfo(employeeId);
    }

    // resignationId(employeeId)의 파일 개수 조회
    private int countFilesByResignationId(String employeeId) {
        return resignationFileDAO.countFilesByResignationId(employeeId);
    }
    // ====================================================== 조회 ======================================================

    // ====================================================== 등록 ======================================================
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
    @Override // 퇴사 문서 업로드
    public void uploadNewFiles(String employeeId, MultipartFile[] resignationDocuments) {
        // 퇴사 문서 개별 업로드
        for (MultipartFile resignationDocument : resignationDocuments) {
            int fileId = fileService.uploadingFile(resignationDocument, FILE_USAGE);
            // 조인 테이블에 등록
            insertResignationFile(employeeId, fileId);
        }
    }

    // 조인 테이블에 등록
    private void insertResignationFile(String employeeId, int fileId) {
        ResignationFile resignationFile = new ResignationFile(employeeId, fileId);
        resignationFileDAO.insertResignationFile(resignationFile);
    }
    // ====================================================== 등록 ======================================================

    // ====================================================== 삭제 ======================================================
    @Transactional
    @Override // 사원 퇴사 정보 삭제
    public void deleteResignation(String employeeId) {
        // 퇴사 정보 확인 및 조회
        findResignationById(employeeId);

        // 조인 테이블 모든 레코즈 삭제
        deleteAllByResignationId(employeeId);

        // 퇴사 정보 삭제
        resignationDAO.deleteResignation(employeeId);
    }

    // 특정 resignationId(employeeId)에 대한 모든 파일 레코드 삭제
    private void deleteResignationFile(String employeeId, int fileId) {
        resignationFileDAO.deleteResignationFile(new ResignationFile(employeeId, fileId));
    }

    // 특정 resignationId(employeeId)에 대한 모든 파일 레코드 삭제
    private void deleteAllByResignationId(String employeeId) {
        resignationFileDAO.deleteAllByResignationId(employeeId);
    }
    // ====================================================== 삭제 ======================================================

    // ================================================== 퇴사 정보 수정 ==================================================
    @Transactional
    @Override // 사원 퇴사 정보 수정
    public void updateResignation(String employeeId, Resignation resignation, List<Integer> fileIdList, MultipartFile[] newFileArr) {
        // 사원 퇴사 정보와 사원 퇴사 문서 변경사항 확인
        boolean hasChangedResignation = hasChangedResignation(employeeId, resignation);
        boolean hasChangedOriginalFiles = !compareFileIdList(employeeId, fileIdList).isEmpty();
        boolean hasNewFiles = newFileArr != null && newFileArr.length > 0;

        // 변경사항 없음
        if (!hasChangedResignation && !hasChangedOriginalFiles && !hasNewFiles) {
            throw new NoChangesDetectedException();
        }

        // 기존 퇴사 문서 수정
        if (hasChangedOriginalFiles) updateOldFiles(employeeId, fileIdList);

        // 새로운 퇴사 문서 파일 업로드
        if (hasNewFiles) uploadNewFiles(employeeId, newFileArr);

        // 현재 업로드된 파일 개수 확인
        fileService.checkFilesLength(countFilesByResignationId(employeeId));

        // 퇴사 정보 수정
        if (hasChangedResignation) resignationDAO.updateResignation(resignation);
    }

    // 퇴사 정보 변경사항 확인
    private boolean hasChangedResignation(String employeeId, Resignation updated) {
        // 퇴사 정보 원본 확인 및 조회
        Resignation original = findResignationById(employeeId);

        // 퇴사 정보 수정할 updatedResignation 객체 초기화
        UserSessionInfo processInfo = new UserSessionInfo();
        updated.initializeResignationDetails(employeeId, updated, processInfo.getCurrentEmployeeId(), processInfo.getNow());

        // Resignation의 특정 필드만 비교하도록 필드 이름을 Set으로 전달하는 메소드
        Set<String> fieldsToCompare = new HashSet<>(Arrays.asList(
                "resignationReason", "codeNumber", "specificReason", "resignationDate", "resignationDocuments"
        ));

        return commonService.hasFieldChanges(original, updated, fieldsToCompare);
    }

    // 퇴사 파일 ID 리스트 비교 후 변경사항 반환
    private List<Integer> compareFileIdList(String employeeId, List<Integer> fileIdList) {
        List<Integer> originalList = getFileIdList(employeeId);
        List<Integer> changes = new ArrayList<>(originalList);
        changes.removeAll(fileIdList);
        return changes;
    }

    // 퇴사 기존 파일 수정
    private void updateOldFiles(String employeeId, List<Integer> fileIdList) {
        for (Integer fileId : compareFileIdList(employeeId, fileIdList)) {
            // 조인테이블 데이터 삭제
            deleteResignationFile(employeeId, fileId);
            // 파일 정보와 S3 파일 삭제
            fileService.deleteFile(fileId);
        }
    }
}
