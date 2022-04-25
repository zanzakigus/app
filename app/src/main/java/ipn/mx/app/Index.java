package ipn.mx.app;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.HashMap;

import ipn.mx.app.global.GlobalInfo;
import ipn.mx.app.service.ClasifyService;
import ipn.mx.app.service.HeadsetConnectionService;
import ipn.mx.app.signs.Login;

public class Index extends AppCompatActivity implements View.OnClickListener {


    // creating constant keys for shared preferences.
    public static String SHARED_PREFS;
    public static String EMAIL_KEY;
    public static String PASSWORD_KEY;
    public static String NOMBRE_KEY;
    Context context;
    Button btnHome, btnGraph, btnNotification, btnUser;
    TextView tevNombreUsuario;
    VideoView vVIni;

    //Http request variables
    RequestQueue queue;
    String host;
    // variable for shared preferences.
    SharedPreferences sharedpreferences;

    //logged variables
    private String loggedEmail;
    private String loggedPassword;

    final String urlTutorial = "https://youtu.be/IxMpad5K-Og";
    TextView fullscreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);

        /*Intent hcs = new Intent(this, HeadsetConnectionService.class);
        startService(hcs);*/

        tevNombreUsuario = findViewById(R.id.title_username);

        btnHome = findViewById(R.id.icon_home);
        btnHome.setBackgroundResource(R.drawable.icon_home_outline);
        btnGraph = findViewById(R.id.icon_graph);
        btnNotification = findViewById(R.id.icon_notifications);
        btnUser = findViewById(R.id.icon_user);
        vVIni = findViewById(R.id.video_ini);
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/"
                + R.raw.video_ini);
        vVIni.setVideoURI(videoUri);

        MediaController mc = new MediaController(this);
        vVIni.setMediaController(mc);

        fullscreen = findViewById(R.id.tutorial_fullscreen);
        fullscreen.setOnClickListener(this);


        btnHome.setOnClickListener(this);
        btnGraph.setOnClickListener(this);
        btnNotification.setOnClickListener(this);
        btnUser.setOnClickListener(this);

        /*btnNext.setOnClickListener(this);*/
        queue = Volley.newRequestQueue(this);
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

        GlobalInfo.setIniEnableNotifyConnHeadset(this);

        if (loggedPassword == null || loggedEmail == null) {
            Log.i("A Login", "onCreate: Deberia de irme a login");
            Intent intent = new Intent(this, Login.class);
            /*intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);*/
            startActivity(intent);
            finish();
        } else {

            PeticionAPI api = new PeticionAPI(this);
            HashMap<String, String> params = new HashMap<>();
            params.put("correo", loggedEmail);
            params.put("password", loggedPassword);
            params.put("section_size", GlobalInfo.getTrainSectionTime() + "");

            Index index = new Index();
            index.loggedEmail = loggedEmail;
            index.loggedPassword = loggedPassword;
            Class[] parameterTypes = new Class[2];
            parameterTypes[0] = JSONObject.class;
            parameterTypes[1] = Context.class;
            Method functionToPass = null;

            try {
                functionToPass = Index.class.getMethod("existNeural", parameterTypes);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }

            api.peticionPOST(this.getResources().getString(R.string.server_host) + "/exist_neural", params, index, functionToPass);
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
        }else if (fullscreen == v) {
            Uri urlVideo = Uri.parse(urlTutorial);
            Intent videoIntent = new Intent(Intent.ACTION_VIEW, urlVideo);
            try {
                startActivity(videoIntent);
            } catch (ActivityNotFoundException e) {
                Log.d("TutorialHeadset", "Fullscreen onClick: " + e.toString());
                Toast.makeText(this, getResources().getString(R.string.tutorial_headset_fullscreen_error), Toast.LENGTH_LONG).show();
            }
        }
    }

    public void localSaveUserInfo(JSONObject response, Context context) throws JSONException {
        if (response.has("error")) {
            Log.e("ERROR", "ERROR localSaveUserInfo: " + response.getString("error"));
            Toast myToast = Toast.makeText(context, "ERROR " + (500) + " localSaveUserInfo: " + response.getString("error"), Toast.LENGTH_LONG);
            myToast.show();
            return;
        }
        int status = response.getInt("status");
        String message = response.getString("message");
        if (status != 200) {
            Log.e("ERROR", "ERROR localSaveUserInfo: " + message);
            Toast myToast = Toast.makeText(context, "ERROR " + (status) + " localSaveUserInfo: " + message, Toast.LENGTH_LONG);
            myToast.show();
            return;
        }
        JSONObject usuarioJSON = response.getJSONObject("contenido");
        String Shared = context.getResources().getString(R.string.shared_key);
        SharedPreferences SharedP = context.getSharedPreferences(Shared, Context.MODE_PRIVATE);
        String nombreKey = context.getResources().getString(R.string.logged_nombre);

        SharedPreferences.Editor editor = SharedP.edit();
        Log.d("Buenas", "localSaveUserInfo: " + usuarioJSON.getString("nombre"));
        editor.putString(nombreKey, usuarioJSON.getString("nombre") + " " + usuarioJSON.getString("ap_paterno") + " " + usuarioJSON.getString("ap_materno"));
        editor.apply();
        ((TextView) ((Activity) context).findViewById(R.id.title_username)).setText(SharedP.getString(nombreKey, "Sin nombre"));
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
        if (message.equals("None") || !message.equals("OK")) {

            Intent intent = new Intent(context, InfoAppView.class);
            context.startActivity(intent);
            Log.i("INFO", "INFO: No neural file found");
            ((Activity) context).finish();
            Toast.makeText(context, R.string.no_neural, Toast.LENGTH_LONG).show();
        } else {
            PeticionAPI api = new PeticionAPI(context);
            HashMap<String, String> params = new HashMap<>();
            params.put("correo", loggedEmail);
            params.put("password", loggedPassword);

            Index index = new Index();
            Class[] parameterTypes = new Class[2];
            parameterTypes[0] = JSONObject.class;
            parameterTypes[1] = Context.class;
            Method functionToPass = null;

            try {
                functionToPass = Index.class.getMethod("localSaveUserInfo", parameterTypes);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            if (!HeadsetConnectionService.isIsIntentServiceRunning()) {
                Intent hcs = new Intent(context, HeadsetConnectionService.class);
                context.startService(hcs);
                Log.i("INFO", "INFO: Servicio verificar conexion diadema iniciado");
            }
            if (!ClasifyService.isIntentServiceRunning()) {
                Intent hcs = new Intent(context, ClasifyService.class);
                context.startService(hcs);
                Log.i("INFO", "INFO: Servicio clasificacion de ondas cereblares iniciado");
            }

            api.peticionGET(context.getResources().getString(R.string.server_host) + "/usuario", params, index, functionToPass);
        }
    }
}
