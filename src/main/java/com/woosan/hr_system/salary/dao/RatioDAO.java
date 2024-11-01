package com.woosan.hr_system.salary.dao;

import com.woosan.hr_system.salary.model.DeductionDetails;
import com.woosan.hr_system.salary.model.PayrollDetails;
import java.util.Map;

public interface RatioDAO {
    PayrollDetails selectPayrollRatios();
    void updatePayrollRatios(PayrollDetails payrollRatios);
    DeductionDetails selectDeductionRatios();
    void updateDeductionRatios(DeductionDetails deductionRatios);
    int selectIncomeTax(Map<String, Object> map);
    int selectIncomeTaxFor100Million(Map<String, Object> map);
}
