package com.woosan.hr_system.resignation.dao;

import com.woosan.hr_system.resignation.model.Resignation;

import java.util.List;

public interface ResignationDAO {
    List<Resignation> getAllResignedEmployees();
    Resignation getResignedEmployee(String employeeId);
    void insertResignation(Resignation resignation);
    void updateResignation(Resignation resignation);
    void deleteResignation(String employeeId);
}
