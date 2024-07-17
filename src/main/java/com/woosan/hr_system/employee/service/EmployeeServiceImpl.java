package com.woosan.hr_system.employee.service;

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

    @Override // 모든 사원 정보 조회
    public List<Employee> getAllEmployees() {
        return employeeDAO.getAllEmployees();
    }

    @Override // 사원 정보 등록
    public void insertEmployee(Employee employee) {
        // BB
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String BB = employee.getHireDate().format(formatter).substring(2, 4);
        // CCC
        int currentYearEmpolyeesCount = employeeDAO.countEmployeesByCurrentYear();
        String CCC = String.format("%03d", currentYearEmpolyeesCount + 1);
        //  Employee ID 형식 : AABBCCC (부서 코드, 입사년도, 해당 년도 입사 순서)
        employee.setEmployeeId(employee.getDepartment() + BB + CCC);

        // 첫 비밀번호는 생년월일
        employee.setPassword(passwordEncoder.encode(employee.getBirth()));

        // 재직 상태
        employee.setStatus("재직");

        // 연차
        employee.setRemainingLeave(11);

        employeeDAO.insertEmployee(employee);
    }

    @Override // 사원 정보 수정
    public void updateEmployee(Employee employee) {
        employeeDAO.updateEmployee(employee);
    }

    @Override // 사원 정보 일부 수정 (변경 가능한 column - password, name, birth, phone, email, address, detailed_address)
    public void updateEmployeePartial(String employeeId, Map<String, Object> updates) {
        Employee employee = employeeDAO.getEmployeeById(employeeId);
        if (employee != null) {
            if (updates.containsKey("name")) {
                employee.setName((String)updates.get("name"));
            }
            if (updates.containsKey("birth")) {
                employee.setBirth((String)updates.get("birth"));
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
                employee.setDetailedAddress((String)updates.get("detailed_address"));
            }

            employee.setLastModified(LocalDateTime.now());
            // employee.setModifiedBy(세션에서 현재 계정 employee 아이디); > 스프링 세큐리티 작업 후 코드 수정

            employeeDAO.updateEmployee(employee);
        }
    }

    @Override // 사원 정보 삭제
    public void deleteEmployee(String employeeId) {
        employeeDAO.deleteEmployee(employeeId);
        resignationDAO.deleteResignation(employeeId);
    }

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
    public void resignEmployee(String employeeId, String ResignationReason, LocalDate ResignationDate) {
        // 재직 상태 - 퇴사 처리
        Employee employee = employeeDAO.getEmployeeById(employeeId);
        employee.setStatus("퇴사");
        employeeDAO.updateEmployee(employee);

        // 퇴사 테이블에 정보 등록
        Resignation resignation = new Resignation();
        resignation.setEmployeeId(employeeId);
        resignation.setResignationReason(ResignationReason);
        resignation.setResignationDate(ResignationDate);
        resignation.setProcessedDate(LocalDateTime.now());

        // 로그인된 사용자 정보
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            resignation.setProcessedBy(userDetails.getUsername());
        }

        // 파일 첨부 기능 구현 후 추가 예정
        // termination.setTerminationDocuments(terminationDocuments);
        resignationDAO.insertResignation(resignation);
    }


}
