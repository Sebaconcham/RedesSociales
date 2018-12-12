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
    public RegistroInterface registroInterface;
    public EstimoteCloudCredentials cloudCredentials = new EstimoteCloudCredentials("redes-sociales-opn", "f63c8d2f7cfca263151b427754426230");
    private NotificationsManager notificationsManager;

    public void enableBeaconNotifications() {
        notificationsManager = new NotificationsManager(this);
        notificationsManager.startMonitoring();
    }

    public void setData(TextView notify, Switch actNotify, RegistroInterface registroSer, Integer idt) {
        notifyText = notify;
        activeNotify = actNotify;
        registroInterface = registroSer;
        id = idt;
    }

}
