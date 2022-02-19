package ipn.mx.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.HashMap;

public class ForgetPassword extends AppCompatActivity implements View.OnClickListener {

    TextView tvCorreo;
    Button btnEnviar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forget_password);

        tvCorreo = findViewById(R.id.email);
        btnEnviar = findViewById(R.id.btn_send);

        btnEnviar.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == btnEnviar){
            PeticionAPI api = new PeticionAPI(this);
            HashMap<String, String> params = new HashMap<>();

            String correo = tvCorreo.getText().toString();

            params.put("correo", correo);

            ForgetPassword forgetPassword = new ForgetPassword();
            Class[] parameterTypes = new Class[2];
            parameterTypes[0] = JSONObject.class;
            parameterTypes[1] = Context.class;
            Method functionToPass = null;
            try {
                functionToPass = ForgetPassword.class.getMethod("forgetPasswordHandle", parameterTypes);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }

            api.peticionPATCH(this.getResources().getString(R.string.server_host) + "/password", params, forgetPassword, functionToPass);
        }
    }

    public void forgetPasswordHandle(JSONObject response, Context context) throws JSONException {
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

        Intent intent = new Intent(context, Login.class);
        context.startActivity(intent);
        finish();
    }
}
