package com.woosan.hr_system.survey.dao;

import com.woosan.hr_system.survey.model.Participant;
import com.woosan.hr_system.survey.model.Question;
import com.woosan.hr_system.survey.model.Response;
import com.woosan.hr_system.survey.model.Survey;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class SurveyDAO {
    @Autowired
    private SqlSession sqlSession;

    // 설문 등록
    public int insertSurvey(Survey survey) {
        sqlSession.insert("survey.insertSurvey", survey);
        return survey.getId();
    }

    // 질문 등록
    public int insertQuestion(Question newQuestion) {
        sqlSession.insert("survey.insertQuestion", newQuestion);
        return newQuestion.getId();
    }

    // 옵션 등록
    public void insertOption(Map<String, Object> map) {
        sqlSession.insert("survey.insertOption", map);
    }

    // 설문 검색 조회
    public List<Survey> searchSurvey(String keyword, int size, int offset, String status) {
        Map<String, Object> map = new HashMap<>();
        map.put("keyword", keyword);
        map.put("size", size);
        map.put("offset", offset);
        map.put("status", status);
        return sqlSession.selectList("survey.searchSurvey", map);
    }

    // ID를 이용한 설문 조회
    public Survey selectSurveyById(int id) {
        return sqlSession.selectOne("survey.selectSurveyById", id);
    }

    // 설문 응답 등록
    public void insertResponse(Response newResponse) {
        sqlSession.insert("survey.insertResponse", newResponse);
    }

    // 설문 참여자 등록
    public void insertParticipant(String employeeId, int surveyId) {
        Map<String, Object> map = new HashMap<>();
        map.put("employeeId", employeeId);
        map.put("surveyId", surveyId);
        sqlSession.insert("survey.insertParticipant", map);
    }

    // 설문 참여자 조회
    public List<String> selectParticipantIds(int surveyId) {
        return sqlSession.selectList("survey.selectParticipantIds", surveyId);
    }

    // 설문 응답 참여자 검색 조회
    public List<Participant> searchParticipants(int size, int offset, int surveyId) {
        Map<String, Object> map = new HashMap<>();
        map.put("size", size);
        map.put("offset", offset);
        map.put("surveyId", surveyId);
        return sqlSession.selectList("survey.searchParticipants", map);
    }

    // 설문 응답 조회
    public List<Response> selectResponses(int surveyId, String employeeId) {
        Map<String, Object> map = new HashMap<>();
        map.put("surveyId", surveyId);
        map.put("employeeId", employeeId);
        return sqlSession.selectList("survey.selectResponses", map);
    }

    // 설문 참여자 정보 조회
    public Participant selectParticipantInfo(int surveyId, String employeeId) {
        Map<String, Object> map = new HashMap<>();
        map.put("surveyId", surveyId);
        map.put("employeeId", employeeId);
        return sqlSession.selectOne("survey.selectParticipationInfo", map);
    }
}
