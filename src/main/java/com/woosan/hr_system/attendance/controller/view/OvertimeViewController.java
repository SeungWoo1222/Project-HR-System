package com.woosan.hr_system.attendance.controller.view;

import com.woosan.hr_system.attendance.model.Attendance;
import com.woosan.hr_system.attendance.model.Overtime;
import com.woosan.hr_system.attendance.service.OvertimeService;
import com.woosan.hr_system.employee.service.EmployeeService;
import com.woosan.hr_system.search.PageRequest;
import com.woosan.hr_system.search.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.YearMonth;

@Controller
@RequestMapping("/overtime")
public class OvertimeViewController {
    @Autowired
    private OvertimeService overtimeService;
    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/list") // 초과근무 목록 조회
    public String viewOvertimeList(@RequestParam(name = "page", defaultValue = "1") int page,
                                     @RequestParam(name = "size", defaultValue = "10") int size,
                                     @RequestParam(name = "keyword", defaultValue = "") String keyword,
                                     @RequestParam(name = "department", defaultValue = "") String department,
                                     @RequestParam(name = "yearmonth", defaultValue = "") String yearMonthString,
                                     Model model) {
        // 검색 년월 설정
        YearMonth yearMonth;
        if (yearMonthString.isEmpty()) {
            yearMonth = YearMonth.now();
        } else {
            yearMonth = YearMonth.parse(yearMonthString);
        }

        // 조건에 해당하는 검색 후 결과 페이징
        PageRequest pageRequest = new PageRequest(page - 1, size, keyword); // 페이지 번호 인덱싱을 위해 다시 -1
        PageResult<Attendance> pageResult = overtimeService.searchOvertime(pageRequest, department, yearMonth);

        // 모델에 추가
        model.addAttribute("overtimeList", pageResult.getData());
        model.addAttribute("currentPage", pageResult.getCurrentPage() + 1); // 뷰에서 가독성을 위해 +1
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("keyword", keyword);
        model.addAttribute("department", department);
        model.addAttribute("yearmonth", yearMonth);

        return "overtime/list";
    }

    @GetMapping("/{overtimeId}") // 초과근무 수정 페이지
    public String viewEditOvertimeForm(@PathVariable("overtimeId") int overtimeId, Model model) {
        // 초과근무 정보 상세 조회
        Overtime overtime = overtimeService.getOvertimeById(overtimeId);
        model.addAttribute(overtime);

        String employeeId = overtime.getEmployeeId();

        // 사원 정보 상세 조회 후 모델에 추가
        model.addAttribute("employee", employeeService.getEmployeeById(employeeId));

        return "overtime/edit";
    }
}
