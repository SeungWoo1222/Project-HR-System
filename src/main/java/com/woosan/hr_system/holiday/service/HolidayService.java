package com.woosan.hr_system.holiday.service;

import com.woosan.hr_system.holiday.model.Holiday;

import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.util.List;

public interface HolidayService {
    Holiday getHolidayById(int holidayId);
    List<Holiday> getHolidayByYear(Year year);
    List<Holiday> getHolidayByYearMonth(YearMonth yearMonth);
    List<Holiday> getAllHoliday();
    String addHoliday(Holiday holiday);
    String editHoliday(Holiday holiday);
    String deleteHoliday(int holidayId);
    boolean isHoliday(LocalDate date);
    String createThisYearHolidays(Year year);
}
