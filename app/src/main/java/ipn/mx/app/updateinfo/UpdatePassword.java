package ipn.mx.app.updateinfo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

public class UpdatePassword extends AppCompatActivity implements View.OnClickListener {

    // creating constant keys for shared preferences.
    public static String SHARED_PREFS;
    public static String EMAIL_KEY;
    public static String PASSWORD_KEY;
    public static String NOMBRE_KEY;
    EditText edtPassword, edtConfirm;
    Button btnActualizar;
    // variable for shared preferences.
    SharedPreferences sharedpreferences;

    //logged variables
    private String loggedEmail;
    private String loggedPassword;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_password);

        edtPassword = findViewById(R.id.new_input);
        edtConfirm = findViewById(R.id.confirm_input);
        btnActualizar = findViewById(R.id.btn_update);

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

        // Se valida que haya la seccion sea correcta
        if (loggedPassword == null || loggedEmail == null) {
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
            finish();
            return;
        }
    }

    @Override
    public void onClick(View v) {
        if (btnActualizar == v) {
            String Password = edtPassword.getText().toString();
            String Actualizar = edtConfirm.getText().toString();

            if (Password.length() <= 4 || Actualizar.length() <= 4) {
                Toast.makeText(this, R.string.update_password_password_short, Toast.LENGTH_LONG).show();
                return;
            }
            if (!Password.equals(Actualizar)) {
                Toast.makeText(this, R.string.update_password_passwords_no_equals, Toast.LENGTH_SHORT).show();
                return;
            }

            PeticionAPI api = new PeticionAPI(this);
            HashMap<String, String> params = new HashMap<>();
            params.put("correo", loggedEmail);
            params.put("password", loggedPassword);
            params.put("new_password", Actualizar);

            UpdatePassword updatePassword = new UpdatePassword();
            Class[] parameterTypes = new Class[2];
            parameterTypes[0] = JSONObject.class;
            parameterTypes[1] = Context.class;
            Method functionToPass = null;
            try {
                functionToPass = UpdatePassword.class.getMethod("updatePasswordCallback", parameterTypes);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }

            api.peticionPUT(this.getResources().getString(R.string.server_host) + "/password", params, updatePassword, functionToPass);


        }
    }

    public void updatePasswordCallback(JSONObject response, Context context) throws JSONException {
        if (response.has("error")) {
            Log.e("ERROR", "ERROR updatePasswordCallback: " + response.getString("error"));
            Toast myToast = Toast.makeText(context, "ERROR " + (500) + " updatePasswordCallback: " + response.getString("error"), Toast.LENGTH_LONG);
            myToast.show();
            return;
        }
        int status = response.getInt("status");
        String message = response.getString("message");
        if (status != 200) {
            Log.e("ERROR", "ERROR updatePasswordCallback: " + message);
            Toast myToast = Toast.makeText(context, "ERROR " + (status) + " updatePasswordCallback: " + message, Toast.LENGTH_LONG);
            myToast.show();
            return;
        }
        String Shared = context.getResources().getString(R.string.shared_key);
        SharedPreferences SharedP = context.getSharedPreferences(Shared, Context.MODE_PRIVATE);
        String emailKey = context.getResources().getString(R.string.logged_email_key);
        String passKey = context.getResources().getString(R.string.logged_password_key);

        SharedPreferences.Editor editor = SharedP.edit();
        ;

        editor.putString(emailKey, null);
        editor.putString(passKey, null);
        editor.apply();

        Intent intent = new Intent(context, Login.class);
        context.startActivity(intent);
        Log.i("INFO", "INFO: Cambio de contraseña");
        ((Activity) context).finish();

        Toast.makeText(context, R.string.update_password_success, Toast.LENGTH_LONG).show();
    }
}
