package com.woosan.hr_system.salary.service;

import com.woosan.hr_system.auth.aspect.LogAfterExecution;
import com.woosan.hr_system.auth.aspect.LogBeforeExecution;
import com.woosan.hr_system.common.service.CommonService;
import com.woosan.hr_system.employee.dao.EmployeeDAO;
import com.woosan.hr_system.employee.model.Employee;
import com.woosan.hr_system.exception.salary.SalaryNotFoundException;
import com.woosan.hr_system.salary.dao.SalaryDAO;
import com.woosan.hr_system.salary.model.Salary;
import com.woosan.hr_system.search.PageRequest;
import com.woosan.hr_system.search.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@Component
public class SalaryServiceImpl implements SalaryService {
    @Autowired
    private CommonService commonService;
    @Autowired
    private SalaryDAO salaryDAO;
    @Autowired
    private EmployeeDAO employeeDAO;

    // Salary 객체 Null 검사
    private void checkForNull(Salary salary, Object id) {
        if (salary == null) throw new SalaryNotFoundException(id);
    }

    @Override // 급여 ID를 이용한 특정 사원의 급여 정보 조회
    public Salary getSalaryById(int salaryId) {
        Salary salaryInfo = salaryDAO.selectSalaryById(salaryId);
        checkForNull(salaryInfo, salaryId);
        return salaryInfo;
    }

    @Override // 사원 ID를 이용한 특정 사원의 급여 정보 조회
    public Salary getSalaryByEmployeeId(String employeeId) {
        Salary salaryInfo = salaryDAO.selectSalaryByEmployeeId(employeeId);
        checkForNull(salaryInfo, employeeId);
        return salaryInfo;
    }

    @Override // 사원 ID를 이용한 특정 사원의 급여 ID 리스트 조회
    public List<Integer> getSalaryIdList(String employeeId) {
        return salaryDAO.selectSalaryIdList(employeeId);
    }

    @Override // 급여 정보가 없는 사원 리스트 조회
    public List<Employee> getEmployeeList() {
        return salaryDAO.selectEmployeeList();
    }

    @Override // 모든 사원의 급여 정보 조회 (검색 기능 추가)
    public PageResult<Salary> searchSalaries(PageRequest pageRequest, String department) {
        int offset = pageRequest.getPage() * pageRequest.getSize();
        List<Salary> salaries = salaryDAO.search(pageRequest.getKeyword(), pageRequest.getSize(), offset, department);
        int total = salaryDAO.count(pageRequest.getKeyword());

        return new PageResult<>(salaries, (int) Math.ceil((double) total / pageRequest.getSize()), total, pageRequest.getPage());
    }

    @Override // 모든 사원의 급여 정보 조회
    public List<Salary> getAllSalaries() {
        return salaryDAO.selectAllSalaries();
    }

    @LogBeforeExecution
    @LogAfterExecution
    @Override // 사원 급여 정보 등록
    public String addSalary(Salary salary) {
        salaryDAO.insertSalary(salary);
        return "'" + employeeDAO.getEmployeeName(salary.getEmployeeId()) + "' 사원의 급여 정보가 등록되었습니다.";
    }

    @LogBeforeExecution
    @LogAfterExecution
    @Override // 사원 급여 정보 수정
    public String updateSalary(Salary updatedSalary, String employeeId) {
        // 변경 사항 확인
        Salary originalSalary = getSalaryByEmployeeId(employeeId);
        checkForSalaryChanges(originalSalary, updatedSalary);

        // 급여 정보 수정
        salaryDAO.updateSalary(updatedSalary);
        return "'" + employeeDAO.getEmployeeName(employeeId) + "' 사원의 급여 정보가 수정되었습니다.";
    }

    // Salary의 특정 필드만 비교하도록 필드 이름 Set으로 전달하는 메소드
    private void checkForSalaryChanges(Salary original, Salary updated) {
        Set<String> fieldsToCompare = new HashSet<>(Arrays.asList(
                "salaryId", "employeeId", "annualSalary", "bank", "accountNumber"
        ));
        commonService.processFieldChanges(original, updated, fieldsToCompare);
    }

    @LogBeforeExecution
    @LogAfterExecution
    @Override // 사원 급여 정보 삭제
    public String removeSalary(int salaryId) {
        // 급여 정보 확인
        Salary salaryInfo = getSalaryById(salaryId);
        checkForNull(salaryInfo, salaryId);

        // 급여 정보 삭제
        salaryDAO.deleteSalary(salaryId);
        return "'" + employeeDAO.getEmployeeName(salaryInfo.getEmployeeId()) + "' 사원의 급여 정보가 삭제되었습니다.";
    }
}
