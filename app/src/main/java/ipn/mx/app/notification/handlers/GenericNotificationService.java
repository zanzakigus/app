package ipn.mx.app.notification.handlers;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import ipn.mx.app.Index;
import ipn.mx.app.global.GlobalInfo;
import ipn.mx.app.notification.GlobalNotificationManager;

public class GenericNotificationService extends IntentService {

    public static final String STRATEGY_ACTION_YES = "YES";
    public static final String STRATEGY_ACTION_NO = "NO";
    private static final String TAG = "NotificationService";

    public GenericNotificationService() {
        super("GenericNotificationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent(): " + intent);

        if (intent != null) {
            final String action = intent.getAction();
            if (STRATEGY_ACTION_YES.equals(action)) {
                handleActionYes();
            }
        }
    }


    /**
     * Handles action strategy in the provided background thread.
     */
    private void handleActionYes() {
        Log.d(TAG, "handleActionStrategy()");
        Intent intent = new Intent(getApplicationContext(), Index.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        GlobalNotificationManager.clearNotification(this, GlobalInfo.NOTIFICATION_EMOTION_ID);
        GlobalInfo.notificationsDisplayed.remove(GlobalInfo.NOTIFICATION_EMOTION_ID);
    }


}
