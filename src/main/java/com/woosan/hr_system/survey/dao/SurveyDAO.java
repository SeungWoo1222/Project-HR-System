package com.woosan.hr_system.survey.dao;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class SurveyDAO {
    @Autowired
    private SqlSession sqlSession;
}
