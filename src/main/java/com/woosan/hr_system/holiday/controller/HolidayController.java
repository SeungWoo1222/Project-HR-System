package com.woosan.hr_system.holiday.controller;

import com.woosan.hr_system.holiday.service.HolidayServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/holiday")
public class HolidayController {
    @Autowired
    private HolidayServiceImpl holidayService;
}
