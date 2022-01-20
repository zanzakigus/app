package ipn.mx.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity implements View.OnClickListener {

    EditText edtCorreo, edtContra;
    View btnLogin, btnRegister;
    RequestQueue queue;
    String urlLogin;

    // creating constant keys for shared preferences.
    public static String SHARED_PREFS;

    // key for storing email.
    public static String EMAIL_KEY;

    // key for storing password.
    public static String PASSWORD_KEY;

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

        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);

        queue = Volley.newRequestQueue(this);
        urlLogin = this.getResources().getString(R.string.server_host)+"/login";

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
            String email = edtCorreo.getText().toString();
            String password = edtContra.getText().toString();
            if (email.equals("")) {
                Toast myToast = Toast.makeText(this, R.string.missing_email, Toast.LENGTH_LONG);
                myToast.show();
            } else if (password.equals("")) {
                Toast myToast = Toast.makeText(this, R.string.missing_password, Toast.LENGTH_LONG);
                myToast.show();
            } else {

                JSONObject jsonObj = new JSONObject();
                try {
                    jsonObj.put("correo", email);
                    jsonObj.put("password", password);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                        (Request.Method.POST, urlLogin, jsonObj, response -> {
                            boolean resp = false;
                            String message = context.getResources().getString(R.string.logged_fail);;
                            try {
                                 resp = response.getBoolean("resp");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if(resp){
                                message = context.getResources().getString(R.string.logged_succed);
                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                editor.putString(EMAIL_KEY, email);
                                editor.putString(PASSWORD_KEY, password);

                                // to save our data with key and value.
                                editor.apply();
                                Intent intent = new Intent(this, Index.class);
                                startActivity(intent);
                                finish();
                            }
                            Toast myToast = Toast.makeText(context, message, Toast.LENGTH_LONG);
                            myToast.show();
                        }, error -> {
                            System.out.println(error.toString());
                            Toast myToast = Toast.makeText(context, R.string.login_error, Toast.LENGTH_LONG);
                            myToast.show();
                        })/*{

                    //This is for Headers If You Needed
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("Content-Type", "application/json; charset=UTF-8");
                        params.put("token", "hola");
                        return params;
                    }
                }*/;
                queue.add(jsonObjectRequest);
            }
        } else if (v == btnRegister) {
            Intent intent = new Intent(this, SignUp1.class);
            startActivity(intent);
        }
    }
}
