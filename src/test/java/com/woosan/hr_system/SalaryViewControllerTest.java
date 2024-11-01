package com.woosan.hr_system;

import com.woosan.hr_system.salary.controller.view.SalaryViewController;
import com.woosan.hr_system.salary.service.SalaryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

@SpringBootTest
public class SalaryViewControllerTest {

    @Autowired
    private SalaryViewController salaryViewController;

    @Autowired
    private SalaryService salaryService;

    @Test
    public void testSalaryServiceInjection() {
        Assert.notNull(salaryViewController, "SalaryViewController should not be null");
        Assert.notNull(salaryService, "SalaryService should not be null");

        // 추가로, SalaryViewController에 주입된 salaryService가 올바르게 초기화되었는지 확인
//        Assert.isTrue(salaryViewController.salaryService != null, "salaryService should be injected in SalaryViewController");
    }
}
