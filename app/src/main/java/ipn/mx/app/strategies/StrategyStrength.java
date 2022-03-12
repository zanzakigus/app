package ipn.mx.app.strategies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Locale;

import ipn.mx.app.HistoryDetection;
import ipn.mx.app.Index;
import ipn.mx.app.PeticionAPI;
import ipn.mx.app.R;
import ipn.mx.app.SettingHeadset;
import ipn.mx.app.User;
import ipn.mx.app.global.GlobalInfo;
import ipn.mx.app.signs.Login;
import ipn.mx.app.updateinfo.UpdateStrengths;

public class StrategyStrength extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = StrategyStrength.class.getSimpleName();

    public static String SHARED_PREFS;
    public static String EMAIL_KEY;
    public static String PASSWORD_KEY;

    SharedPreferences sharedpreferences;

    private Button btnHome,btnGraph,btnNotification,btnUser;
    TextView tvNoInfo;
    LinearLayout vertical_scroll;

    //logged variables
    private String loggedEmail;
    private String loggedPassword;

    Context context;
    TextToSpeech ttobj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.strategy_strengths);
        context = this;

        btnHome = findViewById(R.id.icon_home);
        btnGraph = findViewById(R.id.icon_graph);
        btnNotification = findViewById(R.id.icon_notifications);
        btnUser = findViewById(R.id.icon_user);

        tvNoInfo = findViewById(R.id.no_info);
        vertical_scroll = findViewById(R.id.vertical_scroll);


        btnHome.setOnClickListener(this);
        btnGraph.setOnClickListener(this);
        btnNotification.setOnClickListener(this);
        btnUser.setOnClickListener(this);

        SHARED_PREFS = this.getResources().getString(R.string.shared_key);
        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        EMAIL_KEY = this.getResources().getString(R.string.logged_email_key);
        PASSWORD_KEY = this.getResources().getString(R.string.logged_password_key);


        // initializing our shared preferences.
        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

        loggedEmail = sharedpreferences.getString(EMAIL_KEY, null);
        loggedPassword = sharedpreferences.getString(PASSWORD_KEY, null);

        if (loggedPassword == null || loggedEmail == null) {
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
            finish();
            return;
        }




        // Se hace peticion para obtener la informacion del usuario
        PeticionAPI api = new PeticionAPI(this);
        HashMap<String, String> params = new HashMap<>();
        params.put("correo", loggedEmail);
        params.put("password", loggedPassword);

        StrategyStrength strategyStrenght = new StrategyStrength();
        strategyStrenght.context = this;
        strategyStrenght.tvNoInfo = tvNoInfo;
        strategyStrenght.vertical_scroll = vertical_scroll;

        Class[] parameterTypes = new Class[2];
        parameterTypes[0] = JSONObject.class;
        parameterTypes[1] = Context.class;
        Method functionToPass = null;
        try {
            functionToPass = StrategyStrength.class.getMethod("putInfoOnScreen", parameterTypes);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        api.peticionGET(this.getResources().getString(R.string.server_host) + "/fortalezas", params, strategyStrenght, functionToPass);

        EditText write;
        ttobj=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    ttobj.setLanguage(new Locale(getResources().getString(R.string.locale_lenguage_mx)));
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
         if (btnHome == v) {
            /*Intent intent = new Intent(this, Index.class);
            startActivity(intent);*/
             ttobj.speak("Haz una pausa y respira profundo. Es preciso recordarte tus fortalezas, para que tengas en mente que lo que estás pasando es temporal y tienes herramientas para superarlo.", TextToSpeech.QUEUE_FLUSH, null);
        } else if (btnGraph == v) {
            Intent intent = new Intent(this, HistoryDetection.class);
            startActivity(intent);
        } else if (btnNotification == v) {
            Intent intent = new Intent(this, SettingHeadset.class);
            startActivity(intent);
        } else if (btnUser == v) {
            Intent intent = new Intent(this, User.class);
            startActivity(intent);
        }

    }

    public void putInfoOnScreen(JSONObject response, Context context) throws JSONException {
        Log.d(TAG, "putInfoOnScreen() ");
        if (response.has("error")) {
            Log.e(TAG, "ERROR putInfoOnScreen: " + response.getString("error"));
            Toast myToast = Toast.makeText(context, "ERROR " + (500) + " putInfoOnScreen: " + response.getString("error"), Toast.LENGTH_LONG);
            myToast.show();
            return;
        }
        int status = response.getInt("status");

        if (status != 200) {
            String message = response.getString("message");
            Log.e(TAG, "ERROR putInfoOnScreen: " + message);
            Toast myToast = Toast.makeText(context, "ERROR " + (status) + " putInfoOnScreen: " + message, Toast.LENGTH_LONG);
            myToast.show();
            return;
        }


        JSONArray fortalezas = response.getJSONArray("message");
        if (fortalezas.length() == 0) {
            tvNoInfo.setVisibility(View.VISIBLE);
        } else {
            tvNoInfo.setVisibility(View.GONE);
            Log.d(TAG, "putInfoOnScreen() " + fortalezas.length());
            for (int i = 0; i < fortalezas.length(); i++) {
                ConstraintLayout fortaleza = (ConstraintLayout) LayoutInflater.from(context).inflate(R.layout.component_question, null);

                ConstraintLayout.LayoutParams newLayoutParams = new ConstraintLayout.LayoutParams(
                        ConstraintLayout.LayoutParams.WRAP_CONTENT,
                        ConstraintLayout.LayoutParams.WRAP_CONTENT);
                newLayoutParams.topMargin = 20;
                fortaleza.setLayoutParams(newLayoutParams);

                TextView texto = fortaleza.findViewById(R.id.text_question);
                texto.setText(fortalezas.getJSONObject(i).getString("fortaleza_texto"));
                vertical_scroll.addView(fortaleza);
            }
        }


    }
    public void onPause(){
        if(ttobj !=null){
            ttobj.stop();
            ttobj.shutdown();
        }
        super.onPause();
    }
}
