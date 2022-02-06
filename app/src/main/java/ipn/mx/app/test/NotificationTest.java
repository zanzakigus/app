package ipn.mx.app.test;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.Person;
import androidx.core.app.RemoteInput;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;

import ipn.mx.app.Index;
import ipn.mx.app.MainActivity;
import ipn.mx.app.R;
import ipn.mx.app.notification.GlobalNotificationBuilder;
import ipn.mx.app.notification.GlobalNotificationManager;
import ipn.mx.app.notification.mock.MockDatabase;
import ipn.mx.app.notification.mock.NotificationManagerData;
import ipn.mx.app.notification.util.NotificationUtil;

public class NotificationTest extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "Notification";
    Button btnNotificacionExterna;
    Button btnNotificacionInterna;

    private PendingIntent pendingIntent;

    // Objetos del Dialog
    Dialog dialog;
    Button btnContinuarDialog;
    ImageView btnCloseDialog;

    NotificationManagerCompat notificationManagerCompat;

    // TODO: Si se desea programar una notificacion se hace esto
    // TODO: https://www.youtube.com/watch?v=REJ3pDLGTmA

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_test);

        dialog = new Dialog(this);

        btnNotificacionExterna = findViewById(R.id.btn_notificacion_externa);
        btnNotificacionInterna = findViewById(R.id.btn_notificacion_interna);

        btnNotificacionExterna.setOnClickListener(this);
        btnNotificacionInterna.setOnClickListener(this);
        notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());

    }

    @Override
    public void onClick(View v) {
        if (v == btnNotificacionExterna) {


            boolean areNotificationsEnabled = notificationManagerCompat.areNotificationsEnabled();
            if (!areNotificationsEnabled) {
                Toast.makeText(this, "No se que pedo", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "No se que pedo", Toast.LENGTH_SHORT).show();
            }
            NotificationManagerData nmd = new NotificationManagerData(this);
            GlobalNotificationManager gnm = new GlobalNotificationManager(this, nmd);
            gnm.generateNotification();

        } else if (v == btnNotificacionInterna) {
            dialog.setContentView(R.layout.alert_dialog_test);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            btnCloseDialog = dialog.findViewById(R.id.btn_close);
            btnContinuarDialog = dialog.findViewById(R.id.btn_continuar);

            btnCloseDialog.setOnClickListener(this);
            btnContinuarDialog.setOnClickListener(this);

            dialog.show();
        } else if (v == btnCloseDialog) {
            dialog.dismiss();
        } else if (v == btnContinuarDialog) {
            dialog.dismiss();
            Intent intent = new Intent(this, ClickNotification.class);
            startActivity(intent);
            finish();
        }
    }

    private void setPendingIntent(Class<?> clsActivity) {
        Intent intent = new Intent(this, clsActivity);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(clsActivity);
        stackBuilder.addNextIntent(intent);
        pendingIntent = stackBuilder.getPendingIntent(1, PendingIntent.FLAG_UPDATE_CURRENT);
    }


}