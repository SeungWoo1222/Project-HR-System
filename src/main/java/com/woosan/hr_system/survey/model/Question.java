package com.woosan.hr_system.survey.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question {
    private int id;
    private int surveyId;
    private String questionText;
    private String questionType;
    private List<String> options;
}
