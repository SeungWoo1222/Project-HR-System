package com.woosan.hr_system.salary.controller.api;

import com.woosan.hr_system.auth.aspect.RequireHRPermission;
import com.woosan.hr_system.salary.model.SalaryPayment;
import com.woosan.hr_system.salary.service.SalaryPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/salary/payment/")
public class SalaryPaymentApiController {
    @Autowired
    private SalaryPaymentService salaryPaymentService;

    @RequireHRPermission
    @PostMapping("register")
    ResponseEntity<String> registerPayment(@RequestBody SalaryPayment salaryPayment) {
        return ResponseEntity.ok(salaryPaymentService.addPayment(salaryPayment));
    }

    @RequireHRPermission
    @PutMapping("/{paymentId}")
    ResponseEntity<String> updatePayment(@RequestBody SalaryPayment salaryPayment, @PathVariable int paymentId) {
        return ResponseEntity.ok(salaryPaymentService.updatePayment(salaryPayment, paymentId));
    }

    @RequireHRPermission
    @DeleteMapping("/{paymentId}")
    ResponseEntity<String> deletePayment(@PathVariable int paymentId) {
        return ResponseEntity.ok(salaryPaymentService.removePayment(paymentId));
    }
}
