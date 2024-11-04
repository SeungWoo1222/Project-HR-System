package com.woosan.hr_system.survey.dao;

import com.woosan.hr_system.survey.model.Participant;
import com.woosan.hr_system.survey.model.Question;
import com.woosan.hr_system.survey.model.Response;
import com.woosan.hr_system.survey.model.Survey;
import java.util.List;
import java.util.Map;

public interface SurveyDAO {
    int insertSurvey(Survey survey);
    int insertQuestion(Question newQuestion);
    void insertOption(Map<String, Object> map);
    List<Survey> searchSurvey(String keyword, int size, int offset, String status);
    Survey selectSurveyById(int id);
    void insertResponse(Response newResponse);
    void insertParticipant(String employeeId, int surveyId);
    List<String> selectParticipantIds(int surveyId);
    List<Participant> searchParticipants(int size, int offset, int surveyId);
    List<Response> selectResponses(int surveyId, String employeeId);
    Participant selectParticipantInfo(int surveyId, String employeeId);
    void deleteSurvey(int id);
    List<Response> selectResponses(int surveyId, int questionId);
    List<Survey> selectInvestigatingSurvey();
    void closeSurvey(int id);
    List<Survey> getAllSurvey();
}
