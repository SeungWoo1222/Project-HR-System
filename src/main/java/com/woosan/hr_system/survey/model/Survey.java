package com.woosan.hr_system.survey.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Survey {
    private int id;
    private String title;
    private String description;
    private List<Question> questions;
    private String created_by;
    private LocalDateTime created_at;
}
