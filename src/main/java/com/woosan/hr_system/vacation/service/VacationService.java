package com.woosan.hr_system.vacation.service;

import com.woosan.hr_system.vacation.model.Vacation;

import java.util.List;

public interface VacationService {
    Vacation getVacationById(int vacationId);
    List<Vacation> getVacationByEmployeeId(String employeeId);
    List<Vacation> getVacationByDepartmentId(String departmentId);
    String requestVacation(Vacation vacation);
    String editVacation(Vacation vacation);
    String processVacation(int vacationId, String status);
}
