package com.estimote.notification;

import android.app.Application;
import android.widget.Switch;
import android.widget.TextView;

import com.estimote.notification.estimote.NotificationsManager;
import com.estimote.proximity_sdk.api.EstimoteCloudCredentials;

//
// Running into any issues? Drop us an email to: contact@estimote.com
//

public class MyApplication extends Application {

    public TextView notifyText;
    public Switch activeNotify;
    public Integer id;
    public Controlregistro control;
    public EstimoteCloudCredentials cloudCredentials = new EstimoteCloudCredentials("redsocial-589", "67d48d8a421c24841f6a8d56c67b1b73");
    private NotificationsManager notificationsManager;

    public void enableBeaconNotifications() {
        notificationsManager = new NotificationsManager(this);
        notificationsManager.startMonitoring();
    }

    public void setData(TextView notify, Switch actNotify, Controlregistro registro, Integer id) {
        notifyText = notify;
        activeNotify = actNotify;
        control = registro;
        this.id = id;
    }

}
