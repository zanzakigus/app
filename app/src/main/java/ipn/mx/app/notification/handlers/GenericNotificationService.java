package ipn.mx.app.notification.handlers;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationManagerCompat;

import ipn.mx.app.Index;
import ipn.mx.app.notification.GlobalNotificationManager;
import ipn.mx.app.test.ClickNotification;

public class GenericNotificationService extends IntentService {

    private static final String TAG = "NotificationService";
    public static final String STRATEGY_ACTION_YES = "YES";
    public static final String STRATEGY_ACTION_NO = "NO";

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
        Intent intent = new Intent(getApplicationContext(), ClickNotification.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        GlobalNotificationManager.clearNotification(this);

    }


}
