package com.woosan.hr_system.survey.service;

import com.woosan.hr_system.search.PageRequest;
import com.woosan.hr_system.search.PageResult;
import com.woosan.hr_system.survey.model.Response;
import com.woosan.hr_system.survey.model.Survey;

import java.util.List;

public interface SurveyService {
    Survey getSurveyById(int id);
    PageResult<Survey> searchSurvey(PageRequest pageRequest, String status);
    String submitSurvey(Survey survey);
    String updateSurvey(Survey survey);
    String submitResponse(List<Response> responses);
    List<String> selectParticipantIds(int surveyId);
}
