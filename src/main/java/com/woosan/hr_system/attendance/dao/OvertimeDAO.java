package com.woosan.hr_system.attendance.dao;

import com.woosan.hr_system.attendance.model.Overtime;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class OvertimeDAO {
    @Autowired
    private SqlSession sqlSession;

    // ID를 이용한 초과근무 조회
    public Overtime getOvertimeById(int overtimeId) {
        return sqlSession.selectOne("overtime.getOvertimeById", overtimeId);
    }

    // 모든 초과근무 조회
    public List<Overtime> getAllOvertimes() {
        return sqlSession.selectList("overtime.getAllOvertimes");
    }

    // 사원의 이번 달 초과근무 총 시간 조회
    public List<Overtime> getThisMonthOvertimes(String employeeId, YearMonth yearMonth) {
        Map<String, Object> param = new HashMap<>();
        param.put("employeeId", employeeId);
        param.put("yearMonth", yearMonth);
        return sqlSession.selectList("overtime.getThisMonthOvertimes", param);
    }

    // 사원의 이번 주 초과근무 총 시간 조회
    public float getTotalWeeklyOvertime(String employeeId, LocalDate date) {
        Map<String, Object> param = new HashMap<>();
        param.put("employeeId", employeeId);
        param.put("date", date);
        return sqlSession.selectOne("overtime.getTotalWeeklyOvertime", param);
    }

    // 사원의 이번 주 야간근무 총 시간 조회
    public float getTotalWeeklyNightOvertime(String employeeId, LocalDate date) {
        Map<String, Object> param = new HashMap<>();
        param.put("employeeId", employeeId);
        param.put("date", date);
        return sqlSession.selectOne("overtime.getTotalWeeklyNightOvertime", param);
    }

    // 초과근무 등록
    public void insertOvertime(Overtime overtime, int attendanceId) {
        int overtimeId = sqlSession.insert("overtime.insertOvertime", overtime);

        // 근태 정보에 초과근무 ID 연결
        Map<String, Object> param = new HashMap<>();
        param.put("overtimeId", overtimeId);
        param.put("attendanceId", attendanceId);
        sqlSession.update("overtime.linkOvertimeToAttendance", param);
    }

    // 초과근무 수정
    public void updateOvertime(Overtime overtime) {
        sqlSession.update("overtime.updateOvertime", overtime);
    }

    // 초과근무 삭제
    public void deleteOvertime(int overtimeId) {
        sqlSession.delete("overtime.deleteOvertime", overtimeId);
    }
}
