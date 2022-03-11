package ipn.mx.app.signs;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import ipn.mx.app.Index;
import ipn.mx.app.R;

public class SignUp2 extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener {

    public static String SHARED_PREFS;
    public static String EMAIL_KEY;
    public static String PASSWORD_KEY;
    EditText edtNombre, edtAPaterno, edtAMaterno, edtFNacimiento;
    View btnNext;
    String correo, contra;
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
        edtFNacimiento.setOnFocusChangeListener(this);

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
                enterSignStrengths();

            }
        } else if (v == edtFNacimiento) {
            openCalendarDialog();
        }
    }

    public void onSignUp(JSONObject response, Context context) throws JSONException {
        //System.out.println(context);
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

        String Shared = context.getResources().getString(R.string.shared_key);
        SharedPreferences SharedP = context.getSharedPreferences(Shared, Context.MODE_PRIVATE);
        String emailKey = context.getResources().getString(R.string.logged_email_key);
        String passKey = context.getResources().getString(R.string.logged_password_key);

        SharedPreferences.Editor editor = SharedP.edit();
        editor.putString(emailKey, correo);
        editor.putString(passKey, contra);
        editor.apply();

        Intent intent = new Intent(context, Index.class);
        context.startActivity(intent);
        Log.i("INFO", "INFO: Usuario Creado");
        ((Activity) context).finish();
    }

    private String formatoNumeroDosCifras(int num) {
        return (num < 10) ? "0" + num : Integer.toString(num);
    }

    public void enterSignStrengths() {
        Intent intent = new Intent(this, SignUpStrengths.class);
        intent.putExtra("nombre", edtNombre.getText().toString());
        intent.putExtra("ap_paterno", edtAPaterno.getText().toString());
        intent.putExtra("ap_materno", edtAMaterno.getText().toString());
        intent.putExtra("fecha_nacimiento", edtFNacimiento.getText().toString());
        intent.putExtra("correo", correo);
        intent.putExtra("contra", contra);
        startActivity(intent);

    }


    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(v == edtFNacimiento && hasFocus){
            InputMethodManager imm =  (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            Toast myToast = Toast.makeText(this, R.string.focus_f_nacimiento, Toast.LENGTH_LONG);
            myToast.show();
            openCalendarDialog();
        }
    }

    public void openCalendarDialog(){
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
        }, anno, mes, dia);
        datePickerDialog.show();
    }
}
