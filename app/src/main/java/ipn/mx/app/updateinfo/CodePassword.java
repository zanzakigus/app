package ipn.mx.app.updateinfo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.BaseInputConnection;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.HashMap;

import ipn.mx.app.PeticionAPI;
import ipn.mx.app.R;
import ipn.mx.app.signs.Login;

public class CodePassword extends AppCompatActivity implements View.OnClickListener, TextWatcher {

    public static String codePasswordServer;
    // creating constant keys for shared preferences.
    public static String SHARED_PREFS;
    public static String EMAIL_KEY;
    public static String PASSWORD_KEY;
    public static String NOMBRE_KEY;
    EditText edtNumber1, edtNumber2, edtNumber3, edtNumber4, edtNumber5, edtNumber6;
    Button btnConfirmar;
    String codePasswordUser;
    int intentsCode = 1;
    // variable for shared preferences.
    SharedPreferences sharedpreferences;

    //logged variables
    private String loggedEmail;
    private String loggedPassword;

    public static void getCodePassword(JSONObject response, Context context) throws JSONException {
        if (response.has("error")) {
            Log.e("ERROR", "ERROR getCodePassword: " + response.getString("error"));
            Toast.makeText(context, "ERROR " + (500) + " getCodePassword: " + response.getString("error"), Toast.LENGTH_LONG).show();
            return;
        }
        int status = response.getInt("status");
        String message = response.getString("message");
        if (status != 200) {
            Log.e("ERROR", "ERROR getCodePassword: " + message);
            Toast.makeText(context, "ERROR " + (status) + " getCodePassword: " + message, Toast.LENGTH_LONG).show();
            return;
        }
        codePasswordServer = response.getString("numbers");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.code_password);

        edtNumber1 = findViewById(R.id.number_1);
        edtNumber2 = findViewById(R.id.number_2);
        edtNumber3 = findViewById(R.id.number_3);
        edtNumber4 = findViewById(R.id.number_4);
        edtNumber5 = findViewById(R.id.number_5);
        edtNumber6 = findViewById(R.id.number_6);
        btnConfirmar = findViewById(R.id.btn_confirmar);

        edtNumber1.addTextChangedListener(this);
        edtNumber2.addTextChangedListener(this);
        edtNumber3.addTextChangedListener(this);
        edtNumber4.addTextChangedListener(this);
        edtNumber5.addTextChangedListener(this);
        edtNumber6.addTextChangedListener(this);
        btnConfirmar.setOnClickListener(this);

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

        PeticionAPI api = new PeticionAPI(this);
        HashMap<String, String> params = new HashMap<>();
        params.put("correo", loggedEmail);
        params.put("password", loggedPassword);

        CodePassword codePassword = new CodePassword();
        Class[] parameterTypes = new Class[2];
        parameterTypes[0] = JSONObject.class;
        parameterTypes[1] = Context.class;
        Method functionToPass = null;
        try {
            functionToPass = CodePassword.class.getMethod("getCodePassword", parameterTypes);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        api.peticionGET(this.getResources().getString(R.string.server_host) + "/password", params, codePassword, functionToPass);
    }

    @Override
    public void onClick(View v) {
        if (btnConfirmar == v) {
            intentsCode--;
            // Validamos que todos los campos hayan sido llenados
            if (!getUserCode()) {
                if (intentsCode == -1) logOut();
                else Toast.makeText(this, R.string.invalid_password_code, Toast.LENGTH_LONG).show();
                return;
            }

            // Log.d("codePassword", codePasswordServer + "           " + codePasswordUser);
            // Validamos que el codigo proporcionado es el mismo que la que nos brindo el server
            if (!codePasswordUser.equals(codePasswordServer)) {
                if (intentsCode == -1) logOut();
                else Toast.makeText(this, R.string.invalid_password_code, Toast.LENGTH_LONG).show();
                return;
            }

            Intent intent = new Intent(this, UpdatePassword.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        // Si al momento de cambiar el texto, el EditText se queda vacio entonces la variable i1 valdra 1
        // En caso contrario significa que al EditText se le agrego un numero
        // Esto se hace en caso de que el usuario borre el numero no se aplique el TAB
        if (i1 != 1) {
            // Esto se hace para poder hacer un TAB para cambiar entre EditText al momento de que el usuario agregue un numero
            BaseInputConnection mInputConnection = new BaseInputConnection(findViewById(R.id.area_buttons), true);
            KeyEvent kd = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_TAB);
            KeyEvent ku = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_TAB);
            mInputConnection.sendKeyEvent(kd);
            mInputConnection.sendKeyEvent(ku);
        }
    }

    public void logOut() {
        // Cerramos la sesion en el telefono
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(EMAIL_KEY, null);
        editor.putString(PASSWORD_KEY, null);
        editor.apply();

        Toast.makeText(this, R.string.log_out_reason, Toast.LENGTH_LONG).show();

        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
        finish();
    }

    public boolean getUserCode() {
        StringBuilder builderCodeUser = new StringBuilder();
        builderCodeUser.append(edtNumber1.getText().toString())
                .append(edtNumber2.getText().toString())
                .append(edtNumber3.getText().toString())
                .append(edtNumber4.getText().toString())
                .append(edtNumber5.getText().toString())
                .append(edtNumber6.getText().toString());
        if (builderCodeUser.length() != 6) {
            Log.i("INFO", "INFO getUserCode: Llenar todos los campos");
            return false;
        }
        codePasswordUser = builderCodeUser.toString();
        return true;
    }

    @Override
    public void afterTextChanged(Editable editable) {
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }
}
