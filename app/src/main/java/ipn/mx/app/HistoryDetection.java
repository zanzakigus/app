package ipn.mx.app;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.HashMap;

public class HistoryDetection extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = "HistoryDetection";

    Button btnHome, btnGraph, btnNotification, btnUser;
    LinearLayout vertical_scroll;
    Switch aSwitch;
    EditText fecha_ini, fecha_fin;


    Context context;

    // variable for shared preferences.
    SharedPreferences sharedpreferences;

    //logged variables
    public static String SHARED_PREFS;
    public static String EMAIL_KEY;
    public static String PASSWORD_KEY;
    public static String NOMBRE_KEY;
    private String loggedEmail;
    private String loggedPassword;
    private String loggedNombre;

    private int anno_ini, mes_ini, dia_ini, anno_fin, mes_fin, dia_fin;
    String fInicial, fFinal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_detections);

        context = this;

        btnHome = findViewById(R.id.icon_home);
        btnGraph = findViewById(R.id.icon_graph);
        btnNotification = findViewById(R.id.icon_notifications);
        btnUser = findViewById(R.id.icon_user);
        vertical_scroll = findViewById(R.id.vertical_scroll);
        aSwitch = findViewById(R.id.swt_history);
        fecha_ini = findViewById(R.id.fecha_ini_input);
        fecha_fin = findViewById(R.id.fecha_fin_input);

        fecha_ini.setKeyListener(null);
        fecha_fin.setKeyListener(null);


        fecha_ini.setOnClickListener(this);
        fecha_fin.setOnClickListener(this);
        btnHome.setOnClickListener(this);
        btnGraph.setOnClickListener(this);
        btnNotification.setOnClickListener(this);
        btnUser.setOnClickListener(this);


        final Calendar c = Calendar.getInstance();
        dia_fin = c.get(Calendar.DAY_OF_MONTH);
        mes_fin = c.get(Calendar.MONTH);
        anno_fin = c.get(Calendar.YEAR);

        c.add(Calendar.DAY_OF_YEAR, -7);
        dia_ini = c.get(Calendar.DAY_OF_MONTH);
        mes_ini = c.get(Calendar.MONTH);
        anno_ini = c.get(Calendar.YEAR);


        String Day = formatoNumeroDosCifras(dia_ini);
        String Month = formatoNumeroDosCifras(mes_ini + 1);
        fInicial = Day + "/" + Month + "/" + anno_ini;
        fecha_ini.setText(fInicial);


        Day = formatoNumeroDosCifras(dia_fin);
        Month = formatoNumeroDosCifras(mes_fin + 1);
        fFinal = Day + "/" + Month + "/" + anno_fin;
        fecha_fin.setText(fFinal);


        // initializing shared preferences keys.
        SHARED_PREFS = this.getResources().getString(R.string.shared_key);
        EMAIL_KEY = this.getResources().getString(R.string.logged_email_key);
        PASSWORD_KEY = this.getResources().getString(R.string.logged_password_key);
        NOMBRE_KEY = this.getResources().getString(R.string.logged_nombre);

        // initializing our shared preferences.
        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

        loggedEmail = sharedpreferences.getString(EMAIL_KEY, null);
        loggedPassword = sharedpreferences.getString(PASSWORD_KEY, null);

        getHistory();

    }

    @Override
    public void onClick(View v) {

        // Esto no afecta nada en el codigo, lo pongo aqui para copiarlo y pegarlo en las demas Clases
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
        } else if (fecha_ini == v) {
            Log.d(TAG, "onClick()-fecha_ini: ");

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                    Calendar ini = Calendar.getInstance();
                    ini.set(Calendar.DAY_OF_MONTH, day);
                    ini.set(Calendar.MONTH, month);  // 0-11 so 1 less
                    ini.set(Calendar.YEAR, year);

                    Calendar fin = Calendar.getInstance();
                    fin.set(Calendar.DAY_OF_MONTH, dia_fin);
                    fin.set(Calendar.MONTH, mes_fin);
                    fin.set(Calendar.YEAR, anno_fin);
                    long diff = fin.getTimeInMillis() - ini.getTimeInMillis();
                    Log.d(TAG, "onClick()-fecha_ini: " + diff);
                    if (diff < 0) {
                        Toast myToast = Toast.makeText(context, R.string.text_date_no_mayor_ini, Toast.LENGTH_LONG);
                        myToast.show();
                    } else {
                        String Day = formatoNumeroDosCifras(day);
                        String Month = formatoNumeroDosCifras(month + 1);
                        fInicial = Day + "/" + Month + "/" + year;
                        fecha_ini.setText(fInicial);
                        anno_ini = year;
                        mes_ini = month;
                        dia_ini = day;
                        getHistory();

                    }


                }
            }, anno_ini, mes_ini, dia_ini);
            datePickerDialog.show();
        } else if (fecha_fin == v) {
            Log.d(TAG, "onClick()-fecha_fin: ");
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                    Calendar ini = Calendar.getInstance();
                    ini.set(Calendar.DAY_OF_MONTH, dia_ini);
                    ini.set(Calendar.MONTH, mes_ini);  // 0-11 so 1 less
                    ini.set(Calendar.YEAR, anno_ini);

                    Calendar fin = Calendar.getInstance();
                    fin.set(Calendar.DAY_OF_MONTH, day);
                    fin.set(Calendar.MONTH, month);
                    fin.set(Calendar.YEAR, year);
                    long diff = fin.getTimeInMillis() - ini.getTimeInMillis();
                    if (diff < 0) {
                        Toast myToast = Toast.makeText(context, R.string.text_date_no_mayor_fin, Toast.LENGTH_LONG);
                        myToast.show();
                    } else {
                        String Day = formatoNumeroDosCifras(day);
                        String Month = formatoNumeroDosCifras(month + 1);
                        fFinal = Day + "/" + Month + "/" + year;
                        fecha_fin.setText(fFinal);
                        anno_fin = year;
                        mes_fin = month;
                        dia_fin = day;
                        getHistory();
                    }


                }
            }, anno_fin, mes_fin, dia_fin);
            datePickerDialog.show();
        } else if (aSwitch == v) {
            getHistory();
        }
    }

    private String formatoNumeroDosCifras(int num) {
        return (num < 10) ? "0" + num : Integer.toString(num);
    }

    private void getHistory() {
        Log.d(TAG, "getHistory() ");
        PeticionAPI api = new PeticionAPI(context);
        HashMap<String, String> params = new HashMap<>();
        params.put("correo", loggedEmail);
        params.put("password", loggedPassword);
        params.put("fecha_ini", fInicial);
        params.put("fecha_fin", fFinal);

        HistoryDetection historyDetection = new HistoryDetection();
        historyDetection.vertical_scroll = vertical_scroll;
        Class[] parameterTypes = new Class[2];
        parameterTypes[0] = JSONObject.class;
        parameterTypes[1] = Context.class;
        Method functionToPass = null;
        String functionName = aSwitch.isChecked() ? "grafica" : "listado";
        try {
            functionToPass = HistoryDetection.class.getMethod(functionName, parameterTypes);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        api.peticionGET(this.getResources().getString(R.string.server_host) + "/emocion_detectada", params, historyDetection, functionToPass);
    }

    public void listado(JSONObject response, Context context) throws JSONException {
        Log.d(TAG, "lstado() ");
        if (response.has("error")) {
            Log.e(TAG, "ERROR listado: " + response.getString("error"));
            Toast myToast = Toast.makeText(context, "ERROR " + (500) + " listado: " + response.getString("error"), Toast.LENGTH_LONG);
            myToast.show();
            return;
        }
        int status = response.getInt("status");

        if (status != 200) {
            String message = response.getString("message");
            Log.e(TAG, "ERROR listado: " + message);
            Toast myToast = Toast.makeText(context, "ERROR " + (status) + " listado: " + message, Toast.LENGTH_LONG);
            myToast.show();
            return;
        }

        JSONArray emociones = response.getJSONArray("message");

        Log.d(TAG, "lstado() " + emociones.length());
        for (int i = 0; i < emociones.length(); i++) {
            ConstraintLayout emocion = (ConstraintLayout) LayoutInflater.from(context).inflate(R.layout.component_detection, null);

            ConstraintLayout.LayoutParams newLayoutParams =  new ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.WRAP_CONTENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT);
            newLayoutParams.topMargin = 20;
            emocion.setLayoutParams(newLayoutParams);

            TextView fecha = emocion.findViewById(R.id.text_date);
            fecha.setText(emociones.getJSONObject(i).getString("fecha_deteccion"));
            vertical_scroll.addView(emocion);
        }


    }

}
