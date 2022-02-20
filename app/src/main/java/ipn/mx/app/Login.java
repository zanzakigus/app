package ipn.mx.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.HashMap;

public class Login extends AppCompatActivity implements View.OnClickListener, TextView.OnEditorActionListener {

    // creating constant keys for shared preferences.
    public static String SHARED_PREFS;
    // key for storing email.
    public static String EMAIL_KEY;
    // key for storing password.
    public static String PASSWORD_KEY;
    // View variables
    EditText edtCorreo, edtContra;
    View btnLogin, btnRegister;
    TextView tvForgetPassword;
    //Http request variables
    RequestQueue queue;
    String host;
    String email, password;
    // variable for shared preferences.
    SharedPreferences sharedpreferences;

    //logged variables
    private String loggedEmail;
    private String loggedPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_in);

        btnLogin = findViewById(R.id.arrow);
        edtCorreo = findViewById(R.id.email_input);
        edtContra = findViewById(R.id.password_input);
        btnRegister = findViewById(R.id.registrarse_button);
        tvForgetPassword = findViewById(R.id.forget_password);

        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        tvForgetPassword.setOnClickListener(this);
        edtContra.setOnEditorActionListener(this);

        queue = Volley.newRequestQueue(this);
        host = this.getResources().getString(R.string.server_host);

        // initializing shared preferences keys.
        SHARED_PREFS = this.getResources().getString(R.string.shared_key);
        EMAIL_KEY = this.getResources().getString(R.string.logged_email_key);
        PASSWORD_KEY = this.getResources().getString(R.string.logged_password_key);

        // initializing our shared preferences.
        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

        loggedEmail = sharedpreferences.getString(EMAIL_KEY, null);
        loggedPassword = sharedpreferences.getString(PASSWORD_KEY, null);
        if (loggedPassword != null && loggedEmail != null) {
            Intent intent = new Intent(this, Index.class);
            startActivity(intent);
            finish();
        }
    }


    @Override
    public void onClick(View v) {
        Context context = this;
        if (v == btnLogin) {
            sendPetitionLogin();
        } else if (v == btnRegister) {
            Intent intent = new Intent(this, SignUp1.class);
            startActivity(intent);
        } else if (v == tvForgetPassword) {
            Intent intent = new Intent(this, ForgetPassword.class);
            startActivity(intent);
        }
    }

    public void sendPetitionLogin() {
        email = edtCorreo.getText().toString();
        password = edtContra.getText().toString();
        if (email.equals("")) {
            Toast myToast = Toast.makeText(this, R.string.missing_email, Toast.LENGTH_LONG);
            myToast.show();
        } else if (password.equals("")) {
            Toast myToast = Toast.makeText(this, R.string.missing_password, Toast.LENGTH_LONG);
            myToast.show();
        } else {

            PeticionAPI api = new PeticionAPI(this);

            HashMap<String, String> params = new HashMap<>();
            params.put("correo", email);
            params.put("password", password);

            Log.d("Correo", "sendPetitionLogin: " + email);

            Login login = new Login();
            login.email = email;
            login.password = password;
            Class[] parameterTypes = new Class[2];
            parameterTypes[0] = JSONObject.class;
            parameterTypes[1] = Context.class;
            Method functionToPass = null;

            try {
                functionToPass = Login.class.getMethod("onLogin", parameterTypes);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            api.peticionPOST(host + "/login", params, login, functionToPass);
        }
    }

    public void onLogin(JSONObject response, Context context) throws JSONException {
        if (response.has("error")) {
            Log.e("ERROR", "ERROR onLogin: " + response.getString("error"));
            Toast myToast = Toast.makeText(context, "ERROR " + (500) + " onLogin: " + response.getString("error"), Toast.LENGTH_LONG);
            myToast.show();
            return;
        }
        int status = response.getInt("status");
        String message = response.getString("message");
        if (status != 200) {
            Log.e("ERROR", "ERROR onLogin: " + message);
            Toast myToast = Toast.makeText(context, "ERROR " + (status) + " onLogin: " + message, Toast.LENGTH_LONG);
            myToast.show();
            return;
        }
        String Shared = context.getResources().getString(R.string.shared_key);
        SharedPreferences SharedP = context.getSharedPreferences(Shared, Context.MODE_PRIVATE);
        String emailKey = context.getResources().getString(R.string.logged_email_key);
        String passKey = context.getResources().getString(R.string.logged_password_key);

        message = context.getResources().getString(R.string.logged_succed);
        SharedPreferences.Editor editor = SharedP.edit();

        editor.putString(emailKey, email);
        editor.putString(passKey, password);
        editor.apply();

        Intent intent = new Intent(context, Index.class);
        context.startActivity(intent);
        Log.i("INFO", "INFO: Usuario Loggeado");
        ((Activity) context).finish();

        Toast myToast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        myToast.show();
    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        boolean handled = false;
        if(edtContra.getId() == textView.getId() && i == EditorInfo.IME_ACTION_SEND){
            sendPetitionLogin();
            handled = true;
        }
        return handled;
    }
}
