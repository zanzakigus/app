package ipn.mx.app.notification;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import ipn.mx.app.Index;
import ipn.mx.app.R;
import ipn.mx.app.global.GlobalInfo;
import ipn.mx.app.notification.handlers.GenericNotificationService;
import ipn.mx.app.notification.mock.NotificationManagerData;
import ipn.mx.app.notification.util.NotificationUtil;

public class GlobalNotificationManager {
    public static final String TAG = "Notification";
    NotificationManagerCompat notificationManagerCompat;
    NotificationManagerData notificacionManagerData;
    Intent notifyIntent;
    Intent strategyIntent;
    private Context context;

    public GlobalNotificationManager(Context context, NotificationManagerData notificacionManagerData) {
        this.context = context;
        notificationManagerCompat = NotificationManagerCompat.from(context.getApplicationContext());
        this.notificacionManagerData = notificacionManagerData;

    }

    public static void clearNotification(Context context, int notificationId) {

        Log.d(TAG, "clearNotification()");

        NotificationManagerCompat notificationManagerCompat =
                NotificationManagerCompat.from(context);
        notificationManagerCompat.cancel(notificationId);
    }



    public static boolean existNotification(Context context, int notificationId) {
        Log.d(TAG, "existNotification()");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
            StatusBarNotification[] notifications = mNotificationManager.getActiveNotifications();
            for (StatusBarNotification notification : notifications) {
                if (notification.getId() == notificationId) {
                    return true;
                }
            }
            return false;
        }else{
            for (Integer notificacionIdArr: GlobalInfo.notificationsDisplayed
                 ) {
                if (notificacionIdArr == notificationId){
                    return true;
                }
            }
            return false;
        }

    }

    public void generateNotification() {
        Log.d(TAG, "generateNotification()");

        // 1. Create/Retrieve Notification Channel for O and beyond devices (26+).
        String notificationChannelId =
                NotificationUtil.createNotificationChannel(context, notificacionManagerData);


        // 2. Build the BIG_TEXT_STYLE.
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle()
                // Overrides ContentText in the big form of the template.
                .bigText(notificacionManagerData.getBigText())
                // Overrides ContentTitle in the big form of the template.
                .setBigContentTitle(notificacionManagerData.getBigContentTitle())
                // Summary line after the detail section in the big form of the template.
                // Note: To improve readability, don't overload the user with info. If Summary Text
                // doesn't add critical information, you should skip it.
                .setSummaryText(notificacionManagerData.getSummaryText());


        // 3. Set up main Intent for notification.
        notifyIntent = new Intent(context, Index.class);

        // Sets the Activity to start in a new, empty task
        /* notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);*/

        PendingIntent notifyPendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        notifyIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        // 4. Create additional Actions (Intents) for the Notification.

        // Strategy Action
        strategyIntent = new Intent(context, GenericNotificationService.class);
        strategyIntent.setAction(GenericNotificationService.STRATEGY_ACTION_YES);

        PendingIntent strategyPendingIntent = PendingIntent.getService(context, 0, strategyIntent, 0);
        NotificationCompat.Action strategyAction =
                new NotificationCompat.Action.Builder(
                        R.drawable.ic_reply_white_18dp,
                        notificacionManagerData.getButtonText(),
                        strategyPendingIntent)
                        .build();


        // 5. Build and issue the notification.


        // Notification Channel Id is ignored for Android pre O (26).
        NotificationCompat.Builder notificationCompatBuilder =
                new NotificationCompat.Builder(
                        context.getApplicationContext(), notificationChannelId);

        GlobalNotificationBuilder.setNotificationCompatBuilderInstance(notificationCompatBuilder);

        @SuppressLint("WrongConstant") Notification notification = notificationCompatBuilder
                // BIG_TEXT_STYLE sets title and content for API 16 (4.1 and after).
                .setStyle(bigTextStyle)
                // Title for API <16 (4.0 and below) devices.
                .setContentTitle(notificacionManagerData.getContentTitle())
                // Content for API <24 (7.0 and below) devices.
                .setContentText(notificacionManagerData.getContentText())
                .setSmallIcon(R.drawable.logo)
                .setLargeIcon(BitmapFactory.decodeResource(
                        context.getResources(),
                        R.drawable.logo))
                .setContentIntent(notifyPendingIntent)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                // Set primary color (important for Wear 2.0 Notifications).
                .setColor(ContextCompat.getColor(context.getApplicationContext(), R.color.background_color))

                // SIDE NOTE: Auto-bundling is enabled for 4 or more notifications on API 24+ (N+)
                // devices and all Wear devices. If you have more than one notification and
                // you prefer a different summary notification, set a group key and create a
                // summary notification via
                // .setGroupSummary(true)
                // .setGroup(GROUP_KEY_YOUR_NAME_HERE)

                .setCategory(Notification.CATEGORY_MESSAGE)

                // Sets priority for 25 and below. For 26 and above, 'priority' is deprecated for
                // 'importance' which is set in the NotificationChannel. The integers representing
                // 'priority' are different from 'importance', so make sure you don't mix them.
                .setPriority(notificacionManagerData.getPriority())

                // Sets lock-screen visibility for 25 and below. For 26 and above, lock screen
                // visibility is set in the NotificationChannel.
                .setVisibility(notificacionManagerData.getChannelLockscreenVisibility())
                .addAction(strategyAction)


                .build();

        try {
            notificationManagerCompat.notify(notificacionManagerData.getNotificationId(), notification);
            GlobalInfo.notificationsDisplayed.add(notificacionManagerData.getNotificationId());
        }catch (Exception e){
            e.printStackTrace();

        }

    }

    public Intent getNotifyIntent() {
        return notifyIntent;
    }

    public void setNotifyIntent(Intent notifyIntent) {
        this.notifyIntent = notifyIntent;
    }

    public Intent getStrategyIntent() {
        return strategyIntent;
    }

    public void setStrategyIntent(Intent strategyIntent) {
        this.strategyIntent = strategyIntent;
    }
}
