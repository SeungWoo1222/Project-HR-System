package com.woosan.hr_system.vacation.service;

import com.woosan.hr_system.vacation.model.Vacation;

import java.util.List;

public interface VacationService {
    Vacation getVacationById(int vacationId);
    List<Vacation> getVacationByEmployeeId(String employeeId);
    List<Vacation> getVacationByDepartmentId(String departmentId);
    List<Vacation> getAllVacation();
    String addVacation(Vacation vacation);
    String updateVacation(Vacation vacation);
    String approveVacation(int vacationId, String status);
}
