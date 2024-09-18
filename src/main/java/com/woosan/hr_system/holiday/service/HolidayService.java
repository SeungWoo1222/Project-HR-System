package com.woosan.hr_system.holiday.service;

import com.woosan.hr_system.holiday.model.Holiday;

import java.time.YearMonth;
import java.util.List;

public interface HolidayService {
    List<Holiday> getHolidayByYear(int year);
    List<Holiday> getHolidayByYearMonth(YearMonth yearMonth);
    List<Holiday> getAllHoliday();
    String addHoliday(Holiday holiday);
    String editHoliday(Holiday holiday);
    String deleteHoliday(int holidayId);
}
