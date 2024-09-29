package com.woosan.hr_system.survey.controller;

import com.woosan.hr_system.survey.model.Survey;
import com.woosan.hr_system.survey.service.SurveyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/survey")
public class SurveyApiController {
    @Autowired
    private SurveyService surveyService;

    @PostMapping // 새로운 설문 등록
    public ResponseEntity<String> submitSurvey(@RequestBody Survey survey) {
        log.debug("{}", survey);

        // 처리 후 성공 응답 반환
        return new ResponseEntity<>(surveyService.submitSurvey(survey), HttpStatus.OK);
    }
}
