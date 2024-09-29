package com.woosan.hr_system.survey.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Question {
    private int id;
    private String questionText;
    private String questionType;
    private List<Integer> options;
}
