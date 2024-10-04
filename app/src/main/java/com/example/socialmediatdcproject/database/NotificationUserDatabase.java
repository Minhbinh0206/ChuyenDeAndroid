package com.example.socialmediatdcproject.database;

import com.example.socialmediatdcproject.model.NotificationUser;

import java.util.ArrayList;

public class NotificationUserDatabase {

    private ArrayList<NotificationUser> notificationUsers = new ArrayList<>();


    public ArrayList<NotificationUser> dataNotificationUsers() {
        // thêm người nhận thông báo
        NotificationUser nu1 = new NotificationUser();
        nu1.setNotificationId(1);
        nu1.setUserId(1);

        NotificationUser nu2 = new NotificationUser();
        nu2.setNotificationId(2);
        nu2.setUserId(2);

        NotificationUser nu3 = new NotificationUser();
        nu3.setNotificationId(1);
        nu3.setUserId(3);

        notificationUsers.add(nu1);
        notificationUsers.add(nu2);
        notificationUsers.add(nu3);

        return notificationUsers;
    }
}
