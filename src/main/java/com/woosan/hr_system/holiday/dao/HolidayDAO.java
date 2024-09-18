package com.woosan.hr_system.holiday.dao;

import com.woosan.hr_system.holiday.model.Holiday;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.YearMonth;
import java.util.List;

@Repository
public class HolidayDAO {
    @Autowired
    private SqlSession sqlSession;

    // 해당 년도 공휴일 조회
    public List<Holiday> getHolidayByYear(int year) {
        return sqlSession.selectList("holiday.getHolidayByYear", year);
    }

    // 해당 년월 공휴일 조회
    public List<Holiday> getHolidayByYearMonth(YearMonth yearMonth) {
        return sqlSession.selectList("holiday.getHolidayByYearMonth", yearMonth);
    }

    // 모든 공휴일 조회
    public List<Holiday> getAllHoliday() {
        return sqlSession.selectList("holiday.getAllHoliday");
    }

    // 공휴일 ID를 이용한 공휴일 조회
    public Holiday getHolidayById(int holidayId) {
        return sqlSession.selectOne("holiday.getHolidayById", holidayId);
    }

    // 공휴일 리스트 일괄 등록
    public void insertHolidayList(List<Holiday> holidayList) {
        sqlSession.insert("holiday.insertHolidayList", holidayList);
    }

    // 공휴일 등록
    public void insertHoliday(Holiday holiday) {
        sqlSession.insert("holiday.insertHoliday", holiday);
    }

    // 공휴일 수정
    public void updateHoliday(Holiday holiday) {
        sqlSession.update("holiday.updateHoliday", holiday);
    }

    // 공휴일 삭제
    public void deleteHoliday(int holidayId) {
        sqlSession.delete("holiday.deleteHoliday", holidayId);
    }
}
