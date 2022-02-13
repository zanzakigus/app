package ipn.mx.app;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import ipn.mx.app.global.GlobalInfo;
import ipn.mx.app.neurosky.NeuroSkyManager;
import ipn.mx.app.neurosky.library.NeuroSky;
import ipn.mx.app.neurosky.library.exception.BluetoothNotEnabledException;
import ipn.mx.app.notification.GlobalNotificationManager;
import ipn.mx.app.notification.mock.NotificationManagerData;
import ipn.mx.app.service.HeadsetConnectionService;

public class SettingHeadset extends AppCompatActivity implements View.OnClickListener {


    private final String TAG = "SettingHeadset";

    Button btnConnHs, btnDiscHs, btnClasify, btnHome, btnGraph, btnNotification, btnUser;
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

    private NeuroSky neuroSky;
    BluetoothAdapter bluetoothAdapter;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.headset_setting);

        btnHome = findViewById(R.id.icon_home);
        btnGraph = findViewById(R.id.icon_graph);
        btnNotification = findViewById(R.id.icon_notifications);
        btnUser = findViewById(R.id.icon_user);

        btnHome.setOnClickListener(this);
        btnGraph.setOnClickListener(this);
        btnNotification.setOnClickListener(this);
        btnUser.setOnClickListener(this);

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

        swtEnableNoti.setChecked(GlobalInfo.isEnableNotifyConnHeadset());

        btnClasify.setOnClickListener(this);
        btnDiscHs.setOnClickListener(this);
        btnConnHs.setOnClickListener(this);

        swtEnableNoti.setOnClickListener(this);

        new NeuroSkyManager(this);
        neuroSky = NeuroSkyManager.getNeuroSky();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

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

        if (btnHome == v) {
            Intent intent = new Intent(this, Index.class);
            startActivity(intent);
        } else if (btnGraph == v) {
            Intent intent = new Intent(this, HistoryDetection.class);
            startActivity(intent);
        } else if (btnNotification == v) {
            Intent intent = new Intent(this, SettingHeadset.class);
            startActivity(intent);
        } else if (btnUser == v) {
            Intent intent = new Intent(this, User.class);
            startActivity(intent);
        }

        else if (btnConnHs == v) {
            Log.d(TAG, "onClick()-btnConnHs: ");
            if (neuroSky != null && !neuroSky.isConnected()) {
                if (!bluetoothAdapter.isEnabled()) {
                    bluetoothAdapter.enable();
                }

                try {
                    neuroSky.connect();

                    final Handler handler = new Handler();
                    Context context = this;
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // set the limitations for the numeric
                            // text under the progress bar
                            if (!neuroSky.isConnected()) {
                                handler.postDelayed(this, 1000);
                            } else {
                                btnDiscHs.setVisibility(View.VISIBLE);
                                btnClasify.setVisibility(View.VISIBLE);
                                btnConnHs.setVisibility(View.GONE);
                                neuroSky.start();
                                handler.removeCallbacks(this);
                                Toast myToast = Toast.makeText(context, R.string.text_sett_conn, Toast.LENGTH_LONG);
                                myToast.show();
                            }
                        }
                    }, 1000);


                } catch (BluetoothNotEnabledException e) {
                    Log.d(TAG, e.getMessage());
                }
            } else {
                Toast myToast = Toast.makeText(this, R.string.text_sett_conn, Toast.LENGTH_LONG);
                myToast.show();
                neuroSky.start();
                btnDiscHs.setVisibility(View.VISIBLE);
                btnClasify.setVisibility(View.VISIBLE);
                btnConnHs.setVisibility(View.GONE);
            }

        } else if (btnClasify == v) {
            Log.d(TAG, "onClick()-btnClasify: ");
            if (neuroSky != null && neuroSky.isConnected()) {
                NeuroSkyManager.enviarWavesIdentificar();
            }else {
                Toast myToast = Toast.makeText(this, R.string.no_connect, Toast.LENGTH_LONG);
                myToast.show();
            }


        } else if (btnDiscHs == v) {
            Log.d(TAG, "onClick()-btnDiscHs: ");
            if (neuroSky != null && neuroSky.isConnected()) {
                try {
                    neuroSky.disconnect();
                    final Handler handler = new Handler();
                    Context context = this;
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // set the limitations for the numeric
                            // text under the progress bar
                            if (neuroSky.isConnected()) {
                                handler.postDelayed(this, 1000);
                            } else {
                                btnDiscHs.setVisibility(View.GONE);
                                btnClasify.setVisibility(View.GONE);
                                btnConnHs.setVisibility(View.VISIBLE);
                                Toast myToast = Toast.makeText(context, R.string.text_sett_disconn, Toast.LENGTH_LONG);
                                myToast.show();
                                handler.removeCallbacks(this);
                            }
                        }
                    }, 1000);


                } catch (BluetoothNotEnabledException e) {
                    Log.d(TAG, e.getMessage());
                }
            } else {
                Toast myToast = Toast.makeText(this, R.string.text_sett_disconn, Toast.LENGTH_LONG);
                myToast.show();
                btnDiscHs.setVisibility(View.GONE);
                btnClasify.setVisibility(View.GONE);
                btnConnHs.setVisibility(View.VISIBLE);
            }
        } else if (swtEnableNoti == v) {
            Log.d(TAG, "onClick()-swtEnableNoti: ");
            GlobalInfo.setEnableNotifyConnHeadset(swtEnableNoti.isChecked(), this);
            if (!HeadsetConnectionService.isIsIntentServiceRunning()) {
                Intent hcs = new Intent(this, HeadsetConnectionService.class);
                startService(hcs);
                Log.d(TAG, "onClick()-btnDiscHs:  HeadsetConnectionService inicializado");
            }
        }


    }
}
