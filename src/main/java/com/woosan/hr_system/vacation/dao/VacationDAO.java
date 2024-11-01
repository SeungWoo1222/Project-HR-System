package com.woosan.hr_system.vacation.dao;

import com.woosan.hr_system.vacation.model.Vacation;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

public interface VacationDAO {
    Vacation selectVacationById(int vacationId);
    List<Vacation> searchVacation(HashMap<String, Object> params);
    int countVacation(HashMap<String, Object> params);
    List<Vacation> selectVacationsByEmployeeId(HashMap<String, Object> params);
    int countVacationsByEmployeeId(HashMap<String, Object> params);
    List<Vacation> getVacationsByEmployeeId(String employeeId);
    List<Vacation> selectVacationsByDepartmentId(HashMap<String, Object> params);
    int countVacationsByDepartmentId(HashMap<String, Object> params);
    void insertVacation(Vacation vacation);
    void updateVacation(Vacation vacation);
    void approveVacation(Vacation updatedVacation);
    void deleteVacation(int vacationId);
    List<Vacation> getEmployeesOnVacationToday(LocalDate today);
}
