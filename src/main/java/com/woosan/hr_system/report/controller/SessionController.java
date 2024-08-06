package com.woosan.hr_system.report.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Controller
public class SessionController {

    @Autowired
    private HttpSession session;

    @GetMapping("/session")
    public String getSessionAttributes(Model model) {
        Map<String, Object> attributes = new HashMap<>();
        Enumeration<String> attributeNames = session.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            String attributeName = attributeNames.nextElement();
            attributes.put(attributeName, session.getAttribute(attributeName));
        }
        model.addAttribute("sessionAttributes", attributes);
        return "session-view";
    }
}
