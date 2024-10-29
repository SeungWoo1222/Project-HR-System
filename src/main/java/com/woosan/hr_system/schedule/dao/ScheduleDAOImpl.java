package com.woosan.hr_system.schedule.dao;

import com.woosan.hr_system.schedule.model.Schedule;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class ScheduleDAOImpl implements ScheduleDAO {
    @Autowired
    private SqlSession sqlSession;

    private static final String NAMESPACE = "schedule.";

    // 일정 생성
    public int insertSchedule(Schedule schedule) {
        sqlSession.insert(NAMESPACE + "createSchedule", schedule);
        return schedule.getTaskId();
    }

    // 일정 조회 (ID로)
    public Schedule getScheduleById(int taskId) {
        return sqlSession.selectOne(NAMESPACE + "getScheduleById", taskId);
    }

    // 사원의 모든 일정 조회
    public List<Schedule> getSchedulesByEmployeeId(String employeeId) {
        return sqlSession.selectList(NAMESPACE + "getSchedulesByEmployeeId", employeeId);
    }

    // 모든 일정 조회
    public List<Schedule> getAllSchedules() {
        return sqlSession.selectList(NAMESPACE + "getAllSchedules");
    }

    // 일정 업데이트
    public void updateSchedule(Schedule schedule) {
        sqlSession.update(NAMESPACE + "updateSchedule", schedule);
    }

    // 일정 상태 변경
    public void updateScheduleStatus(int taskId, String status) {
        log.info("status : {}", status);
        Map<String, Object> params = new HashMap<>();
        params.put("taskId", taskId);
        params.put("status", status);
        sqlSession.update(NAMESPACE + "updateScheduleStatus", params);
    }

    // 일정 삭제
    public void deleteSchedule(int taskId) {
        sqlSession.delete(NAMESPACE + "deleteSchedule", taskId);
    }

    // 일정 아카이브 추가
    public void insertScheduleArchive(Schedule schedule) {
        sqlSession.insert(NAMESPACE + "insertScheduleArchive", schedule);
    }
}
