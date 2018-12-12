package com.estimote.notification.estimote;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.estimote.notification.MainActivity;
import com.estimote.notification.MyApplication;
import com.estimote.notification.Registro;
import com.estimote.proximity_sdk.api.ProximityObserver;
import com.estimote.proximity_sdk.api.ProximityObserverBuilder;
import com.estimote.proximity_sdk.api.ProximityZone;
import com.estimote.proximity_sdk.api.ProximityZoneBuilder;
import com.estimote.proximity_sdk.api.ProximityZoneContext;

import java.util.Set;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//
// Running into any issues? Drop us an email to: contact@estimote.com
//

public class NotificationsManager {

    private Context context;
    private NotificationManager notificationManager;

    public NotificationsManager(Context context) {
        this.context = context;
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    private Notification buildNotification(String title, String text) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel contentChannel = new NotificationChannel(
                    "content_channel", "Things near you", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(contentChannel);
        }

        return new NotificationCompat.Builder(context, "content_channel")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(text)
                .setContentIntent(PendingIntent.getActivity(context, 0,
                        new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();
    }

    public void startMonitoring() {
        ProximityObserver proximityObserver =
                new ProximityObserverBuilder(context, ((MyApplication) context).cloudCredentials)
                        .onError(new Function1<Throwable, Unit>() {
                            @Override
                            public Unit invoke(Throwable throwable) {
                                Log.e("app", "proximity observer error: " + throwable);
                                return null;
                            }
                        })
                        .withBalancedPowerMode()
                        .build();

        ProximityZone zone = new ProximityZoneBuilder()
                .forTag("Dagoberto")
                .inCustomRange(1)
                .onEnter(new Function1<ProximityZoneContext, Unit>() {
                    @Override
                    public Unit invoke(ProximityZoneContext proximityContext) {
                        notifyDB(proximityContext,"Entrada");
                        int notificationId = 1;
                        if (((MyApplication) context).activeNotify.isChecked()){
                            Notification mensaje = buildNotification("Conectado", "Beacon: "+proximityContext.getDeviceId()+", Etiquetado como: "+proximityContext.getTag());
                            notificationManager.notify(notificationId, mensaje);
                        }else{
                            ((MyApplication) context).notifyText.setText("Conectado enviando registro... Sin notificacion");
                        }
                        return null;
                    }
                })
                .onExit(new Function1<ProximityZoneContext, Unit>() {
                    @Override
                    public Unit invoke(ProximityZoneContext proximityContext) {
                        notifyDB(proximityContext,"Salida");
                        int notificationId = 1;
                        if (((MyApplication) context).activeNotify.isChecked()){
                            Notification mensaje = buildNotification("Desconectado", "Beacon: "+proximityContext.getDeviceId()+", Etiquetado como: "+proximityContext.getTag());
                            notificationManager.notify(notificationId, mensaje);
                        }else{
                            ((MyApplication) context).notifyText.setText("Desconectado enviando registro... Sin notificacion");
                        }
                        return null;
                    }
                })
                .build();

        proximityObserver.startObserving(zone);
    }

    private void notifyDB(ProximityZoneContext proximityContext , String estado) {
        Call<Registro> registro = ((MyApplication) context).registroInterface.insertRegistro(proximityContext.getDeviceId(),estado,((MyApplication) context).id);

        registro.enqueue(new Callback<Registro>() {
            @Override
            public void onResponse(Call<Registro> call, Response<Registro> response) {
                if (response.isSuccessful()){
                    ((MyApplication) context).notifyText.setText(response.toString());
                }else{
                    ((MyApplication) context).notifyText.setText(response.toString());
                }
            }
            @Override
            public void onFailure(Call<Registro> call, Throwable t) {
                ((MyApplication) context).notifyText.setText(t.toString());
            }
        });
    }

    public class ProximityContent {

        private String title;
        private String subtitle;

        ProximityContent(String title, String subtitle) {
            this.title = title;
            this.subtitle = subtitle;
        }

        String getTitle() {
            return title;
        }

        String getSubtitle() {
            return subtitle;
        }
    }
}
