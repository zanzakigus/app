package ipn.mx.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class Login extends AppCompatActivity implements View.OnClickListener {

    EditText edtCorreo, edtContra;
    View btnLogin, btnRegister;
    RequestQueue queue;
    String urlLogin;

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
        urlLogin = this.getResources().getString(R.string.server_host);

    }


    @Override
    public void onClick(View v) {
        Context context = this;
        if (v == btnLogin) {
            if (edtCorreo.getText().toString().equals("")) {
                Toast myToast = Toast.makeText(this, R.string.missing_email, Toast.LENGTH_LONG);
                myToast.show();
            } else if (edtContra.getText().toString().equals("")) {
                Toast myToast = Toast.makeText(this, R.string.missing_password, Toast.LENGTH_LONG);
                myToast.show();
            } else {

                JSONObject jsonObj = new JSONObject();
                try {
                    jsonObj.put("correo", edtCorreo.getText().toString());
                    jsonObj.put("password", edtContra.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                        (Request.Method.GET, urlLogin, jsonObj, response -> {
                            Toast myToast = Toast.makeText(context, response.toString(), Toast.LENGTH_LONG);
                            myToast.show();
                        }, error -> {
                            System.out.println(error.toString());
                            Toast myToast = Toast.makeText(context, R.string.login_error, Toast.LENGTH_LONG);
                            myToast.show();

                        });
                queue.add(jsonObjectRequest);
                /*Intent intent = new Intent(this, SignUp2.class);
                intent.putExtra("correo", edtCorreo.getText().toString());
                intent.putExtra("contra", edtContra.getText().toString());
                startActivity(intent);*/
            }
        } else if (v == btnRegister) {
            Intent intent = new Intent(this, SignUp1.class);
            startActivity(intent);
        }
    }
}
