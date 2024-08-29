package com.woosan.hr_system.notification.service;

import com.woosan.hr_system.employee.model.Department;
import com.woosan.hr_system.notification.model.Notification;

import java.util.List;

public interface NotificationService {
    List<Notification> getAllNotification();
    void createNotification(String employeeId, String message, String url);
    void createNotifications(List<String> employeeIdList, String message, String url);
    void createNotifications(String message, String url);
    void createNotifications(Department department, String message, String url);
    void createNotifications(String position, String message, String url);
    void readNotification(int notificationId);
    void readAllNotification();
    void removeNotification(int notificationId);
    void removeAllNotification();
}
