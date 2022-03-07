package ipn.mx.app.neuralfit;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.HashMap;

import ipn.mx.app.Index;
import ipn.mx.app.InfoAppView;
import ipn.mx.app.Login;
import ipn.mx.app.PeticionAPI;
import ipn.mx.app.R;
import ipn.mx.app.SignUp2;
import ipn.mx.app.global.GlobalInfo;
import ipn.mx.app.neurosky.NeuroSkyManager;
import ipn.mx.app.neurosky.library.NeuroSky;
import ipn.mx.app.neurosky.library.exception.BluetoothNotEnabledException;
import ipn.mx.app.service.ClasifyService;
import ipn.mx.app.service.HeadsetConnectionService;

public class FitConnect extends AppCompatActivity implements View.OnClickListener {
    private final static String LOG_TAG = "NeuroSky";
    View btnNext, btnConnect;
    BluetoothAdapter bluetoothAdapter;
    NeuroSky neuroSky;


    //Http request variables
    RequestQueue queue;
    String host;
    // variable for shared preferences.
    SharedPreferences sharedpreferences;

    //logged variables
    private String loggedEmail;
    private String loggedPassword;

    // creating constant keys for shared preferences.
    public static String SHARED_PREFS;
    public static String EMAIL_KEY;
    public static String PASSWORD_KEY;
    public static String NOMBRE_KEY;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.section_fit_ini);

        NeuroSkyManager neuroSkyManager = new NeuroSkyManager(this);
        neuroSky = NeuroSkyManager.getNeuroSky();
        btnNext = findViewById(R.id.arrow);
        btnConnect = findViewById(R.id.button_connect);

        btnNext.setOnClickListener(this);
        btnConnect.setOnClickListener(this);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        host = this.getResources().getString(R.string.server_host);

        // initializing shared preferences keys.
        SHARED_PREFS = this.getResources().getString(R.string.shared_key);
        EMAIL_KEY = this.getResources().getString(R.string.logged_email_key);
        PASSWORD_KEY = this.getResources().getString(R.string.logged_password_key);
        NOMBRE_KEY = this.getResources().getString(R.string.logged_nombre);

        // initializing our shared preferences.
        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

        loggedEmail = sharedpreferences.getString(EMAIL_KEY, null);
        loggedPassword = sharedpreferences.getString(PASSWORD_KEY, null);

        if (loggedPassword == null || loggedEmail == null) {
            Intent intent = new Intent(this, Login.class);
            /*intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);*/
            startActivity(intent);
            finish();
            return;
        }

    }

    @Override
    public void onClick(View v) {
        if (v == btnNext) {
            if (!NeuroSkyManager.getNeuroSky().isConnected()) {
                Toast myToast = Toast.makeText(this, R.string.no_connect, Toast.LENGTH_LONG);
                myToast.show();
            } else {
                PeticionAPI api = new PeticionAPI(this);
                HashMap<String, String> params = new HashMap<>();
                params.put("correo", loggedEmail);
                params.put("password", loggedPassword);
                params.put("section_size", GlobalInfo.getTrainSectionTime() + "");

                FitConnect fitConnect = new FitConnect();
                Class[] parameterTypes = new Class[2];
                parameterTypes[0] = JSONObject.class;
                parameterTypes[1] = Context.class;
                Method functionToPass = null;

                try {
                    functionToPass = FitConnect.class.getMethod("existNeural", parameterTypes);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }

                api.peticionPOST(this.getResources().getString(R.string.server_host) + "/exist_neural", params, fitConnect, functionToPass);
            }
        } else if (v == btnConnect) {
            if (neuroSky != null && !neuroSky.isConnected()) {
                if (!bluetoothAdapter.isEnabled()) {
                    bluetoothAdapter.enable();
                }
                try {
                    neuroSky.connect();
                } catch (BluetoothNotEnabledException e) {
                    Toast myToast = Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG);
                    myToast.show();
                    Log.d(LOG_TAG, e.getMessage());
                }
            } else {
                Toast myToast = Toast.makeText(this, R.string.connected_diadema, Toast.LENGTH_LONG);
                myToast.show();
            }

        }
    }

    public void existNeural(JSONObject response, Context context) throws JSONException {
        if (response.has("error")) {
            Log.e("ERROR", "ERROR existNeural: " + response.getString("error"));
            Toast myToast = Toast.makeText(context, "ERROR " + (500) + " existNeural: " + response.getString("error"), Toast.LENGTH_LONG);
            myToast.show();
            return;
        }
        int status = response.getInt("status");
        String message = response.getString("message");
        if (status != 200) {
            Log.e("ERROR", "ERROR existNeural: " + message);
            Toast myToast = Toast.makeText(context, "ERROR " + (status) + " existNeural: " + message, Toast.LENGTH_LONG);
            myToast.show();
            return;
        }

        if (message.equals("None")) {
            Intent intent = new Intent(context, TrainNegativeVisual.class);
            context.startActivity(intent);
        } else {
            Intent intent = new Intent(context, IncompleteTraining.class);
            intent.putExtra("training", message);
            context.startActivity(intent);
        }
    }
}
