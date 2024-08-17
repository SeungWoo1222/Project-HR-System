package com.woosan.hr_system.salary.controller.api;

import com.woosan.hr_system.salary.service.SalaryCalculation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/salary/payment/")
public class SalaryPaymentApiController {
    @Autowired
    private SalaryCalculation salaryCalculation;
}
