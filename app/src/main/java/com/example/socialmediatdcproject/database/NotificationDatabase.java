package com.example.socialmediatdcproject.database;

import com.example.socialmediatdcproject.model.Notification;

import java.util.ArrayList;
import java.util.Date;

public class NotificationDatabase {

    private ArrayList<Notification> notifications = new ArrayList<>();

    // dữ liệu thông báo
    public ArrayList<Notification> dataNotifications() {
        // 1. Notification đầu tiên
        Notification n1 = new Notification();
        n1.setId(0);
        n1.setRoleId(1);
        n1.setTitle("New Feature Released");
        n1.setContent("We have just released a new feature for admins.");
        n1.setAdminUserId(101);

        // 2. Notification thứ hai
        Notification n2 = new Notification();
        n2.setId(1);
        n2.setRoleId(2);
        n2.setTitle("System Maintenance");
        n2.setContent("The system will be down for maintenance this weekend.");
        n2.setAdminUserId(102);


        // 3. Notification thứ ba
        Notification n3 = new Notification();
        n3.setId(2);
        n3.setRoleId(3);
        n3.setTitle("Policy Update");
        n3.setContent("We have updated our privacy policy.");
        n3.setAdminUserId(103);


        // Thêm tất cả các thông báo vào danh sách
        notifications.add(n1);
        notifications.add(n2);
        notifications.add(n3);

        return notifications;
    }
}
