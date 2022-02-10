package ipn.mx.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import ipn.mx.app.global.GlobalInfo;
import ipn.mx.app.neurosky.NeuroSkyManager;

public class SettingHeadset extends AppCompatActivity implements View.OnClickListener {

    Button btnConnHs, btnDiscHs, btnClasify;
    Switch swtEnableNoti;
    TextView tvUserName;

    // variable for shared preferences.
    SharedPreferences sharedpreferences;

    //logged variables
    private String loggedEmail;
    private String loggedPassword;
    private String loggedNombre;


    // creating constant keys for shared preferences.
    public static String SHARED_PREFS;
    public static String EMAIL_KEY;
    public static String PASSWORD_KEY;
    public static String NOMBRE_KEY;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.headset_setting);

        btnConnHs = findViewById(R.id.conectar);
        btnDiscHs = findViewById(R.id.desconetar);
        btnClasify = findViewById(R.id.detectar_emocion);
        swtEnableNoti = findViewById(R.id.swt_notify_conn);
        tvUserName = findViewById(R.id.title_username);

        SHARED_PREFS = this.getResources().getString(R.string.shared_key);
        EMAIL_KEY = this.getResources().getString(R.string.logged_email_key);
        PASSWORD_KEY = this.getResources().getString(R.string.logged_password_key);
        NOMBRE_KEY = this.getResources().getString(R.string.logged_nombre);

        // initializing our shared preferences.
        sharedpreferences = getSharedPreferences(this.getResources().getString(R.string.shared_key), Context.MODE_PRIVATE);
        loggedEmail = sharedpreferences.getString(EMAIL_KEY, null);
        loggedPassword = sharedpreferences.getString(PASSWORD_KEY, null);
        loggedNombre = sharedpreferences.getString(NOMBRE_KEY, null);
        tvUserName.setText(loggedNombre);

        btnClasify.setOnClickListener(this);
        btnDiscHs.setOnClickListener(this);
        btnConnHs.setOnClickListener(this);

        swtEnableNoti.setOnClickListener(this);

        if (NeuroSkyManager.getNeuroSky() != null && NeuroSkyManager.getNeuroSky().isConnected()) {
            btnDiscHs.setVisibility(View.VISIBLE);
            btnClasify.setVisibility(View.VISIBLE);
            btnConnHs.setVisibility(View.GONE);
        } else {
            btnDiscHs.setVisibility(View.GONE);
            btnClasify.setVisibility(View.GONE);
            btnConnHs.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {

        if (btnConnHs == v) {
            Toast.makeText(this, "ss", Toast.LENGTH_SHORT).show();
        } else if (btnClasify == v) {
            Toast.makeText(this, "ss2", Toast.LENGTH_SHORT).show();
        } else if (btnConnHs == v) {
            Toast.makeText(this, "ss2", Toast.LENGTH_SHORT).show();
        } else if (swtEnableNoti == v) {
            GlobalInfo.setEnableNotifyConnHeadset(swtEnableNoti.isChecked());
        }


    }
}
