package com.woosan.hr_system.salary.service;

import com.woosan.hr_system.salary.dao.SalaryDAO;
import com.woosan.hr_system.salary.model.Salary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SalaryServiceImpl implements SalaryService {

    @Autowired
    private SalaryDAO salaryDAO;

    @Override
    public void addSalary(Salary salary) {
        salaryDAO.insertSalary(salary);
    }

    @Override
    public Salary getSalaryById(int salaryId) {
        return salaryDAO.selectSalaryById(salaryId);
    }

    @Override
    public List<Salary> getAllSalaries() {
        return salaryDAO.selectAllSalaries();
    }

    @Override
    public void updateSalary(Salary salary) {
        salaryDAO.updateSalary(salary);
    }

    @Override
    public void removeSalary(int salaryId) {
        salaryDAO.deleteSalary(salaryId);
    }
}
