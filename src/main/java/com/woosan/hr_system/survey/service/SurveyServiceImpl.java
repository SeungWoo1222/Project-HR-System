package com.woosan.hr_system.survey.service;

import com.woosan.hr_system.auth.service.AuthService;
import com.woosan.hr_system.survey.dao.SurveyDAO;
import com.woosan.hr_system.survey.model.Question;
import com.woosan.hr_system.survey.model.Survey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class SurveyServiceImpl implements SurveyService {
    @Autowired
    private SurveyDAO surveyDAO;
    @Autowired
    private AuthService authService;

    @Transactional
    @Override // 새로운 설문 등록
    public String submitSurvey(Survey survey) {
        // 설문 정보 부분 등록
        int surveyId = addSurvey(survey);
        log.debug("survey id : {}", surveyId);

        // 질문 부분 등록
        addQuestion(survey.getQuestions(), surveyId);

        return "새로운 설문조사가 등록되었습니다.";
    }

    // 설문 정보 등록
    private int addSurvey(Survey survey) {
        Survey newSurvey = survey.toBuilder()
                .title(survey.getTitle())
                .description(survey.getDescription())
                .createdBy(authService.getAuthenticatedUser().getNameWithId())
                .createdAt(LocalDateTime.now())
                .expiresAt(survey.getExpiresAt())
                .build();
        return surveyDAO.insertSurvey(newSurvey);
    }

    // 질문 부분 등록
    private void addQuestion(List<Question> questions, int surveyId) {

    }

}
