package com.woosan.hr_system.attendance.dao;

import com.woosan.hr_system.attendance.model.Attendance;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
public class AttendanceDAO {
    @Autowired
    private SqlSession sqlSession;

    private static final String NAMESPACE = "com.woosan.hr_system.attendance.dao.AttendanceDAO.";

    // 근태 ID를 통한 근태 상세 조회
    public Attendance getAttendanceById(int attendanceId) {
        return sqlSession.selectOne(NAMESPACE + "getAttendanceById", attendanceId);
    }

    // 사원 ID를 통한 해당 사원의 근태 목록 조회
    public List<Attendance> getAttendanceByEmployeeId(String employeeId) {
        return sqlSession.selectList(NAMESPACE + "getAttendanceByEmployeeId", employeeId);
    }

    // 모든 근태 목록 조회
    public List<Attendance> getAllAttendance() {
        return sqlSession.selectList(NAMESPACE + "getAllAttendance");
    }

    // 금일 근태 현황 조회
    public List<Attendance> getTodayAttendance() {
        return sqlSession.selectList(NAMESPACE + "getTodayAttendance", LocalDate.now());
    }

    // 근태 목록 검색 조회

    // 부서원의 근태 목록 검색 조회

    // 근태 등록
    public void insertAttendance(Attendance attendance) {
        sqlSession.insert(NAMESPACE + "insertAttendance", attendance);
    }

    // 퇴근 시간 등록
    public void updateCheckout(Map<String, Object> params) {
        sqlSession.update(NAMESPACE + "updateCheckout", params);
    }

    // 조퇴 처리
    public void updateEarlyLeave(Map<String, Object> params) {
        sqlSession.update(NAMESPACE + "updateEarlyLeave", params);
    }

    // 근태 수정
    public void updateAttendance(Attendance updatedAttendance) {
        sqlSession.update(NAMESPACE + "updateAttendance", updatedAttendance);
    }

    // 금일 근태 ID 조회
    public int getMyTodayAttendance(Map<String, Object> params) {
        return sqlSession.selectOne(NAMESPACE + "getMyTodayAttendance", params);
    }

    // 근태 목록 검색 조회
    public List<Attendance> searchAttendance(Map<String, Object> params) {
        return sqlSession.selectList(NAMESPACE + "searchAttendance", params);
    }
}
