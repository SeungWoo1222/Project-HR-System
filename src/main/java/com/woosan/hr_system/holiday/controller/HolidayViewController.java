package com.woosan.hr_system.holiday.controller;

import com.woosan.hr_system.aspect.RequireHRPermission;
import com.woosan.hr_system.aspect.RequireManagerPermission;
import com.woosan.hr_system.holiday.service.HolidayServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Year;

@Controller
@RequestMapping("/holiday")
public class HolidayViewController {
    @Autowired
    private HolidayServiceImpl holidayService;

    @GetMapping("/list") // 공휴일 관리 페이지 조회
    public String viewHolidayManagement(@RequestParam(name = "year", defaultValue = "") String yearString,
                                        Model model) {
        // 검색 년도 설정
        Year year;
        if (yearString.isEmpty()) {
            year = Year.now();
        } else {
            year = Year.parse(yearString);
        }

        model.addAttribute("holidays", holidayService.getHolidayByYear(year));
        model.addAttribute("year", year);
        return "holiday/list";
    }

    @RequireHRPermission
    @RequireManagerPermission
    @GetMapping("/year") // 연 단위 공휴일 생성 페이지
    public String viewCreateYearHolidayForm(Model model) {
        return "holiday/year";
    }

    @RequireHRPermission
    @RequireManagerPermission
    @GetMapping("/add") // 공휴일 등록 페이지
    public String addHolidayForm(Model model) {
        return "holiday/add";
    }

    @RequireHRPermission
    @RequireManagerPermission
    @GetMapping("/edit/{holidayId}") // 공휴일 수정 페이지
    public String addHolidayForm(@PathVariable("holidayId") int holidayId, Model model) {
        model.addAttribute("holiday", holidayService.getHolidayById(holidayId));
        return "holiday/edit";
    }

}
