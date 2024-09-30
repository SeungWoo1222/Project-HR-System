package com.woosan.hr_system.survey.controller;

import com.woosan.hr_system.search.PageRequest;
import com.woosan.hr_system.search.PageResult;
import com.woosan.hr_system.survey.model.Survey;
import com.woosan.hr_system.survey.service.SurveyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequestMapping("/survey")
public class SurveyViewController {
    @Autowired
    private SurveyService surveyService;

    @GetMapping("/list") // 설문조사 목록 조회
    public String viewSurveyList(@RequestParam(name = "page", defaultValue = "1") int page,
                                 @RequestParam(name = "size", defaultValue = "10") int size,
                                 @RequestParam(name = "keyword", defaultValue = "") String keyword,
                                 @RequestParam(name = "status", defaultValue = "") String status,
                                 Model model) {
        PageRequest pageRequest = new PageRequest(page - 1, size, keyword); // 페이지 번호 인덱싱을 위해 다시 -1
        PageResult<Survey> pageResult = surveyService.searchSurvey(pageRequest, status);

        model.addAttribute("surveys", pageResult.getData());
        model.addAttribute("currentPage", pageResult.getCurrentPage() + 1); // 뷰에서 가독성을 위해 +1
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);

        return "survey/list";
    }

    @GetMapping("/form") // 설문조사 등록 폼
    public String viewSurveyForm(Model model) {
        return "survey/form";
    }

    @GetMapping("/{id}") // 설문조사 조회
    public String viewSurvey(@PathVariable("id") int id, Model model) {
        Survey survey = surveyService.getSurveyById(id);
        log.debug("survey: {}", survey);
        model.addAttribute("survey", survey);

        // 설문 작성자 사원 ID 이름과 분리 후 모델에 추가
        String[] createdBy = survey.getCreatedBy().split("\\(");
        model.addAttribute("createdBy", createdBy[1].replace(")", ""));
        return "survey/detail";
    }
}
