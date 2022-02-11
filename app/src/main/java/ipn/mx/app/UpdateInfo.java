package ipn.mx.app;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.HashMap;

public class UpdateInfo extends AppCompatActivity implements View.OnClickListener {

    // creating constant keys for shared preferences.
    public static String SHARED_PREFS;
    public static String EMAIL_KEY;
    public static String PASSWORD_KEY;
    public static String NOMBRE_KEY;
    // Action Bar
    Button btnHome, btnGraph, btnNotification, btnUser;
    // View
    EditText edtNombre, edtAPaterno, edtAMaterno, edtFNacimiento;
    Button btnActualizar;
    // variable for shared preferences.
    SharedPreferences sharedpreferences;

    //logged variables
    private String loggedEmail;
    private String loggedPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_info);

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
        edtNombre = findViewById(R.id.name_input);
        edtAPaterno = findViewById(R.id.a_paterno_input);
        edtAMaterno = findViewById(R.id.a_materno_input);
        edtFNacimiento = findViewById(R.id.fecha_nacimiento_input);
        btnActualizar = findViewById(R.id.editar_info);

        // Quitar lo editable
        // https://stackoverflow.com/questions/3928711/how-to-make-edittext-not-editable-through-xml-in-android/6174440#6174440
        edtFNacimiento.setKeyListener(null);

        edtFNacimiento.setOnClickListener(this);
        btnActualizar.setOnClickListener(this);

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
            startActivity(intent);
            finish();
            return;
        }

        // Se hace peticion para obtener la informacion del usuario
        PeticionAPI api = new PeticionAPI(this);
        HashMap<String, String> params = new HashMap<>();
        params.put("correo", loggedEmail);
        params.put("password", loggedPassword);

        UpdateInfo updateInfo = new UpdateInfo();
        Class[] parameterTypes = new Class[2];
        parameterTypes[0] = JSONObject.class;
        parameterTypes[1] = Context.class;
        Method functionToPass = null;
        try {
            functionToPass = UpdateInfo.class.getMethod("putInfoOnScreen", parameterTypes);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        api.peticionGET(this.getResources().getString(R.string.server_host) + "/usuario", params, updateInfo, functionToPass);
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
        } else if (btnActualizar == v) {
            handleUpdateButton();
        } else if (edtFNacimiento == v) {
            String strFNacimiento = edtFNacimiento.getText().toString();
            int dia = Integer.parseInt(strFNacimiento.split("/")[0]);
            int mes = Integer.parseInt(strFNacimiento.split("/")[1]) - 1;
            int anno = Integer.parseInt(strFNacimiento.split("/")[2]);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    String Day = formatoNumeroDosCifras(day);
                    String Month = formatoNumeroDosCifras(month + 1);
                    String fNacimiento = Day + "/" + Month + "/" + year;
                    edtFNacimiento.setText(fNacimiento);
                }
            }, anno, mes, dia);
            datePickerDialog.show();
        }
    }

    private void handleUpdateButton() {
        String nombre = edtNombre.getText().toString();
        String aPaterno = edtAPaterno.getText().toString();
        String aMaterno = edtAMaterno.getText().toString();
        String fNacimiento = edtFNacimiento.getText().toString();
        Log.d("info", nombre + aPaterno + aMaterno + fNacimiento);

        if (nombre.equals("")) {
            Toast myToast = Toast.makeText(this, R.string.missing_name, Toast.LENGTH_LONG);
            myToast.show();
            return;
        }
        if (aPaterno.equals("")) {
            Toast myToast = Toast.makeText(this, R.string.missing_a_paterno, Toast.LENGTH_LONG);
            myToast.show();
            return;
        }
        if (aMaterno.equals("")) {
            Toast myToast = Toast.makeText(this, R.string.missing_a_materno, Toast.LENGTH_LONG);
            myToast.show();
            return;
        }
        if (fNacimiento.equals("")) {
            Toast myToast = Toast.makeText(this, R.string.missing_f_nacimiento, Toast.LENGTH_LONG);
            myToast.show();
            return;
        }

        // Se hace peticion para obtener la informacion del usuario
        PeticionAPI api = new PeticionAPI(this);
        HashMap<String, String> params = new HashMap<>();
        params.put("correo", loggedEmail);
        params.put("password", loggedPassword);
        params.put("nombre", nombre);
        params.put("ap_paterno", aPaterno);
        params.put("ap_materno", aMaterno);
        params.put("fecha_nacimiento", fNacimiento);

        UpdateInfo updateInfo = new UpdateInfo();
        Class[] parameterTypes = new Class[2];
        parameterTypes[0] = JSONObject.class;
        parameterTypes[1] = Context.class;
        Method functionToPass = null;
        try {
            functionToPass = UpdateInfo.class.getMethod("callbackUpdateInfo", parameterTypes);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        api.peticionPUT(this.getResources().getString(R.string.server_host) + "/usuario", params, updateInfo, functionToPass);
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
            Log.w("WARNING", "WARNING putInfoOnScreen: " + message);
            Toast myToast = Toast.makeText(context, "ERROR " + (status) + " putInfoOnScreen: " + message, Toast.LENGTH_LONG);
            myToast.show();
            return;
        }

        JSONObject userData = response.getJSONObject("contenido");

        // Poner el texto a los TextView
        // Se hace de este modo debido a que esta funcion se corre despues del onCrete, entonces no comparte
        // las variables de esta funcion, es principalmente por eso que se manda en Context por parametro
        ((TextView) ((Activity) context).findViewById(R.id.name_input)).setText(userData.getString("nombre"));
        ((TextView) ((Activity) context).findViewById(R.id.a_paterno_input)).setText(userData.getString("ap_paterno"));
        ((TextView) ((Activity) context).findViewById(R.id.a_materno_input)).setText(userData.getString("ap_materno"));
        ((TextView) ((Activity) context).findViewById(R.id.fecha_nacimiento_input)).setText(userData.getString("fecha_nacimiento"));
    }

    public void callbackUpdateInfo(JSONObject response, Context context) throws JSONException {
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

        Log.d("callbackUpdateInfo", "Informacion del Usuario actualizada");
        Toast myToast = Toast.makeText(context, R.string.update_info_success, Toast.LENGTH_LONG);
        myToast.show();
    }

    private String formatoNumeroDosCifras(int num) {
        return (num < 10) ? "0" + num : Integer.toString(num);
    }
}