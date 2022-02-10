package ipn.mx.app.test;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import ipn.mx.app.R;

public class ClickNotification extends AppCompatActivity {

    Button tvNotificaionExterna;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_notification_click);
    }

}