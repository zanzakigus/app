package ipn.mx.app.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class StartSendNotificationService extends BroadcastReceiver {

    private static final String TAG = StartSendNotificationService.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent hcs = new Intent(context, SendNotificationsService.class);
        context.startService(hcs);

        Log.d(TAG, "onReceive: Me encendi");
    }
}
