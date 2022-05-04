package ipn.mx.app.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import ipn.mx.app.Index;
import ipn.mx.app.R;
import ipn.mx.app.SettingHeadset;
import ipn.mx.app.global.GlobalInfo;
import ipn.mx.app.neurosky.NeuroSkyManager;
import ipn.mx.app.notification.GlobalNotificationManager;
import ipn.mx.app.notification.handlers.GenericNotificactionServiceDiConn;
import ipn.mx.app.notification.mock.NotificationManagerData;

public class HeadsetConnectionService extends Service {

    private static boolean isIntentServiceRunning = false;
    private final String TAG = "HeadsetConnService";
    // Objetos del Dialog


    public static boolean isIsIntentServiceRunning() {
        return isIntentServiceRunning;
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {

        isIntentServiceRunning = true;
        Log.d(TAG, "onStartCommand(): " + intent);
        final Handler handler = new Handler();
        Context context = this;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!GlobalNotificationManager.existNotification(context, GlobalInfo.NOTIFICATION_CONN_ID) && GlobalInfo.isEnableNotifyConnHeadset() && (NeuroSkyManager.getNeuroSky() == null || !NeuroSkyManager.getNeuroSky().isConnected())) {
                    NotificationManagerData nmd = new NotificationManagerData(getApplicationContext());
                    nmd.setmSummaryText(context.getResources().getString(R.string.text_conn_serv_btn));

                    nmd.setmContentTitle(context.getResources().getString(R.string.text_conn_serv_ctitle));
                    nmd.setmContentText(context.getResources().getString(R.string.text_conn_serv_ctext));
                    nmd.setmContentTitle(context.getResources().getString(R.string.text_conn_serv_ctitle));
                    nmd.setButtonText(context.getResources().getString(R.string.text_conn_serv_btn));

                    nmd.setmBigContentTitle(context.getResources().getString(R.string.text_conn_serv_btitle));
                    nmd.setmBigText(context.getResources().getString(R.string.text_conn_serv_btext));
                    nmd.setNotificationId(GlobalInfo.NOTIFICATION_CONN_ID);
                    GlobalNotificationManager gnm = new GlobalNotificationManager(getApplicationContext(), nmd);
                    gnm.setNotifyIntent(new Intent(context, SettingHeadset.class));
                    gnm.setStrategyIntent(new Intent(context, GenericNotificactionServiceDiConn.class));
                    gnm.generateNotification();
                }
                handler.postDelayed(this, 60000);
            }
        }, 2000);

        // returns the status
        // of the program
        return START_STICKY;

    }

    // execution of the service will
    // stop on calling this method
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy(): ");
        isIntentServiceRunning = false;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
