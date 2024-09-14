package com.woosan.hr_system.schedule.controller;

import org.springframework.ui.Model;
import com.woosan.hr_system.schedule.model.BusinessTrip;
import com.woosan.hr_system.schedule.service.BusinessTripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/businessTrip")
public class BusinessTripController {
    @Autowired
    private BusinessTripService businessTripService;

    // 전체 조회
    @GetMapping("/list")
    public String getAllBusinessTrips(Model model) {
        List<BusinessTrip> trips = businessTripService.getAllBusinessTrips();
        model.addAttribute("trips", trips);
        return "businessTrip/list"; // 모든 비즈니스 트립을 보여주는 페이지
    }

    // ID로 단일 출장 조회
    @GetMapping("/{mapId}")
    public String getBusinessTripById(@RequestParam("mapId") int mapId, Model model) {
        BusinessTrip trip = businessTripService.getBusinessTripById(mapId);
        if (trip != null) {
            model.addAttribute("businessTrip", trip);
            return "businessTrip/detail"; // 특정 비즈니스 트립 상세 페이지
        } else {
            model.addAttribute("error", "출장 정보를 찾을 수 없습니다.");
            return "error"; // 에러 페이지
        }
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("businessTrip", new BusinessTrip());
        model.addAttribute("contacts", businessTripService.getAllContacts());
        return "businessTrip/form";
    }

    @PostMapping("/create")
    public String createBusinessTrip(@RequestPart BusinessTrip businessTrip) {
        businessTripService.createBusinessTrip(businessTrip);
        return "redirect:/businessTrip/list";
    }

    @GetMapping("/edit/{mapId}")
    public String editForm(@PathVariable("mapId") int mapId, Model model) {
        BusinessTrip trip = businessTripService.getBusinessTripById(mapId);
        model.addAttribute("businessTrip", trip);
        model.addAttribute("contacts", businessTripService.getAllContacts());
        return "businessTrip/form";
    }

    @PostMapping("/edit")
    public String editBusinessTrip(@ModelAttribute BusinessTrip businessTrip) {
        businessTripService.updateBusinessTrip(businessTrip);
        return "redirect:/businessTrip/list";
    }

    @GetMapping("/delete/{mapId}")
    public String deleteBusinessTrip(@PathVariable("mapId") int mapId) {
        businessTripService.deleteBusinessTrip(mapId);
        return "redirect:/businessTrip/list";
    }
}
