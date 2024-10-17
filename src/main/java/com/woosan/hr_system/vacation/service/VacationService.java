package com.woosan.hr_system.vacation.service;

import com.woosan.hr_system.search.PageRequest;
import com.woosan.hr_system.search.PageResult;
import com.woosan.hr_system.vacation.model.Vacation;

import java.util.List;

public interface VacationService {
    Vacation getVacationById(int vacationId);
    PageResult<Vacation> searchVacation(PageRequest pageRequest, String department, String status, String startDate, String endDate);
    PageResult<Vacation> getVacationsByEmployeeId(PageRequest pageRequest, String employeeId, String startDate, String endDate);
    List<Vacation> getVacationsByEmployeeId(String employeeId);
    PageResult<Vacation> getVacationsByDepartmentId(PageRequest pageRequest, String departmentId, String status, String startDate, String endDate);
    String requestVacation(Vacation vacation);
    String editVacation(Vacation vacation);
    String processVacation(int vacationId, String status);
    String deleteVacation(int vacationId);
    List<Vacation> findEmployeesOnVacationToday();
}
