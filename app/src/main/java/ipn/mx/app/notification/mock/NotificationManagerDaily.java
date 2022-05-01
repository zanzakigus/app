package ipn.mx.app.notification.mock;

import android.app.NotificationManager;
import android.content.Context;

import androidx.core.app.NotificationCompat;

import ipn.mx.app.R;
import ipn.mx.app.global.GlobalInfo;

public class NotificationManagerDaily extends MockDatabase.MockNotificationData {


    // Unique data for this Notification.Style:
    private String mBigContentTitle;
    private String mBigText;
    private String mSummaryText;
    private String buttonText;
    private int notificationId;


    public NotificationManagerDaily(Context context) {
        // Standard Notification values:
        // Title for API <16 (4.0 and below) devices.
        mContentTitle = context.getResources().getString(R.string.daily_content_title);
        ;
        // Content for API <24 (4.0 and below) devices.
        mContentText = context.getResources().getString(R.string.daily_content_text);
        mPriority = NotificationCompat.PRIORITY_HIGH;
        buttonText = context.getResources().getString(R.string.daily_notification_button);

        // BigText Style Notification values:
        mBigContentTitle = context.getResources().getString(R.string.app_name);
        mBigText = context.getResources().getString(R.string.daily_big_text);
        mSummaryText = context.getResources().getString(R.string.daily_summary_text);
        notificationId = GlobalInfo.NOTIFICATION_EMOTION_ID;

        // Notification channel values (for devices targeting 26 and above):
        mChannelId = "notifications";
        // The user-visible name of the channel.
        mChannelName = "Sample Reminder";
        // The user-visible description of the channel.
        mChannelDescription = "Sample Reminder Notifications";
        mChannelImportance = NotificationManager.IMPORTANCE_HIGH;
        mChannelEnableVibrate = true;
        mChannelLockscreenVisibility = NotificationCompat.VISIBILITY_PRIVATE;
    }

    public String getBigContentTitle() {
        return mBigContentTitle;
    }

    public String getBigText() {
        return mBigText;
    }

    public String getSummaryText() {
        return mSummaryText;
    }

    public void setmBigContentTitle(String mBigContentTitle) {
        this.mBigContentTitle = mBigContentTitle;
    }

    public void setmBigText(String mBigText) {
        this.mBigText = mBigText;
    }

    public void setmSummaryText(String mSummaryText) {
        this.mSummaryText = mSummaryText;
    }

    public String getButtonText() {
        return buttonText;
    }

    public void setButtonText(String buttonText) {
        this.buttonText = buttonText;
    }

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    @Override
    public String toString() {
        return getBigContentTitle() + getBigText();
    }


}
