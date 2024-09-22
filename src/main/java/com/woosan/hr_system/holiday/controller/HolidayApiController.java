package com.woosan.hr_system.holiday.controller;

import com.woosan.hr_system.holiday.model.Holiday;
import com.woosan.hr_system.holiday.service.HolidayServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.Year;

@RestController
@RequestMapping("/api/holiday")
public class HolidayApiController {
    @Autowired
    private HolidayServiceImpl holidayService;

    @PostMapping("/{year}") // 해당 연도 공휴일 생성
    public String createHolidays(@PathVariable("year") Year year) {
        return holidayService.createThisYearHolidays(year);
    }

    @PostMapping("/add") // 공휴일 등록
    public String addHolidays(@ModelAttribute Holiday holiday) {
        return holidayService.addHoliday(holiday);
    }

    @PutMapping // 공휴일 수정
    public String editHolidays(@ModelAttribute Holiday holiday) {
        return holidayService.editHoliday(holiday);
    }

    @DeleteMapping("/{holidayId}") // 공휴일 삭제
    public String deleteHolidays(@PathVariable("holidayId") int holidayId) {
        return holidayService.deleteHoliday(holidayId);
    }
}
