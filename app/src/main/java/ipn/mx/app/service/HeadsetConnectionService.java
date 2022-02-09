package ipn.mx.app.service;

import android.app.Dialog;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import ipn.mx.app.R;
import ipn.mx.app.neurosky.NeuroSkyManager;
import ipn.mx.app.notification.GlobalNotificationManager;
import ipn.mx.app.notification.mock.NotificationManagerData;
import ipn.mx.app.test.ClickNotification;

public class HeadsetConnectionService extends IntentService implements View.OnClickListener {

    private static boolean isIntentServiceRunning = false;
    private final String TAG = "HeadsetConnService";

    // Objetos del Dialog
    Dialog dialog;
    Button btnContinuarDialog;
    ImageView btnCloseDialog;


    public HeadsetConnectionService() {
        super("HeadsetConnectionService");
    }




    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d(TAG, "onHandleIntent(): " + intent);
        if(!isIntentServiceRunning) {
            isIntentServiceRunning = true;
        }
        if (NeuroSkyManager.getNeuroSky() == null || !NeuroSkyManager.getNeuroSky().isConnected()) {
            /*Context context = getApplicationContext();
            dialog = new Dialog(context);


            dialog.setContentView(R.layout.alert_dialog_test);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            btnCloseDialog = dialog.findViewById(R.id.btn_close);
            btnContinuarDialog = dialog.findViewById(R.id.btn_continuar);

            btnCloseDialog.setOnClickListener(this);
            btnContinuarDialog.setOnClickListener(this);

            dialog.show();*/
            NotificationManagerData nmd = new NotificationManagerData(this);
            GlobalNotificationManager gnm = new GlobalNotificationManager(this, nmd);
            gnm.generateNotification();

        }

    }

    @Override
    public void onClick(View v) {
        if (v == btnCloseDialog) {
            dialog.dismiss();
        } else if (v == btnContinuarDialog) {
            dialog.dismiss();
            Intent intent = new Intent(this, ClickNotification.class);
            startActivity(intent);
        }
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand(): " + intent);
        Toast.makeText(this, "service startedff", Toast.LENGTH_LONG).show();
        isIntentServiceRunning = true;
        return super.onStartCommand(intent, flags, startId);
    }

    /*@Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy(): ");
        Toast.makeText(this, "service started", Toast.LENGTH_LONG).show();
        isIntentServiceRunning = false;
        super.onDestroy();
    }*/


    public static boolean isIsIntentServiceRunning() {
        return isIntentServiceRunning;
    }

}
