package ipn.mx.app.neuralfit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.opengl.Visibility;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.HashMap;

import ipn.mx.app.Index;
import ipn.mx.app.PeticionAPI;
import ipn.mx.app.R;
import ipn.mx.app.global.GlobalInfo;

public class TrainFinished extends AppCompatActivity implements View.OnClickListener, Runnable {

    View arrow;
    static TextView messageFinished;
    static int lengthMessageFinished;

    static Handler hiloTexto;

    // creating constant keys for shared preferences.
    public static String SHARED_PREFS;
    public static String EMAIL_KEY;
    public static String PASSWORD_KEY;
    public static String NOMBRE_KEY;

    // variable for shared preferences.
    SharedPreferences sharedpreferences;

    //logged variables
    private String loggedEmail;
    private String loggedPassword;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.train_finished);

        messageFinished = findViewById(R.id.train_finished_message);
        arrow = findViewById(R.id.arrow);
        arrow.setOnClickListener(this);

        hiloTexto = new Handler();
        hiloTexto.postDelayed(this,1500);

        lengthMessageFinished = messageFinished.getText().toString().length();


        // initializing shared preferences keys.
        SHARED_PREFS = this.getResources().getString(R.string.shared_key);
        EMAIL_KEY = this.getResources().getString(R.string.logged_email_key);
        PASSWORD_KEY = this.getResources().getString(R.string.logged_password_key);
        NOMBRE_KEY = this.getResources().getString(R.string.logged_nombre);

        // initializing our shared preferences.
        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

        loggedEmail = sharedpreferences.getString(EMAIL_KEY, null);
        loggedPassword = sharedpreferences.getString(PASSWORD_KEY, null);

        PeticionAPI api = new PeticionAPI(this);
        HashMap<String, String> params = new HashMap<>();
        params.put("correo", loggedEmail);
        params.put("password", loggedPassword);

        TrainFinished trainFinished = new TrainFinished();
        Class[] parameterTypes = new Class[2];
        parameterTypes[0] = JSONObject.class;
        parameterTypes[1] = Context.class;
        Method functionToPass = null;

        try {
            functionToPass = TrainFinished.class.getMethod("trainingFinished", parameterTypes);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        api.peticionPOST(this.getResources().getString(R.string.server_host) + "/fit_neural", params, trainFinished, functionToPass);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this, TrainPositivePersonal.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void run() {
        int lengthNow = messageFinished.getText().toString().length();
        if(lengthNow >= lengthMessageFinished + 4){
            messageFinished.setText(getResources().getString(R.string.train_finished_training));
        } else {
            String newText = messageFinished.getText().toString() + ".";
            messageFinished.setText(newText);
        }
        hiloTexto.postDelayed(this, 1500);
    }

    public void trainingFinished(JSONObject response, Context context) throws JSONException{
        if (response.has("error")) {
            Log.e("ERROR", "ERROR trainingFinished: " + response.getString("error"));
            Toast myToast = Toast.makeText(context, "ERROR " + (500) + " trainingFinished: " + response.getString("error"), Toast.LENGTH_LONG);
            myToast.show();
            return;
        }
        int status = response.getInt("status");
        String message = response.getString("message");
        if (status != 200) {
            Log.e("ERROR", "ERROR trainingFinished: " + message);
            Toast myToast = Toast.makeText(context, "ERROR " + (status) + " trainingFinished: " + message, Toast.LENGTH_LONG);
            myToast.show();
            return;
        }

        hiloTexto.removeCallbacks((Runnable) context);
        ((TextView) ((Activity) context).findViewById(R.id.train_finished_message)).setText(context.getResources().getString(R.string.train_finished_trained));
        ((TextView) ((Activity) context).findViewById(R.id.train_finished_message)).setTextAppearance(context, R.style.text_green_bold);

        ((View) ((Activity) context).findViewById(R.id.arrow)).setVisibility(View.VISIBLE);
    }
}
