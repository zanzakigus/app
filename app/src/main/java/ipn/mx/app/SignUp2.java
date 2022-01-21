package ipn.mx.app;


import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.HashMap;

public class SignUp2 extends AppCompatActivity implements View.OnClickListener {

    EditText edtNombre, edtAPaterno, edtAMaterno, edtFNacimiento;
    View btnNext;

    String correo, contra;

    public static String SHARED_PREFS;
    public static String EMAIL_KEY;
    public static String PASSWORD_KEY;
    SharedPreferences sharedpreferences;

    private int anno, mes, dia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_2);

        correo = getIntent().getStringExtra("correo");
        contra = getIntent().getStringExtra("contra");

        edtNombre = findViewById(R.id.name_input);
        edtAPaterno = findViewById(R.id.a_paterno_input);
        edtAMaterno = findViewById(R.id.a_materno_input);
        edtFNacimiento = findViewById(R.id.fecha_nacimiento_input);
        btnNext = findViewById(R.id.arrow);

        // Quitar lo editable
        // https://stackoverflow.com/questions/3928711/how-to-make-edittext-not-editable-through-xml-in-android/6174440#6174440
        edtFNacimiento.setKeyListener(null);

        // Agregar accion para que aparezca el calendario
        edtFNacimiento.setOnClickListener(this);

        // Agregar accion al boton
        btnNext.setOnClickListener(this);

        SHARED_PREFS = this.getResources().getString(R.string.shared_key);
        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        EMAIL_KEY = this.getResources().getString(R.string.logged_email_key);
        PASSWORD_KEY = this.getResources().getString(R.string.logged_password_key);
    }

    @Override
    public void onClick(View v) {
        if (v == btnNext) {
            if (edtNombre.getText().toString().equals("")) {
                Toast myToast = Toast.makeText(this, R.string.missing_name, Toast.LENGTH_LONG);
                myToast.show();
            } else if (edtAPaterno.getText().toString().equals("")) {
                Toast myToast = Toast.makeText(this, R.string.missing_a_paterno, Toast.LENGTH_LONG);
                myToast.show();
            } else if (edtAMaterno.getText().toString().equals("")) {
                Toast myToast = Toast.makeText(this, R.string.missing_a_materno, Toast.LENGTH_LONG);
                myToast.show();
            } else if (edtFNacimiento.getText().toString().equals("")) {
                Toast myToast = Toast.makeText(this, R.string.missing_f_nacimiento, Toast.LENGTH_LONG);
                myToast.show();
            } else {

                PeticionAPI api = new PeticionAPI(this);
                HashMap<String, String> params = new HashMap<>();

                params.put("correo", correo);
                params.put("nombre", edtNombre.getText().toString());
                params.put("ap_paterno", edtAPaterno.getText().toString());
                params.put("ap_materno", edtAMaterno.getText().toString());
                params.put("password", contra);
                params.put("fecha_nacimiento", edtFNacimiento.getText().toString());

                SignUp2 signUp2 = new SignUp2();
                Class[] parameterTypes = new Class[2];
                parameterTypes[0] = JSONObject.class;
                parameterTypes[1] = Context.class;
                Method functionToPass = null;
                try {
                    functionToPass = SignUp2.class.getMethod("onSignUp", parameterTypes);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }

                api.peticionPOST(this.getResources().getString(R.string.server_host) + "/usuario", params, signUp2, functionToPass);
            }
        } else if (v == edtFNacimiento) {
            final Calendar c = Calendar.getInstance();
            dia = c.get(Calendar.DAY_OF_MONTH);
            mes = c.get(Calendar.MONTH);
            anno = c.get(Calendar.YEAR);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    String Day = formatoNumeroDosCifras(day);
                    String Month = formatoNumeroDosCifras(month + 1);
                    String fNacimiento = Day + "/" + Month + "/" + year;
                    edtFNacimiento.setText(fNacimiento);
                }
            }, dia, mes, anno);
            datePickerDialog.show();
        }
    }

    public void onSignUp(JSONObject response, Context context) throws JSONException {
        //context = context.getApplicationContext();
        System.out.println(context);
        if (response.has("error")) {
            Log.e("ERROR", "ERROR onSignUp: " + response.getString("error"));
            Toast myToast = Toast.makeText(context, "ERROR " + (500) + " onSignUp: " + response.getString("error"), Toast.LENGTH_LONG);
            myToast.show();
            return;
        }
        int status = response.getInt("status");
        String message = response.getString("message");
        if (status != 200) {
            Log.e("ERROR", "ERROR onSignUp: " + message);
            Toast myToast = Toast.makeText(context, "ERROR " + (status) + " onSignUp: " + message, Toast.LENGTH_LONG);
            myToast.show();
            return;
        }
        Toast myToast = Toast.makeText(context, "Usuario Creado", Toast.LENGTH_LONG);
        myToast.show();

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(EMAIL_KEY, correo);
        editor.putString(PASSWORD_KEY, contra);
        editor.apply();

        Intent intent = new Intent(context, Index.class);
        context.startActivity(intent);
        Log.i("INFO", "INFO: Usuario Creado");
        finish();
    }

    private String formatoNumeroDosCifras(int num) {
        return (num < 10) ? "0" + num : Integer.toString(num);
    }
}
