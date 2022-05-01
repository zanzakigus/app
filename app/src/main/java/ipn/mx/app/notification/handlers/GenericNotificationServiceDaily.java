package ipn.mx.app.notification.handlers;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import java.util.Random;

import ipn.mx.app.Daily;
import ipn.mx.app.Index;
import ipn.mx.app.global.GlobalInfo;
import ipn.mx.app.notification.GlobalNotificationManager;
import ipn.mx.app.strategies.StrategiesEnum;
import ipn.mx.app.strategies.StrategySelection;

public class GenericNotificationServiceDaily extends IntentService {

    private static final String TAG = "GenericNotificationServiceDaily";

    public GenericNotificationServiceDaily() {
        super("GenericNotificationService");
    }

    @SuppressLint("LongLogTag")
    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent(): " + intent);
        handleAction();
        
    }

    @SuppressLint("LongLogTag")
    private void handleAction() {
        Log.d(TAG, "handleAction()");

        Intent intent = new Intent(getApplicationContext(), Daily.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        GlobalNotificationManager.clearNotification(this, GlobalInfo.NOTIFICATION_EMOTION_ID);
        GlobalInfo.notificationsDisplayed.remove(GlobalInfo.NOTIFICATION_EMOTION_ID);
    }
}
