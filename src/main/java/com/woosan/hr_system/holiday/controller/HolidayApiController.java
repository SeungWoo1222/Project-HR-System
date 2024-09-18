package com.woosan.hr_system.holiday.controller;

import com.woosan.hr_system.holiday.service.HolidayServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/holiday")
public class HolidayApiController {
    @Autowired
    private HolidayServiceImpl holidayService;
}
