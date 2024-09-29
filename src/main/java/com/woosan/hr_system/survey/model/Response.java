package com.woosan.hr_system.survey.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Response {
    private int id;
    private int surveyId;
    private int questionId;
    private String employeeId;
    private String answer;
}
