package com.woosan.hr_system.survey.controller;

import com.woosan.hr_system.search.PageRequest;
import com.woosan.hr_system.search.PageResult;
import com.woosan.hr_system.survey.model.Participant;
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

    @GetMapping("/list") // 설문조사 목록 검색 조회
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
        model.addAttribute("survey", survey);

        // 설문 작성자 사원 ID 이름과 분리 후 모델에 추가
        model.addAttribute("createdBy", extractEmployeeId(survey.getCreatedBy()));
        return "survey/detail";
    }

    @GetMapping("/participants") // 설문조사 응답 참여자 조회
    public String viewSurveyParticipant(@RequestParam(name = "page", defaultValue = "1") int page,
                                        @RequestParam(name = "surveyId") int surveyId,
                                        Model model) {
        int size = 10;

        PageRequest pageRequest = new PageRequest(page - 1, size); // 페이지 번호 인덱싱을 위해 다시 -1
        PageResult<Participant> pageResult = surveyService.searchParticipants(pageRequest, surveyId);

        model.addAttribute("participants", pageResult.getData());
        model.addAttribute("currentPage", pageResult.getCurrentPage() + 1); // 뷰에서 가독성을 위해 +1
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("surveyId", surveyId);

        // 설문 작성자 사원 ID 이름과 분리 후 모델에 추가
        Survey survey = surveyService.getSurveyById(surveyId);
        model.addAttribute("createdBy", extractEmployeeId(survey.getCreatedBy()));

        return "survey/participants";
    }

    // 이름(사원ID)에서 사원 ID 추출하는 함수
    private String extractEmployeeId(String fullNameWithId) {
        String[] parts = fullNameWithId.split("\\(");
        return parts[1].replace(")", "");
    }

    @GetMapping("/response") // 응답이 포함된 설문조사 조회
    public String viewSurveyResponse(@RequestParam("surveyId") int surveyId,
                                     @RequestParam("employeeId") String employeeId,
                                     Model model) {
        // 설문 참여자 정보 조회 후 모델에 추가
        model.addAttribute("info", surveyService.getParticipantInfo(surveyId, employeeId));

        // 설문 정보 조회 후 모델에 추가
        Survey survey = surveyService.getSurveyWithResponse(surveyId, employeeId);
        model.addAttribute("survey", survey);
        model.addAttribute("createdBy", extractEmployeeId(survey.getCreatedBy()));
        return "survey/response";
    }
}
