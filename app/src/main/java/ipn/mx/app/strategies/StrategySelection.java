package ipn.mx.app.strategies;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
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
import java.util.Random;

import ipn.mx.app.HistoryDetection;
import ipn.mx.app.PeticionAPI;
import ipn.mx.app.R;
import ipn.mx.app.signs.Login;
import ipn.mx.app.signs.SignUp1;

public class StrategySelection extends AppCompatActivity {

    public static String SHARED_PREFS;
    public static String EMAIL_KEY;
    public static String PASSWORD_KEY;


    SharedPreferences sharedpreferences;
    LinearLayout scrollView;
    Context context;

    //logged variables
    private String loggedEmail;
    private String loggedPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selection_strategy);

        context = this;
        scrollView = findViewById(R.id.scrollview);

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

        StrategySelection strategySelection = new StrategySelection();
        strategySelection.context = this;
        strategySelection.scrollView = scrollView;
        Class[] parameterTypes = new Class[2];
        parameterTypes[0] = JSONObject.class;
        parameterTypes[1] = Context.class;
        Method functionToPass = null;
        try {
            functionToPass = StrategySelection.class.getMethod("putInfoOnScreen", parameterTypes);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        api.peticionGET(this.getResources().getString(R.string.server_host) + "/estrategia", params, strategySelection, functionToPass);

    }

    public void putInfoOnScreen(JSONObject response, Context context) throws JSONException {
        if (response.has("error")) {
            Log.e("ERROR", "ERROR putInfoOnScreen: " + response.getString("error"));
            Toast myToast = Toast.makeText(context, "ERROR " + (500) + " putInfoOnScreen: " + response.getString("error"), Toast.LENGTH_LONG);
            myToast.show();
            return;
        }

        int status = response.getInt("status");
        String message = response.getString("message");
        if (status != 200) {
            Log.w("WARNING", "WARNING putInfoOnScreen: " + message);
            Toast myToast = Toast.makeText(context, "ERROR " + (status) + " putInfoOnScreen: " + message, Toast.LENGTH_LONG);
            myToast.show();
            return;
        }

        JSONArray estrategia = response.getJSONArray("message");


        for (int i = 0; i < estrategia.length(); i++) {
            addStrategy(estrategia.getJSONObject(i));
        }
        JSONObject aleatorio = new JSONObject();
        aleatorio.put("image_name", "aleatorio");
        aleatorio.put("texto_estrategia", "Aleatorio");
        addStrategy(aleatorio);

    }

    public void addStrategy(JSONObject estrategia) {
        ConstraintLayout con_estrategia = (ConstraintLayout) LayoutInflater.from(context).inflate(R.layout.component_strategy_select, null);
        ConstraintLayout.LayoutParams newLayoutParams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT);
        newLayoutParams.topMargin = 20;
        con_estrategia.setLayoutParams(newLayoutParams);

        TextView tvTexto = con_estrategia.findViewById(R.id.text_strategy);
        View icon = con_estrategia.findViewById(R.id.icon_image);
        String image_name = null;
        try {
            image_name = estrategia.getString("image_name");

            System.out.println(image_name);
            String texto_estrategia = estrategia.getString("texto_estrategia");
            int resourceId = context.getResources().getIdentifier(image_name, "drawable",
                    context.getPackageName());
            tvTexto.setText(texto_estrategia);
            icon.setBackgroundResource(resourceId);


            con_estrategia.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    View backgound = con_estrategia.findViewById(R.id.component_detection);
                    backgound.setBackgroundResource(R.drawable.background_strategy_clicked);

                    Intent intent = null;
                    try {
                        intent = new Intent(context, StrategiesEnum.values()[estrategia.getInt("id_estrategia")].getResId());
                    } catch (JSONException e) {
                        Random random = new Random();
                        int strategyNumber = random.nextInt(4);
                        intent = new Intent(context, StrategiesEnum.values()[strategyNumber].getResId());
                    }
                    context.startActivity(intent);
                    ((Activity) context).finish();
                }
            });

            con_estrategia.setOnLongClickListener(v -> {
                View backgound = con_estrategia.findViewById(R.id.component_detection);
                backgound.setBackgroundResource(R.drawable.background_strategy_clicked);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        backgound.setBackgroundResource(R.drawable.background_strategy_with_shadow);
                    }
                }, 500);
                return true;
            });
            scrollView.addView(con_estrategia);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
