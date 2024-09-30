package com.woosan.hr_system.survey.controller;

import com.woosan.hr_system.survey.model.Response;
import com.woosan.hr_system.survey.model.Survey;
import com.woosan.hr_system.survey.service.SurveyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/survey")
public class SurveyApiController {
    @Autowired
    private SurveyService surveyService;

    @PostMapping // 새로운 설문 제출
    public ResponseEntity<String> submitSurvey(@RequestBody Survey survey) {
        return ResponseEntity.ok(surveyService.submitSurvey(survey));
    }

    @PostMapping("/response") // 설문 응답 제출
    public ResponseEntity<String> submitResponse(@RequestBody List<Response> responses) {
        log.debug("{}", responses);
        return ResponseEntity.ok("");
    }
}
