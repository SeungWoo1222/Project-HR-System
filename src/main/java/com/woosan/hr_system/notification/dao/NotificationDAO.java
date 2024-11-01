package com.woosan.hr_system.notification.dao;

import com.woosan.hr_system.notification.model.Notification;
import java.util.List;
import java.util.Map;

public interface NotificationDAO {
    List<Notification> selectAllNotification(String employeeId);
    int selectUnreadCount(String employeeId);
    void insertNotification(Notification notification);
    void insertNotifications(List<Notification> notificationList);
    void insertDepartmentNotifications(Map<String, Object> map);
    void insertPositionNotifications(Map<String, Object> map);
    void markAsRead(int notificationId);
    void markAsReadAll(String employeeId);
    void deleteNotification(int notificationId);
    void deleteAllNotification(String employeeId);
    int deleteOldNotifications();
}
