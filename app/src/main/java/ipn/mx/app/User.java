package ipn.mx.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.HashMap;

import ipn.mx.app.service.ClasifyService;
import ipn.mx.app.signs.Login;
import ipn.mx.app.updateinfo.CodePassword;
import ipn.mx.app.updateinfo.UpdateInfo;

public class User extends AppCompatActivity implements View.OnClickListener, DialogInterface.OnClickListener {

    // creating constant keys for shared preferences.
    public static String SHARED_PREFS;
    public static String EMAIL_KEY;
    public static String PASSWORD_KEY;
    public static String NOMBRE_KEY;
    // variable for shared preferences.
    static SharedPreferences sharedpreferences;
    Context cGlobal;
    // Action Bar
    Button btnHome, btnGraph, btnNotification, btnUser;
    // View
    TextView tvUsername, tvFNacimiento, tvEmail, tvDetUltimaSemana, tvDetTotal;
    Button btnUpdateInfo, btnLogOut, btnUpdatePassword;
    //logged variables
    private String loggedEmail;
    private String loggedPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user);

        cGlobal = this;

        // Action Bar
        btnHome = findViewById(R.id.icon_home);
        btnGraph = findViewById(R.id.icon_graph);
        btnNotification = findViewById(R.id.icon_notifications);
        btnUser = findViewById(R.id.icon_user);

        btnHome.setOnClickListener(this);
        btnGraph.setOnClickListener(this);
        btnNotification.setOnClickListener(this);
        btnUser.setOnClickListener(this);

        // View
        tvUsername = findViewById(R.id.title_username);
        tvFNacimiento = findViewById(R.id.text_f_nacimiento);
        tvEmail = findViewById(R.id.text_correo);
        tvDetUltimaSemana = findViewById(R.id.text_det_ultima_semana);
        tvDetTotal = findViewById(R.id.text_det_total);

        btnUpdateInfo = findViewById(R.id.editar_info);
        btnLogOut = findViewById(R.id.cerrar_sesion);
        btnUpdatePassword = findViewById(R.id.cambiar_contra);

        btnUpdateInfo.setOnClickListener(this);
        btnLogOut.setOnClickListener(this);
        btnUpdateInfo.setOnClickListener(this);
        btnUpdatePassword.setOnClickListener(this);

        // Obtener la informacion del usuario a mostrar

        // initializing shared preferences keys.
        SHARED_PREFS = this.getResources().getString(R.string.shared_key);
        EMAIL_KEY = this.getResources().getString(R.string.logged_email_key);
        PASSWORD_KEY = this.getResources().getString(R.string.logged_password_key);
        NOMBRE_KEY = this.getResources().getString(R.string.logged_nombre);

        // initializing our shared preferences.
        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

        loggedEmail = sharedpreferences.getString(EMAIL_KEY, null);
        loggedPassword = sharedpreferences.getString(PASSWORD_KEY, null);

        // Se valida que haya la seccion sea correcta
        if (loggedPassword == null || loggedEmail == null) {
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
            finish();
            return;
        }

        // Se hace peticion para obtener la informacion del usuario
        PeticionAPI api = new PeticionAPI(this);
        HashMap<String, String> params = new HashMap<>();
        params.put("correo", loggedEmail);
        params.put("password", loggedPassword);

        User user = new User();
        user.tvDetTotal = tvDetTotal;
        user.tvDetUltimaSemana = tvDetUltimaSemana;
        Class[] parameterTypes = new Class[2];
        parameterTypes[0] = JSONObject.class;
        parameterTypes[1] = Context.class;
        Method functionToPass = null;
        try {
            functionToPass = User.class.getMethod("putInfoOnScreen", parameterTypes);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        api.peticionGET(this.getResources().getString(R.string.server_host) + "/usuario", params, user, functionToPass);
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
        } else if (btnUpdateInfo == v) {
            Intent intent = new Intent(this, UpdateInfo.class);
            startActivity(intent);
        } else if (btnLogOut == v) {
            logOut();
        } else if (btnUpdatePassword == v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.user_dialog_message)
                    .setTitle(R.string.user_dialog_title)
                    .setPositiveButton(R.string.user_dialog_affirmative, this)
                    .setNegativeButton(R.string.user_dialog_negative, this);
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    // Evento de los Clicks del Dialog Alert
    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        // Cuando i = -1: Botón Afirmativo
        // Cuando i = -2: Botón Negativo
        // Cuando el usuario le da al botón negativo no es necesario poner código
        // El dialog se cierra de forma automatica
        if (i == -1) {
            Intent intent = new Intent(this, CodePassword.class);
            startActivity(intent);
        }
    }

    public void putInfoOnScreen(JSONObject response, Context context) throws JSONException {
        if (response.has("error")) {
            Log.e("ERROR", "ERROR putInfoOnScreen: " + response.getString("error"));
            Toast myToast = Toast.makeText(context, "ERROR " + (500) + " putInfoOnScreen: " + response.getString("error"), Toast.LENGTH_LONG);
            myToast.show();
            return;
        }

        int status = response.getInt("status");
        String message = response.getString("message");
        if (status != 200) {
            Log.e("ERROR", "ERROR putInfoOnScreen: " + message);
            Toast myToast = Toast.makeText(context, "ERROR " + (status) + " putInfoOnScreen: " + message, Toast.LENGTH_LONG);
            myToast.show();
            return;
        }

        JSONObject userData = response.getJSONObject("contenido");
        String username = userData.getString("nombre") + " " + userData.getString("ap_paterno") + " " + userData.getString("ap_materno");
        int statistics_all = response.getInt("statistics_all");
        int statistics_week = response.getInt("statistics_week");
        String detecion_all = " detecciones en total";
        String detecion_week = " detecciones en última semana";
        if (!(statistics_all > 0)) {
            detecion_all = " detección en total";
        }
        if (!(statistics_week > 0)) {
            detecion_week = " detección en última semana";
        }
        detecion_week = statistics_week + detecion_week;
        detecion_all = statistics_all + detecion_all;
        if (statistics_week == 0) {
            detecion_week = "Ninguna detección en última semana";
        }
        if (statistics_all == 0) {
            detecion_all = "Ninguna detección";
        }
        // Poner el texto a los TextView
        // Se hace de este modo debido a que esta funcion se corre despues del onCreate, entonces no comparte
        // las variables de esta funcion, es principalmente por eso que se manda en Context por parametro
        ((TextView) ((Activity) context).findViewById(R.id.title_username)).setText(username);
        ((TextView) ((Activity) context).findViewById(R.id.text_f_nacimiento)).setText(userData.getString("correo"));
        ((TextView) ((Activity) context).findViewById(R.id.text_correo)).setText(userData.getString("fecha_nacimiento"));


        tvDetUltimaSemana.setText(detecion_week);
        tvDetTotal.setText(detecion_all);
    }

    private void logOut() {
        // Cerramos la sesion en el telefono
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear();
        editor.apply();

        Intent stopHeadset = new Intent(this, ClasifyService.class);
        stopService(stopHeadset);

        /*if (HeadsetConnectionService.isIsIntentServiceRunning()){
            Intent hcs = new Intent(this, HeadsetConnectionService.class);
            stopService(hcs);
        }*/

        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
        finish();
    }
}
