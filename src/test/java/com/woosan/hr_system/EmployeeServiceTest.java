package com.woosan.hr_system;

import com.woosan.hr_system.employee.service.EmployeeService;
import com.woosan.hr_system.resignation.dao.ResignationDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EmployeeServiceTest {

    @Mock
    private ResignationDAO resignationDAO;

    @InjectMocks
    private EmployeeService employeeService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetRemainingResignationDocs() {
        // Given
        // When
        // Then

    }
}
