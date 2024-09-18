package com.woosan.hr_system.schedule.dao;

import com.woosan.hr_system.schedule.model.Schedule;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ScheduleDAO {
    @Autowired
    private SqlSession sqlSession;
    private static final String NAMESPACE = "com.woosan.hr_system.schedule.dao.ScheduleDAO.";

    // 일정 생성
    public void insertSchedule(Schedule schedule) {
        sqlSession.insert(NAMESPACE + "createSchedule", schedule);
    }

    // 일정 조회 (ID로)
    public Schedule getScheduleById(int taskId) {
        return sqlSession.selectOne(NAMESPACE + "getScheduleById", taskId);
    }

    // 모든 일정 조회
    public List<Schedule> getAllSchedules() {
        return sqlSession.selectList(NAMESPACE + "getAllSchedules");
    }

    // 일정 업데이트
    public void updateSchedule(Schedule schedule) {
        sqlSession.update(NAMESPACE + "updateSchedule", schedule);
    }

    // 일정 삭제
    public void deleteSchedule(int taskId) {
        sqlSession.delete(NAMESPACE + "deleteSchedule", taskId);
    }
}
