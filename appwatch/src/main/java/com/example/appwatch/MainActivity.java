package com.example.appwatch;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.core.app.NotificationManagerCompat;
import androidx.wear.ambient.AmbientMode;
import androidx.wear.widget.WearableLinearLayoutManager;
import androidx.wear.widget.WearableRecyclerView;



public class MainActivity extends Activity implements
        AmbientMode.AmbientCallbackProvider {



    private static final String TAG = "StandaloneMainActivity";

    public static final int NOTIFICATION_ID = 888;

    /*
     * Used to represent each major {@link NotificationCompat.Style} in the
     * {@link WearableRecyclerView}. These constants are also used in a switch statement when one
     * of the items is selected to create the appropriate {@link Notification}.
     */
    private static final String BIG_TEXT_STYLE = "BIG_TEXT_STYLE";
    private static final String BIG_PICTURE_STYLE = "BIG_PICTURE_STYLE";
    private static final String INBOX_STYLE = "INBOX_STYLE";
    private static final String MESSAGING_STYLE = "MESSAGING_STYLE";

    /*
    Collection of major {@link NotificationCompat.Style} to create {@link CustomRecyclerAdapter}
    for {@link WearableRecyclerView}.
    */
    private static final String[] NOTIFICATION_STYLES =
            {BIG_TEXT_STYLE, BIG_PICTURE_STYLE, INBOX_STYLE, MESSAGING_STYLE};

    private NotificationManagerCompat mNotificationManagerCompat;

    // Needed for {@link SnackBar} to alert users when {@link Notification} are disabled for app.
    private FrameLayout mMainFrameLayout;
    private WearableRecyclerView mWearableRecyclerView;
    private CustomRecyclerAdapter mCustomRecyclerAdapter;

    /**
     * Ambient mode controller attached to this display. Used by Activity to see if it is in
     * ambient mode.
     */
    private AmbientMode.AmbientController mAmbientController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");

        setContentView(R.layout.activity_main);

        // Enables Ambient mode.
        mAmbientController = AmbientMode.attachAmbientSupport(this);

        mNotificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());

        mMainFrameLayout = findViewById(R.id.mainFrameLayout);
        mWearableRecyclerView = findViewById(R.id.recycler_view);

        // Aligns the first and last items on the list vertically centered on the screen.
        mWearableRecyclerView.setEdgeItemsCenteringEnabled(true);

        // Customizes scrolling so items farther away form center are smaller.
        ScalingScrollLayoutCallback scalingScrollLayoutCallback =
                new ScalingScrollLayoutCallback();
        mWearableRecyclerView.setLayoutManager(
                new WearableLinearLayoutManager(this, scalingScrollLayoutCallback));

        // Improves performance because we know changes in content do not change the layout size of
        // the RecyclerView.
        mWearableRecyclerView.setHasFixedSize(true);

        // Specifies an adapter (see also next example).
        mCustomRecyclerAdapter = new CustomRecyclerAdapter(
                NOTIFICATION_STYLES,
                // Controller passes selected data from the Adapter out to this Activity to trigger
                // updates in the UI/Notifications.
                new Controller(this));

        mWearableRecyclerView.setAdapter(mCustomRecyclerAdapter);

        mWearableRecyclerView.setEdgeItemsCenteringEnabled(true);

        mWearableRecyclerView.setLayoutManager(
                new WearableLinearLayoutManager(this));


    }

    @Override
    public AmbientMode.AmbientCallback getAmbientCallback() {
        return new MyAmbientCallback();
    }

    private class MyAmbientCallback extends AmbientMode.AmbientCallback {
        /** Prepares the UI for ambient mode. */
        @Override
        public void onEnterAmbient(Bundle ambientDetails) {
            super.onEnterAmbient(ambientDetails);

            Log.d(TAG, "onEnterAmbient() " + ambientDetails);
            // In our case, the assets are already in black and white, so we don't update UI.
        }

        /** Restores the UI to active (non-ambient) mode. */
        @Override
        public void onExitAmbient() {
            super.onExitAmbient();

            Log.d(TAG, "onExitAmbient()");
            // In our case, the assets are already in black and white, so we don't update UI.
        }
    }
}