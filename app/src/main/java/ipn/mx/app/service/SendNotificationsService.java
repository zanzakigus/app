package ipn.mx.app.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Calendar;

import ipn.mx.app.global.GlobalInfo;
import ipn.mx.app.neurosky.NeuroSkyManager;
import ipn.mx.app.notification.GlobalNotificationManager;
import ipn.mx.app.notification.mock.NotificationManagerDaily;

public class SendNotificationsService extends Service {

    private static final String TAG = SendNotificationsService.class.getSimpleName();

    private static boolean intentServiceRunning = false;

    public static boolean isIntentServiceRunning() {
        return intentServiceRunning;
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        intentServiceRunning = true;
        Log.d(TAG, "onStartCommand(): " + intent);
        Context context = this;
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isIntentServiceRunning()) {
                    final Calendar c = Calendar.getInstance();
                    int hora = c.get(Calendar.HOUR_OF_DAY);
                    int minuto = c.get(Calendar.MINUTE);

                    int[][] horarios = GlobalInfo.getScheduleNotifications();

                    for(int[] horario: horarios){
                        Log.d(TAG, "run: Comparo " + hora + ":" + minuto + " Con: " + horario[0] + ":" + horario[1]);
                        if(horario[0] == hora && horario[1] == minuto){
                            NotificationManagerDaily nmd = new NotificationManagerDaily(context);
                            GlobalNotificationManager gnm = new GlobalNotificationManager(context, nmd);
                            gnm.generateNotificationDaily();
                            break;
                        }
                    }
                }
                handler.postDelayed(this, 60000);
            }
        }, 60000);

        // returns the status
        // of the program
        return START_STICKY;
    }

    // execution of the service will
    // stop on calling this method
    public void onDestroy() {
        super.onDestroy();
        intentServiceRunning = false;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
