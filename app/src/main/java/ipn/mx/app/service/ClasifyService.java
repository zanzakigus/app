package ipn.mx.app.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import ipn.mx.app.global.GlobalInfo;
import ipn.mx.app.neurosky.NeuroSkyManager;

public class ClasifyService extends Service {

    private static boolean intentServiceRunning = false;
    private final String TAG = "ClasifyService";

    public static boolean isIntentServiceRunning() {
        return intentServiceRunning;
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        intentServiceRunning = true;
        Log.d(TAG, "onStartCommand(): " + intent);
        final Handler handler = new Handler();
        Context context = this;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (NeuroSkyManager.getNeuroSky() != null && NeuroSkyManager.getNeuroSky().isConnected() && isIntentServiceRunning()) {
                    Log.d(TAG, "onStartCommand(): Classifying");
                    NeuroSkyManager.enviarWavesIdentificar();
                }
                handler.postDelayed(this, GlobalInfo.getClasifyTimeDelay());
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
        intentServiceRunning = false;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
