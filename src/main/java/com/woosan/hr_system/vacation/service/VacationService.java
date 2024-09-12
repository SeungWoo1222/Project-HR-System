package com.woosan.hr_system.vacation.service;

import com.woosan.hr_system.search.PageRequest;
import com.woosan.hr_system.search.PageResult;
import com.woosan.hr_system.vacation.model.Vacation;

import java.util.List;

public interface VacationService {
    Vacation getVacationById(int vacationId);
    PageResult<Vacation> getVacationByEmployeeId(PageRequest pageRequest, String employeeId);
    List<Vacation> getVacationByDepartmentId(String departmentId);
    String requestVacation(Vacation vacation);
    String editVacation(Vacation vacation);
    String processVacation(int vacationId, String status);
    String deleteVacation(int vacationId);
}
