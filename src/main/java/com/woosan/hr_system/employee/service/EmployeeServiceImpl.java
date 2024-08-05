package com.woosan.hr_system.employee.service;

import com.woosan.hr_system.auth.dao.PasswordDAO;
import com.woosan.hr_system.auth.model.Password;
import com.woosan.hr_system.auth.service.AuthService;
import com.woosan.hr_system.employee.dao.EmployeeDAO;
import com.woosan.hr_system.employee.dao.ResignationDAO;
import com.woosan.hr_system.employee.model.Employee;
import com.woosan.hr_system.employee.model.Resignation;
import com.woosan.hr_system.search.PageRequest;
import com.woosan.hr_system.search.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeDAO employeeDAO;

    @Autowired
    private ResignationDAO resignationDAO;

    @Autowired
    private AuthService authService;

    @Autowired
    private PasswordDAO passwordDAO;

    // ============================================ 조회 관련 로직 start-point ============================================
    @Override // 모든 사원 정보 조회
    public PageResult<Employee> searchEmployees(PageRequest pageRequest) {
        int offset = pageRequest.getPage() * pageRequest.getSize();
        List<Employee> employees = employeeDAO.search(pageRequest.getKeyword(), pageRequest.getSize(), offset);
        int total = employeeDAO.count(pageRequest.getKeyword());

        return new PageResult<>(employees, (int) Math.ceil((double) total / pageRequest.getSize()), total, pageRequest.getPage());
    }

    @Override // id를 이용한 특정 사원 정보 조회
    public Employee getEmployeeById(String employeeId) {
        return employeeDAO.getEmployeeById(employeeId);
    }

    @Override // id를 이용한 특정 사원 정보 조회 (Resignation, Password 정보 포함)
    public Employee getEmployeeWithAdditionalInfo(String employeeId) {
        Employee employee = employeeDAO.getEmployeeById(employeeId);
        if (employee == null) {
            return null;
        }

        Password password = passwordDAO.selectPassword(employeeId);
        if (password != null) {
            employee.setPassword(password);
        }

        Resignation resignation = resignationDAO.getResignedEmployee(employeeId);
        if (resignation != null) {
            employee.setResignation(resignation);
        }

        return employee;
    }
    // ============================================= 조회 관련 로직 end-point =============================================

    // ============================================ 등록 관련 로직 start-point ============================================
    @Override // 사원 정보 등록
    public String insertEmployee(Employee employee) {
        if (employee.getName() == null || employee.getPhone() == null || employee.getEmail() == null || employee.getAddress() == null || employee.getDetailAddress() == null || employee.getDepartment() == null || employee.getPosition() == null || employee.getHireDate() == null) {
            return "employeeEmpty";
        } else {
            //  Employee ID 형식 : AABBCCC (부서 코드, 입사년도, 해당 년도 입사 순서)
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String BB = employee.getHireDate().format(formatter).substring(2, 4);
            int currentYearEmpolyeesCount = employeeDAO.countEmployeesByCurrentYear();
            String CCC = String.format("%03d", currentYearEmpolyeesCount + 1);
            String employeeId = employee.getDepartment() + BB + CCC;

            // 사원 번호 중복 체크
            if (employeeDAO.existsById(employeeId)) {
                throw new DuplicateKeyException("이미 존재하는 사원 아이디입니다. : " + employeeId);
            }
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
    }
    // ============================================= 등록 관련 로직 end-point =============================================

    // ============================================ 수정 관련 로직 start-point ============================================
    @Override // 사원 정보 수정
    public String updateEmployee(Employee employee) {
        String CurrentEmployeeId = employee.getEmployeeId();
        Employee originalEmployee = employeeDAO.getEmployeeById(CurrentEmployeeId);

        // 변경 사항 확인
        boolean isModified = false;
        if (!Objects.equals(originalEmployee.getName(), employee.getName())) {
            isModified = true;
        }
        if (!Objects.equals(originalEmployee.getBirth(), employee.getBirth())) {
            isModified = true;
        }
        if (!Objects.equals(originalEmployee.getResidentRegistrationNumber(), employee.getResidentRegistrationNumber())) {
            isModified = true;
        }
        if (!Objects.equals(originalEmployee.getPhone(), employee.getPhone())) {
            isModified = true;
        }
        if (!Objects.equals(originalEmployee.getEmail(), employee.getEmail())) {
            isModified = true;
        }
        if (!Objects.equals(originalEmployee.getAddress(), employee.getAddress())) {
            isModified = true;
        }
        if (!Objects.equals(originalEmployee.getDetailAddress(), employee.getDetailAddress())) {
            isModified = true;
        }
        if (!Objects.equals(originalEmployee.getHireDate(), employee.getHireDate())) {
            isModified = true;
        }
        if (!Objects.equals(originalEmployee.getStatus(), employee.getStatus())) {
            isModified = true;
        }
        if (!Objects.equals(originalEmployee.getDepartment(), employee.getDepartment())) {
            isModified = true;
        }
        if (!Objects.equals(originalEmployee.getPosition(), employee.getPosition())) {
            isModified = true;
        }
        if (!Objects.equals(originalEmployee.getRemainingLeave(), employee.getRemainingLeave())) {
            isModified = true;
        }
        if (!Objects.equals(originalEmployee.getPicture(), employee.getPicture())) {
            isModified = true;
        }

        // 변경 사항 X
        if (!isModified) {
            return "no_changes";
        }

        // 변경 사항 O
        try {
            employee.setModifiedBy(CurrentEmployeeId);
            employee.setLastModified(LocalDateTime.now());
            employeeDAO.updateEmployee(employee);
            return "success";
        } catch (DataAccessException dae) {
            log.error("‼️사원 정보 수정 중 데이터베이스 오류 발생 : " + dae.getMessage(), dae);
            return "error";
        } catch (Exception e) {
            log.error("‼️사원 정보 수정 중 알 수 없는 오류 발생 : " + e.getMessage(), e);
            return "fail";
        }
    }
    // ============================================ 수정 관련 로직 end-point ============================================

    // ============================================ 퇴사 관련 로직 start-point ============================================
    @Override // 퇴사 예정인 사원 정보 조회
    public List<Employee> getPreResignationEmployees() {
        List<Employee> employees = employeeDAO.getPreResignationEmployees();
        return employees;
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

    @Override // 사원 퇴사 처리 로직
    public String resignEmployee(String employeeId, Resignation resignation) {
        // 재직 상태 - 퇴사 처리
        Employee employee = employeeDAO.getEmployeeById(employeeId);
        if (employee == null) {
            return "null";
        }
        employee.setStatus("퇴사");
        employeeDAO.updateEmployee(employee);

        // 퇴사 사유와 코드 분류
        classifyReason(resignation);
        classifyCodeNumber(resignation);

        // employeeId 설정
        resignation.setEmployeeId(employeeId);

        // 로그인된 사용자(처리 사원) 정보와 일시 설정
        resignation.setProcessedDate(LocalDateTime.now());
        resignation.setProcessedBy(authService.getAuthenticatedUser().getUsername());

        resignationDAO.insertResignation(resignation);
        return "success";
    }

    @Override // 사원 퇴사 정보 수정
    public String updateResignationInfo(String employeeId, Resignation newResignation) {
        Resignation originalResignation = resignationDAO.getResignedEmployee(employeeId);
        // 사원 확인
        checkResignationNull(originalResignation);

        // 변경 사항 확인
        if (!compareOriginalToNew(originalResignation, newResignation))
            throw new IllegalArgumentException("사원의 퇴사 정보에 변경 사항이 없습니다.");

        // 퇴사 사유와 코드 분류 후 입력
        classifyReason(newResignation);
        classifyCodeNumber(newResignation);

        // employeeId 입력
        newResignation.setEmployeeId(employeeId);

        // 퇴사 정보 수정자와 일시 입력
        newResignation.setProcessedDate(LocalDateTime.now());
        newResignation.setProcessedBy(authService.getAuthenticatedUser().getUsername());

        // 사원 퇴사 정보 수정
        try {
            resignationDAO.updateResignation(newResignation);
            return null;
        } catch (DataAccessException dae) {
            throw new RuntimeException("데이터베이스 오류로 인해 퇴사 정보 수정에 실패하였습니다.", dae);
        } catch (Exception e) {
            throw new RuntimeException("사원 퇴사 정보 수정 중 오류가 발생하였습니다.", e);
        }
    }

    @Override // 새로운 문서 이름과 기존 문서 이름 합치는 메소드
    public void updateResignationDocuments(Resignation resignation, String registeredResignationDocuments) {
        StringBuilder sb = new StringBuilder();

        // 새로 등록한 문서 확인 후 sb에 입력
        String resignationDocuments = resignation.getResignationDocuments();
        if (resignationDocuments != null) sb.append(resignationDocuments);

        // 기존 등록한 문서 확인 후 sb에 입력
        if (registeredResignationDocuments != null) sb.append(registeredResignationDocuments);

        resignation.setResignationDocuments(sb.toString());
    }

    @Override // 사원 정보 삭제
    public String deleteEmployee(String employeeId) {
        Employee employee = employeeDAO.getEmployeeById(employeeId);
        if (employee == null) {
            return "null"; // 사원 정보 없음
        } else {
            employee.setResignation(resignationDAO.getResignedEmployee(employeeId));
            if (employee.getResignation() == null) {
                return "null_resignation"; // 퇴사 정보 없음
            }

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
    }
    // ============================================= 퇴사 관련 로직 end-point =============================================

    // ============================================== 기타 로직 start-point ==============================================
    // 사원 정보 Null 확인하는 메소드
    private void checkEmployeeNull(Employee employee) {
        if (employee == null) {
            throw new IllegalArgumentException("해당 사원을 찾을 수 없습니다.");
        }
    }

    // 사원 퇴사 정보 Null 확인하는 메소드
    private void checkResignationNull(Resignation resignation) {
        if (resignation == null) {
            throw new IllegalArgumentException("해당 사원의 퇴사 정보를 찾을 수 없습니다.");
        }
    }

    // 사원 새로운 퇴사 정보와 기존 퇴사정보 비교하는 메소드
    private boolean compareOriginalToNew(Resignation Original, Resignation New) {
        // 변경 사항 확인
        boolean isModified = false;
        if (!Objects.equals(Original.getResignationDate(), New.getResignationDate())) {
            isModified = true;
        }
        if (!Objects.equals(Original.getResignationReason(), New.getResignationReason())) {
            isModified = true;
        }
        if (!Objects.equals(Original.getCodeNumber(), New.getCodeNumber())) {
            isModified = true;
        }
        if (!Objects.equals(Original.getSpecificReason(), New.getSpecificReason())) {
            isModified = true;
        }
        if (!Objects.equals(Original.getResignationDocuments(), New.getResignationDocuments())) {
            isModified = true;
        }
        return isModified;
    }

    // 퇴사 사유 분류하는 메소드
    private void classifyReason(Resignation resignation) {
        // 퇴사 사유 분류
        switch (resignation.getResignationReason()) {
            case "1":
                resignation.setResignationReason("1. 자진퇴사");
                break;
            case "2":
                resignation.setResignationReason("2. 권고사직 : 회사 사정과 근로자 귀책에 의한 이직");
                break;
            case "3":
                resignation.setResignationReason("3. 정년 등 기간만료에 의한 이직");
                break;
            case "4":
                resignation.setResignationReason("4. 기타");
                break;
            default:
                throw new IllegalArgumentException("잘못된 퇴사 사유입니다.");
        }
    }

    // 퇴사 코드 분류하는 메소드
    private void classifyCodeNumber(Resignation resignation) {
        // 퇴사 코드 분류
        switch (resignation.getCodeNumber()) {
            case "11":
                resignation.setCodeNumber("11. 개인사정으로 인한 자진퇴사");
                break;
            case "12":
                resignation.setCodeNumber("12. 사업장 이전, 근로조건(계약조건) 변동, 임금체불 등으로 자진퇴사");
                break;
            case "22":
                resignation.setCodeNumber("22. 폐업, 도산(예정 포함), 공사 중단");
                break;
            case "23":
                resignation.setCodeNumber("23. 경영상 필요 및 회사 불황으로 인원 감축 등에 의한 퇴사 (해고•권고사직•계약파기 포함)");
                break;
            case "26":
                resignation.setCodeNumber("26. 피보험자의 귀책사유에 의한 징계해고•권고사직 또는 계약 파기");
                break;
            case "31":
                resignation.setCodeNumber("31. 정년");
                break;
            case "32":
                resignation.setCodeNumber("32. 계약기간만료, 공사 종료");
                break;
            case "41":
                resignation.setCodeNumber("41. 고용보험 비적용");
                break;
            case "42":
                resignation.setCodeNumber("42. 이중고용");
                break;
            default:
                throw new IllegalArgumentException("잘못된 퇴사 코드입니다.");
        }
    }
}
