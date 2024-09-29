package com.woosan.hr_system.survey.dao;

import com.woosan.hr_system.survey.model.Survey;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class SurveyDAO {
    @Autowired
    private SqlSession sqlSession;

    // 설문 등록
    public int insertSurvey(Survey survey) {
        sqlSession.insert("survey.insertSurvey", survey);
        return survey.getId();
    }
}
