package ipn.mx.app.service;

import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import ipn.mx.app.Index;
import ipn.mx.app.global.GlobalInfo;
import ipn.mx.app.neurosky.NeuroSkyManager;
import ipn.mx.app.notification.GlobalNotificationManager;
import ipn.mx.app.notification.mock.NotificationManagerData;

public class HeadsetConnectionService extends Service implements View.OnClickListener {

    private static boolean isIntentServiceRunning = false;
    private final String TAG = "HeadsetConnService";
    // Objetos del Dialog
    Dialog dialog;
    Button btnContinuarDialog;
    ImageView btnCloseDialog;

    public static boolean isIsIntentServiceRunning() {
        return isIntentServiceRunning;
    }

    @Override
    public void onClick(View v) {
        if (v == btnCloseDialog) {
            dialog.dismiss();
        } else if (v == btnContinuarDialog) {
            dialog.dismiss();
            Intent intent = new Intent(this, Index.class);
            startActivity(intent);
        }
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand(): " + intent);
        final Handler handler = new Handler();
        Context context = this;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!GlobalNotificationManager.existNotification(context, GlobalInfo.NOTIFICATION_CONN_ID) && GlobalInfo.isEnableNotifyConnHeadset() && (NeuroSkyManager.getNeuroSky() == null || !NeuroSkyManager.getNeuroSky().isConnected())) {
                    NotificationManagerData nmd = new NotificationManagerData(getApplicationContext());
                    GlobalNotificationManager gnm = new GlobalNotificationManager(getApplicationContext(), nmd);
                    gnm.generateNotification();
                }
                handler.postDelayed(this, 5000);
            }
        }, 1000);

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
