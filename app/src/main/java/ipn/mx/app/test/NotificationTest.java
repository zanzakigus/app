package ipn.mx.app.test;

import android.app.Dialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import ipn.mx.app.R;

public class NotificationTest extends AppCompatActivity implements View.OnClickListener {

    Button btnNotificacionExterna;
    Button btnNotificacionInterna;

    private PendingIntent pendingIntent;

    // Objetos del Dialog
    Dialog dialog;
    Button btnContinuarDialog;
    ImageView btnCloseDialog;

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
    }

    @Override
    public void onClick(View v) {
        if(v == btnNotificacionExterna){
            setPendingIntent(ClickNotification.class);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "NOTIFICACION");
            builder.setSmallIcon(R.drawable.icon_home);
            builder.setContentTitle("Notificacion de Prueba");
            builder.setContentText("Esto es una notificacion de prueba");

            // Agregamos vista que se redirige al darle click a la notificacion
            builder.setContentIntent(pendingIntent);

            // A partir de aqui la configuracion ya esta de mas
            builder.setColor(Color.BLUE);
            builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
            builder.setLights(Color.MAGENTA, 1000, 1000);
            builder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
            builder.setDefaults(Notification.DEFAULT_SOUND);

            // Una vez configurada la notificacion se procede a mandarla a llamar
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
            notificationManagerCompat.notify(0, builder.build());
        } else if(v == btnNotificacionInterna){
            dialog.setContentView(R.layout.alert_dialog_test);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            btnCloseDialog = dialog.findViewById(R.id.btn_close);
            btnContinuarDialog = dialog.findViewById(R.id.btn_continuar);

            btnCloseDialog.setOnClickListener(this);
            btnContinuarDialog.setOnClickListener(this);

            dialog.show();
        } else if(v == btnCloseDialog){
            dialog.dismiss();
        } else if(v == btnContinuarDialog){
            dialog.dismiss();
            Intent intent = new Intent(this, ClickNotification.class);
            startActivity(intent);
            finish();
        }
    }

    private void setPendingIntent(Class<?> clsActivity){
        Intent intent = new Intent(this, clsActivity);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(clsActivity);
        stackBuilder.addNextIntent(intent);
        pendingIntent = stackBuilder.getPendingIntent(1, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}