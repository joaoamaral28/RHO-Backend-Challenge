package com.rho.challenge.dao;

import com.rho.challenge.model.Notification;

public interface NotificationDAO{

    public Notification getNotification(Integer account_id);
    public int storeNotification(Notification notification);

}
