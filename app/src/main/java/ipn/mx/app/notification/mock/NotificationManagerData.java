package ipn.mx.app.notification.mock;

import android.app.NotificationManager;
import android.content.Context;

import androidx.core.app.NotificationCompat;
import ipn.mx.app.R;

public class NotificationManagerData extends MockDatabase.MockNotificationData {


    // Unique data for this Notification.Style:
    private String mBigContentTitle;
    private String mBigText;
    private String mSummaryText;



    public NotificationManagerData(Context context) {


        // Standard Notification values:
        // Title for API <16 (4.0 and below) devices.
        mContentTitle = context.getResources().getString(R.string.m_content_title);;
        // Content for API <24 (4.0 and below) devices.
        mContentText = context.getResources().getString(R.string.m_content_text);
        mPriority = NotificationCompat.PRIORITY_HIGH;

        // BigText Style Notification values:
        mBigContentTitle = context.getResources().getString(R.string.app_name);
        mBigText =context.getResources().getString(R.string.m_big_text);
        mSummaryText = context.getResources().getString(R.string.m_summary_text);

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


    @Override
    public String toString() {
        return getBigContentTitle() + getBigText();
    }


}
