package com.woosan.hr_system.employee.service;

import com.woosan.hr_system.auth.dao.PasswordDAO;
import com.woosan.hr_system.auth.model.ModificationInfo;
import com.woosan.hr_system.auth.model.Password;
import com.woosan.hr_system.auth.service.AuthService;
import com.woosan.hr_system.employee.dao.EmployeeDAO;
import com.woosan.hr_system.employee.dao.ResignationDAO;
import com.woosan.hr_system.employee.model.Employee;
import com.woosan.hr_system.employee.model.Position;
import com.woosan.hr_system.employee.model.Resignation;
import com.woosan.hr_system.exception.employee.EmployeeNotFoundException;
import com.woosan.hr_system.exception.employee.NoChangesDetectedException;
import com.woosan.hr_system.exception.employee.PasswordNotFoundException;
import com.woosan.hr_system.exception.employee.ResignationNotFoundException;
import com.woosan.hr_system.search.PageRequest;
import com.woosan.hr_system.search.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeDAO employeeDAO;
    @Autowired
    private ResignationDAO resignationDAO;
    @Autowired
    private PasswordDAO passwordDAO;
    @Autowired
    private AuthService authService;

    // ============================================ 조회 관련 로직 start-point ============================================
    @Override // id를 이용한 특정 사원 정보 조회
    public Employee getEmployeeById(String employeeId) {
        return findEmployeeById(employeeId);
    }

    // 사원 정보를 찾는 메소드
    private Employee findEmployeeById(String employeeId) {
        Employee employee = employeeDAO.getEmployeeById(employeeId);
        if (employee == null) {
            throw new EmployeeNotFoundException(employeeId);
        }
        return employee;
    }

    @Override // id를 이용한 특정 사원 정보 조회 (비밀번호 정보, 퇴사 정보 포함)
    public Employee getEmployeeDetails(String employeeId) {
        Employee employee = findEmployeeById(employeeId);
        // 비밀번호 정보 조회 및 설정
        verifyAndSetPasswordInfo(employee);
        // 퇴사 정보 조회 및 설정
        verifyAndSetResignationInfo(employee);
        return employee;
    }

    // 사원 비밀번호 정보 확인 후 설정하는 메소드
    private void verifyAndSetPasswordInfo(Employee employee) {
        String employeeId = employee.getEmployeeId();
        Password passwordInfo = passwordDAO.selectPassword(employeeId);
        if (passwordInfo == null) throw new PasswordNotFoundException(employeeId);
        employee.setPassword(passwordInfo);
    }

    // 사원 퇴사 정보 확인 후 설정하는 메소드
    private void verifyAndSetResignationInfo(Employee employee) {
        String employeeId = employee.getEmployeeId();
        if (employee.getStatus().equals("퇴사")) {
            Resignation resignation = resignationDAO.getResignedEmployee(employeeId);
            verifyNullResignation(resignation, employeeId);
            employee.setResignation(resignation);
        }
    }

    // 사원 퇴사 정보 Null 확인하는 메소드
    private void verifyNullResignation(Resignation resignation, String employeeId) {
        if (resignation == null) throw new ResignationNotFoundException(employeeId);
    }

    @Override // 모든 사원 정보 조회
    public PageResult<Employee> searchEmployees(PageRequest pageRequest) {
        int offset = pageRequest.getPage() * pageRequest.getSize();
        List<Employee> employees = employeeDAO.search(pageRequest.getKeyword(), pageRequest.getSize(), offset);
        int total = employeeDAO.count(pageRequest.getKeyword());

        return new PageResult<>(employees, (int) Math.ceil((double) total / pageRequest.getSize()), total, pageRequest.getPage());
    }
    // =========================================== 퇴사 조회 관련 로직 start-point ==========================================
    @Override // 퇴사 예정인 사원 정보 조회
    public List<Employee> getPreResignationEmployees() {
        return employeeDAO.getPreResignationEmployees();
    }

    @Override // 퇴사 후 2개월 이내의 사원 정보 조회
    public List<Employee> getResignedEmployees() {
        List<Employee> employees = employeeDAO.getResignedEmployees();
        return mergeEmployeeWithResignation(employees);
    }

    @Override // 퇴사 후 12개월이 지난 사원 정보 조회
    public List<Employee> getPreDeletionEmployees() {
        List<Employee> employees = employeeDAO.getPreDeletionEmployees();
        return mergeEmployeeWithResignation(employees);
    }

    // 사원 목록에 해당 사원의 퇴사정보를 합쳐주는 메소드
    private List<Employee> mergeEmployeeWithResignation(List<Employee> employees) {
        List<Resignation> resignations = resignationDAO.getAllResignedEmployees();
        for (Employee employee : employees) {
            for (Resignation resignation : resignations) {
                if (employee.getEmployeeId().equals(resignation.getEmployeeId())) {
                    employee.setResignation(resignation);
                    break;
                }
            }
        }
        return employees;
    }
    // ============================================= 조회 관련 로직 end-point =============================================

    // ============================================ 등록 관련 로직 start-point ============================================
    @Override // 사원 정보 등록
    public String insertEmployee(Employee employee) {
        // 필수 필드를 검증
        List<Object> requiredFields = Arrays.asList(
                employee.getName(),
                employee.getPhone(),
                employee.getEmail(),
                employee.getAddress(),
                employee.getDetailAddress(),
                employee.getDepartment(),
                employee.getPosition(),
                employee.getHireDate()
        );
        boolean hasEmptyField = requiredFields.stream().anyMatch(Objects::isNull);
        if (hasEmptyField) return "employeeEmpty";

        //  Employee ID 생성 - 형식 : AABBCCC (부서 코드, 입사년도, 해당 년도 입사 순서)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String BB = employee.getHireDate().format(formatter).substring(2, 4);
        int currentYearEmpolyeesCount = employeeDAO.countEmployeesByCurrentYear();
        String CCC = String.format("%03d", currentYearEmpolyeesCount + 1);
        String employeeId = employee.getDepartment() + BB + CCC;

        // 사원 번호 중복 체크
        verifyDuplicateId(employeeId);
        employee.setEmployeeId(employeeId);

        // 첫 비밀번호 생년월일로 설정
        authService.insertFirstPassword(employeeId, employee.getBirth());

        // 재직 상태 설정
        employee.setStatus("재직");

        // 기본 연차 11일 설정
        employee.setRemainingLeave(11);

        employeeDAO.insertEmployee(employee);

        return "success";
    }

    // 사원 번호 중복 체크하는 메소드
    private void verifyDuplicateId(String employeeId) {
        if (employeeDAO.existsById(employeeId)) {
            throw new DuplicateKeyException("이미 존재하는 사원 아이디입니다. 사원 ID : " + employeeId);
        }
    }

    @Override // 사원 퇴사 처리 로직
    public String resignEmployee(String employeeId, Resignation resignation) {
        // 재직 상태 - 퇴사 처리
        Employee employee = findEmployeeById(employeeId);
        employee.setStatus("퇴사");
        employeeDAO.updateEmployee(employee);

        // 퇴사 사유와 코드 분류
        classifyReason(resignation);
        classifyCodeNumber(resignation);

        // employeeId 설정
        resignation.setEmployeeId(employeeId);

        // 처리 사원번호와 처리 일시 설정
        setModificationInfoToResignation(resignation);

        resignationDAO.insertResignation(resignation);
        return "success";
    }

    // 퇴사 사유와 설명을 매핑하는 맵 초기화
    private static final Map<String, String> reasonDescriptions = new HashMap<>();
    static {
        reasonDescriptions.put("1", "1. 자진퇴사");
        reasonDescriptions.put("2", "2. 권고사직 : 회사 사정과 근로자 귀책에 의한 이직");
        reasonDescriptions.put("3", "3. 정년 등 기간만료에 의한 이직");
        reasonDescriptions.put("4", "4. 기타");
    }

    // 퇴사 코드와 설명을 매핑하는 맵 초기화
    private static final Map<String, String> codeDescriptions = new HashMap<>();
    static {
        codeDescriptions.put("11", "11. 개인사정으로 인한 자진퇴사");
        codeDescriptions.put("12", "12. 사업장 이전, 근로조건(계약조건) 변동, 임금체불 등으로 자진퇴사");
        codeDescriptions.put("22", "22. 폐업, 도산(예정 포함), 공사 중단");
        codeDescriptions.put("23", "23. 경영상 필요 및 회사 불황으로 인원 감축 등에 의한 퇴사 (해고•권고사직•계약파기 포함)");
        codeDescriptions.put("26", "26. 피보험자의 귀책사유에 의한 징계해고•권고사직 또는 계약 파기");
        codeDescriptions.put("31", "31. 정년");
        codeDescriptions.put("32", "32. 계약기간만료, 공사 종료");
        codeDescriptions.put("41", "41. 고용보험 비적용");
        codeDescriptions.put("42", "42. 이중고용");
    }

    // 퇴사 사유 분류하는 메소드
    private void classifyReason(Resignation resignation) {
        String codeNumber = resignation.getCodeNumber();
        String description = reasonDescriptions.get(codeNumber);
        if (description == null) {
            throw new IllegalArgumentException("잘못된 퇴사 사유입니다.");
        }
        resignation.setResignationReason(description);
    }

    // 퇴사 코드 분류하는 메소드
    private void classifyCodeNumber(Resignation resignation) {
        String codeNumber = resignation.getCodeNumber();
        String description = codeDescriptions.get(codeNumber);
        if (description == null) {
            throw new IllegalArgumentException("잘못된 퇴사 코드입니다.");
        }
        resignation.setCodeNumber(description);
    }
    // ============================================= 등록 관련 로직 end-point =============================================

    // ============================================ 수정 관련 로직 start-point ============================================
    @Override // 사원 정보 수정
    public String updateEmployee(Employee updatedEmployee) {
        String employeeId = updatedEmployee.getEmployeeId();
        Employee originalEmployee = employeeDAO.getEmployeeById(employeeId);

        // 변경사항 확인
        checkForEmployeeChanges(originalEmployee, updatedEmployee);

        // 수정 사원번호와 수정 일시 설정
        setModificationInfoToEmployee(updatedEmployee);

        // 사원 정보 수정 처리
        employeeDAO.updateEmployee(updatedEmployee);
        return "success";
    }

    // Employee의 특정 필드만 비교하도록 필드 이름을 Set으로 전달하는 메소드
    private void checkForEmployeeChanges(Employee original, Employee updated) {
        Set<String> fieldsToCompare = new HashSet<>();
        fieldsToCompare.add("name");
        fieldsToCompare.add("birth");
        fieldsToCompare.add("residentRegistrationNumber");
        fieldsToCompare.add("phone");
        fieldsToCompare.add("email");
        fieldsToCompare.add("address");
        fieldsToCompare.add("detailAddress");
        fieldsToCompare.add("department");
        fieldsToCompare.add("position");
        fieldsToCompare.add("hireDate");
        fieldsToCompare.add("status");
        fieldsToCompare.add("remainingLeave");
        fieldsToCompare.add("picture");

        verifyChanges(original, updated, fieldsToCompare);
    }

    // 사원 정보에 수정 정보 설정
    private void setModificationInfoToEmployee(Employee employee) {
        ModificationInfo info = getModificationInfo();
        employee.setModifiedBy(info.getModifiedBy());
        employee.setLastModified(info.getLastModified());
    }

    @Override // 재직 상태 수정하는 메소드
    public String updateStatus(String employeeId, String status) {
        // 수정 정보들 Map에 담기
        Map<String, Object> params = new HashMap<>();
        params.put("status", status);
        params.put("lastModified", LocalDateTime.now());
        params.put("modifiedBy", authService.getAuthenticatedUser().getUsername());
        params.put("employeeId", employeeId);
        try {
            employeeDAO.updateStatus(params);
            String updatedStatus = employeeDAO.getEmployeeById(employeeId).getStatus();
            return "'" + getEmployeeById(employeeId).getName() + "' 사원의 재직 상태가 '" + updatedStatus + "'으로 변경되었습니다.";
        } catch (DataAccessException dae) {
            throw new RuntimeException("재직 상태 변경 중 데이터베이스 오류가 발생했습니다.\n관리자에게 문의하세요.");
        } catch (Exception e) {
            throw new RuntimeException("재직 상태 변경 중 오류가 발생했습니다.\n관리자에게 문의하세요.");
        }
    }

    @Override // 직급 승진시키는 메소드
    public String promoteEmployee(String employeeId) {
        // 현재 랭크 가져온 뒤 부장과 사장인지 확인
        int positionRank = Position.getRankByPositionName(employeeDAO.getEmployeeById(employeeId).getPosition().name());
        if (positionRank >= 5) throw new IllegalArgumentException("해당 직급에서는 더 이상 승진할 수 없습니다.");

        // 랭크 +1로 승진 처리
        Position positionToBePromoted = Position.fromRank(positionRank + 1);
        log.info("현재 직급의 랭크는 : {}", positionRank);
        log.info("승진할 직급은 : {}", positionToBePromoted);

        // 사원 번호와 승진할 직급 Map에 담기
        Map<String, Object> params = new HashMap<>();
        params.put("employeeId", employeeId);
        params.put("positionId", positionToBePromoted);
        params.put("lastModified", LocalDateTime.now());
        params.put("modifiedBy", authService.getAuthenticatedUser().getUsername());
        try {
            employeeDAO.updatePosition(params);
            Position promotedPosition = employeeDAO.getEmployeeById(employeeId).getPosition();
            return "'" + getEmployeeById(employeeId).getName() + "' 사원이 '" + promotedPosition + "'으로 승진하였습니다.";
        } catch (DataAccessException dae) {
            log.error("승진 처리 중 DB 오류가 발생했습니다 : {}", dae.getMessage(), dae);
            throw new RuntimeException("승진 처리 중 데이터베이스 오류가 발생했습니다.\n관리자에게 문의하세요.");
        } catch (Exception e) {
            log.error("승진 처리 중 오류가 발생했습니다 : {}", e.getMessage(), e);
            throw new RuntimeException("승진 처리 중 오류가 발생했습니다.\n관리자에게 문의하세요.");
        }
    }

    @Override // 계정 잠금과 해제 수정하는 메소드
    public String setAccountLock(String employeeId) {
        int pwdCount = passwordDAO.getPasswordCount(employeeId);
        try {
            if (pwdCount == 5) { // 계정 잠금해제
                passwordDAO.resetPasswordCount(employeeId);
                return "사원의 계정이 잠금 해제되었습니다.";
            }
            else { // 계정 잠금
                passwordDAO.maxOutPasswordCount(employeeId);
                return "사원의 계정이 잠금 처리되었습니다.";
            }
        } catch (DataAccessException dae) {
            throw new RuntimeException("계정 잠금 상태 변경 중 데이터베이스 오류가 발생했습니다.\n관리자에게 문의하세요.");
        } catch (Exception e) {
            throw new RuntimeException("계정 잠금 상태 변경 중 오류가 발생했습니다.\n관리자에게 문의하세요.");
        }
    }
    // =========================================== 퇴사 수정 관련 로직 start-point ==========================================
    @Override // 사원 퇴사 정보 수정
    public void updateResignationInfo(String employeeId, Resignation updatedResignation) {
        Resignation originalResignation = resignationDAO.getResignedEmployee(employeeId);
        // 퇴사 사원 정보 확인
        verifyNullResignation(originalResignation, employeeId);

        // 퇴사 사유와 코드 분류 후 입력
        classifyReason(updatedResignation);
        classifyCodeNumber(updatedResignation);

        // 변경 사항 확인
        checkForResignationChanges(originalResignation, updatedResignation);

        // employeeId 입력
        updatedResignation.setEmployeeId(employeeId);

        // 수정 사원번호와 수정 일시 설정
        setModificationInfoToResignation(updatedResignation);

        // 사원 퇴사 정보 수정
        try {
            resignationDAO.updateResignation(updatedResignation);
        } catch (DataAccessException dae) {
            throw new RuntimeException("데이터베이스 오류로 인해 퇴사 정보 수정에 실패하였습니다.", dae);
        } catch (Exception e) {
            throw new RuntimeException("사원 퇴사 정보 수정 중 오류가 발생하였습니다.", e);
        }
    }

    // Resignation의 특정 필드만 비교하도록 필드 이름을 Set으로 전달하는 메소드
    private void checkForResignationChanges(Resignation original, Resignation updated) {
        Set<String> fieldsToCompare = new HashSet<>();
        fieldsToCompare.add("resignationReason");
        fieldsToCompare.add("codeNumber");
        fieldsToCompare.add("specificReason");
        fieldsToCompare.add("resignationDate");
        fieldsToCompare.add("resignationDocuments");

        verifyChanges(original, updated, fieldsToCompare);
    }
    // ============================================ 수정 관련 로직 end-point ============================================

    // ============================================== 삭제 로직 start-point ==============================================
    @Override // 사원 정보 삭제
    public String deleteEmployee(String employeeId) {
        Employee employee = findEmployeeById(employeeId);

        // 삭제 전 퇴사 정보 확인
        verifyAndSetResignationInfo(employee);

        LocalDate resignationDate = employee.getResignation().getResignationDate(); // 퇴사일자
        LocalDate oneYearLater = resignationDate.plusDays(365); // 퇴사일자 1년 경과일
        LocalDate now = LocalDate.now();

        if (oneYearLater.isBefore(now) || oneYearLater.isEqual(now)) {
            employeeDAO.deleteEmployee(employeeId);
            resignationDAO.deleteResignation(employeeId);
            return "success"; // 1년이 지남 (삭제 가능)
        } else {
            return "not_expired"; // 1년이 지나지 않음
        }
    }
    // =============================================== 삭제 로직 end-point ===============================================

    // ============================================== 기타 로직 start-point ==============================================
    @Override // 기존 문서 이름 합치는 메소드
    public void updateResignationDocuments(Resignation resignation, String newDocumentsName) {
        String originalDocumentsName = resignation.getResignationDocuments();
        // 새로 등록하거나 기존 파일이 없을 경우
        if (originalDocumentsName == null) resignation.setResignationDocuments(newDocumentsName);
            // 기존 파일이 있을 경우
        else resignation.setResignationDocuments(originalDocumentsName + newDocumentsName);
    }

    // 수정한 사원 번호와 일시 반환하는 메소드
    private ModificationInfo getModificationInfo() {
        String modifiedBy = authService.getAuthenticatedUser().getUsername();
        LocalDateTime lastModified = LocalDateTime.now();
        return new ModificationInfo(modifiedBy, lastModified);
    }
    // 사원 퇴사 정보에 수정 정보 설정
    private void setModificationInfoToResignation(Resignation resignation) {
        ModificationInfo info = getModificationInfo();
        resignation.setProcessedBy(info.getModifiedBy());
        resignation.setProcessedDate(info.getLastModified());
    }

    // 변경사항 확인하는 메소드
    private <T> void verifyChanges(T original, T updated, Set<String> fieldsToCompare) {
        if (!compareFields(original, updated, fieldsToCompare)) {
            throw new NoChangesDetectedException();
        }
    }
    // 두 객체의 필드가 동일한지 비교 확인하는 메소드
    private <T> boolean compareFields (T original, T updated, Set<String> fieldsToCompare) {
        try {
            for (Field field : original.getClass().getDeclaredFields()) {
                field.setAccessible(true); // 비공개 필드 접근 가능
                if (fieldsToCompare.contains(field.getName())) {
                    Object originalValue = field.get(original);
                    Object updatedValue = field.get(updated);
                    if (!Objects.equals(originalValue, updatedValue)) {
                        return true; // 변경 사항 있음
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException("변경사항 필드 확인 중 오류가 발생했습니다.", e);
        }
        return false; // 변경 사항 없음
    }


}
