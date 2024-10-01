package com.woosan.hr_system.survey.controller;

import com.woosan.hr_system.aspect.RequireManagerPermission;
import com.woosan.hr_system.survey.model.Response;
import com.woosan.hr_system.survey.model.Survey;
import com.woosan.hr_system.survey.service.SurveyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/survey")
public class SurveyApiController {
    @Autowired
    private SurveyService surveyService;

    @RequireManagerPermission
    @PostMapping // 새로운 설문 제출
    public ResponseEntity<String> submitSurvey(@RequestBody Survey survey) {
        return ResponseEntity.ok(surveyService.submitSurvey(survey));
    }

    @PostMapping("/response") // 설문 응답 제출
    public ResponseEntity<String> submitResponse(@RequestBody List<Response> responses) {
        return ResponseEntity.ok(surveyService.submitResponse(responses));
    }

    @RequireManagerPermission
    @DeleteMapping("/{id}") // 설문 삭제
    public ResponseEntity<String> deleteSurvey(@PathVariable("id") int id) {
        return ResponseEntity.ok(surveyService.deleteSurvey(id));
    }
}
