package com.woosan.hr_system.attendance.dao;

import com.woosan.hr_system.attendance.model.Attendance;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class AttendanceDAO {
    @Autowired
    private SqlSession sqlSession;

    // 근태 ID를 통한 근태 상세 조회
    public Attendance getAttendanceById(int attendanceId) {
        return sqlSession.selectOne("attendance.getAttendanceById", attendanceId);
    }

    // 사원 ID를 통한 해당 사원의 근태 목록 조회
    public List<Attendance> getAttendanceByEmployeeId(String employeeId) {
        return sqlSession.selectList("attendance.getAttendanceByEmployeeId", employeeId);
    }

    // 모든 근태 목록 조회
    public List<Attendance> getAllAttendance() {
        return sqlSession.selectList("attendance.getAllAttendance");
    }

    // 금일 근태 현황 조회
    public List<Attendance> getTodayAttendance() {
        return sqlSession.selectList("attendance.getTodayAttendance", LocalDate.now());
    }

    // 근태 등록
    public void insertAttendance(Attendance attendance) {
        sqlSession.insert("attendance.insertAttendance", attendance);
    }

    // 퇴근 시간 등록
    public void updateCheckout(Map<String, Object> params) {
        sqlSession.update("attendance.updateCheckout", params);
    }

    // 조퇴 처리
    public void updateEarlyLeave(Map<String, Object> params) {
        sqlSession.update("attendance.updateEarlyLeave", params);
    }

    // 근태 수정
    public void updateAttendance(Attendance updatedAttendance) {
        sqlSession.update("attendance.updateAttendance", updatedAttendance);
    }

    // 금일 근태 ID 조회
    public int getMyTodayAttendance(Map<String, Object> params) {
        return sqlSession.selectOne("attendance.getMyTodayAttendance", params);
    }

    // 근태 목록 검색 조회
    public List<Attendance> searchAttendance(Map<String, Object> params) {
        return sqlSession.selectList("attendance.searchAttendance", params);
    }

    // 사원의 이번 주 근무시간 조회
    public float getTotalWeeklyWorkingTime(String employeeId, LocalDate date) {
        Map<String, Object> param = new HashMap<>();
        param.put("employeeId", employeeId);
        param.put("date", date);
        return sqlSession.selectOne("attendance.getTotalWeeklyWorkingTime", param);
    }
}
