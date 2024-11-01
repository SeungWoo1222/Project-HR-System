package com.woosan.hr_system.survey.model;

import com.woosan.hr_system.employee.model.Employee;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Participant {
    private int surveyId;
    private String employeeId;
    private LocalDateTime participationAt;

    private Employee employee;
}
