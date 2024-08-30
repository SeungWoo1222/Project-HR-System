package com.woosan.hr_system.notification.controller;

import com.woosan.hr_system.notification.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/notification")
public class NotificationController {
    @Autowired
    private NotificationService notificationService;

    @GetMapping("/all") // 내 알림 조회
    public String getAllNotification(Model model) {
        model.addAttribute("notificationList", notificationService.getAllNotification());
        return "/fragments/notification-content";
    }

    @GetMapping("/unread") // 읽지 않은 알림 개수 조회
    public ResponseEntity<Map<String, Integer>> getUnreadNotifications() {
        int unreadCount = notificationService.getUnreadCount();
        Map<String, Integer> response = new HashMap<>();
        response.put("unreadCount", unreadCount);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{notificationId}") // 내 알림 읽음 처리
    public ResponseEntity<Void> readNotification(@PathVariable("notificationId") int notificationId) {
        notificationService.readNotification(notificationId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/all") // 내 모든 알림 읽음 처리
    public ResponseEntity<Void> readAllNotification() {
        notificationService.readAllNotification();
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{notificationId}")// 내 알림 삭제
    public ResponseEntity<Void> deleteNotification(@PathVariable("notificationId") int notificationId) {
        notificationService.removeNotification(notificationId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/all") // 내 모든 알림 삭제
    public ResponseEntity<Void> deleteAllNotification() {
        notificationService.removeAllNotification();
        return ResponseEntity.ok().build();
    }
}
