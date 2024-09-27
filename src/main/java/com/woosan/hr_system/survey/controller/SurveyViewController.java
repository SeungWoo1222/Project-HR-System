package com.woosan.hr_system.survey.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/survey")
public class SurveyViewController {

    @GetMapping("/list") // 설문조사 목록
    public String viewSurveyList(Model model) {
        return "survey/list";
    }

    @GetMapping("/form") // 설문조사 등록 폼
    public String viewSurveyForm(Model model) {
        return "survey/form";
    }
}
