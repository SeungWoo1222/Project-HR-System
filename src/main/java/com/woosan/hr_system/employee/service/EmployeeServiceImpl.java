package com.woosan.hr_system.employee.service;

import com.woosan.hr_system.auth.AuthService;
import com.woosan.hr_system.search.PageRequest;
import com.woosan.hr_system.search.PageResult;
import com.woosan.hr_system.auth.CustomUserDetails;
import com.woosan.hr_system.employee.dao.EmployeeDAO;
import com.woosan.hr_system.employee.dao.ResignationDAO;
import com.woosan.hr_system.employee.model.Employee;
import com.woosan.hr_system.employee.model.Resignation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeDAO employeeDAO;

    @Autowired
    private ResignationDAO resignationDAO;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthService authService;

    // 조회 관련 로직 start-point
    @Override // 모든 사원 정보 조회
    public PageResult<Employee> searchEmployees(PageRequest pageRequest) {
        int offset = pageRequest.getPage() * pageRequest.getSize();
        List<Employee> employees = employeeDAO.search(pageRequest.getKeyword(), pageRequest.getSize(), offset);
        int total = employeeDAO.count(pageRequest.getKeyword());

        return new PageResult<>(employees, (int) Math.ceil((double)total / pageRequest.getSize()), total, pageRequest.getPage());
    }

    @Override // id를 이용한 특정 사원 정보 조회
    public Employee getEmployeeById(String employeeId) {
        return employeeDAO.getEmployeeById(employeeId);
    }

    @Override // id를 이용한 특정 사원 정보 조회 (Resignation 정보 포함)
    public Employee getEmployeeWithResignation(String employeeId) {
        Employee employee = employeeDAO.getEmployeeById(employeeId);
        if (employee == null) {
            return null;
        }
        employee.setResignation(resignationDAO.getResignedEmployee(employee.getEmployeeId()));
        return employee;
    }
    // 조회 관련 로직 end-point

    // 등록 관련 로직 start-point
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
            employee.setEmployeeId(employee.getDepartment() + BB + CCC);

            // 첫 비밀번호 생년월일로 설정
            employee.setPassword(passwordEncoder.encode(employee.getBirth()));

            // 재직 상태 설정
            employee.setStatus("재직");

            // 기본 연차 11일 설정
            employee.setRemainingLeave(11);

            employeeDAO.insertEmployee(employee);

            return "success";
        }
    }
    // 등록 관련 로직 end-point

    // 수정 관련 로직 start-point
    @Override // 사원 정보 수정
    public void updateEmployee(Employee employee) {
        employeeDAO.updateEmployee(employee);
    }

    @Override // 사원 정보 일부 수정 (변경 가능한 column - password, name, phone, email, address, detailed_address)
    public void updateEmployeePartial(String employeeId, Map<String, Object> updates) {
        Employee employee = employeeDAO.getEmployeeById(employeeId);
        if (employee != null) {
            if (updates.containsKey("name")) {
                employee.setName((String)updates.get("name"));
            }
            if (updates.containsKey("phone")) {
                employee.setPhone((String)updates.get("phone"));
            }
            if (updates.containsKey("email")) {
                employee.setEmail((String)updates.get("email"));
            }
            if (updates.containsKey("address")) {
                employee.setAddress((String)updates.get("address"));
            }
            if (updates.containsKey("detailed_address")) {
                employee.setDetailAddress((String)updates.get("detailed_address"));
            }

            // 수정한 사원 아이디, 날짜 시간 기록
            employee.setLastModified(LocalDateTime.now());
            employee.setModifiedBy(authService.getAuthenticatedUser().getUsername());

            employeeDAO.updateEmployee(employee);
        }
    }
    // 수정 관련 로직 end-point

    // 퇴사 관련 로직 start-point
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
    public String resignEmployee(String employeeId, String resignationReason, String codeNumber, String specificReason, LocalDate resignationDate, List<String> resignationDocumentsNames) {
        // 재직 상태 - 퇴사 처리
        Employee employee = employeeDAO.getEmployeeById(employeeId);
        if (employee == null) {
            return "null";
        }
        employee.setStatus("퇴사");
        employeeDAO.updateEmployee(employee);

        Resignation resignation = new Resignation();

        // 퇴사 사유 분류
        switch (resignationReason) {
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
        }

        // 퇴사 코드 분류
        switch (codeNumber) {
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

        }

        // 퇴사 테이블에 나머지 정보 등록
        resignation.setEmployeeId(employeeId);
        resignation.setSpecificReason(specificReason);
        resignation.setResignationDate(resignationDate);
        resignation.setProcessedDate(LocalDateTime.now());

        // 파일 정보 등록
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < resignationDocumentsNames.size(); i++) {
            sb.append(resignationDocumentsNames.get(i));
            if (i != resignationDocumentsNames.size() - 1) {
                sb.append(" / ");
            }
        }
        resignation.setResignationDocuments(sb.toString());

        // 로그인된 사용자(처리 사원) 정보 등록
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            resignation.setProcessedBy(userDetails.getUsername());
        }

        resignationDAO.insertResignation(resignation);
        return "success";
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
    // 퇴사 관련 로직 end-point
}
