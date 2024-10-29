package com.woosan.hr_system.holiday.dao;

import com.woosan.hr_system.holiday.model.Holiday;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.util.List;

public interface HolidayDAO {
    Holiday getHolidayById(int holidayId);
    List<Holiday> getHolidayByYear(Year year);
    List<Holiday> getHolidayByYearMonth(YearMonth yearMonth);
    List<Holiday> getAllHoliday();
    void insertHolidayList(List<Holiday> holidayList);
    void insertHoliday(Holiday holiday);
    void updateHoliday(Holiday holiday);
    void deleteHoliday(int holidayId);
    int isHoliday(LocalDate date);
}
