package ipn.mx.app.test;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.HashMap;

import ipn.mx.app.R;

public class NotificationTest extends AppCompatActivity implements View.OnClickListener {

    Button tvNotificaionExterna;

    private PendingIntent pendingIntent;

    // TODO: Si se desea programar una notificacion se hace esto
    // TODO: https://www.youtube.com/watch?v=REJ3pDLGTmA

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_test);

        tvNotificaionExterna = findViewById(R.id.btn_notificacion_externa);
        tvNotificaionExterna.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v == tvNotificaionExterna){
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