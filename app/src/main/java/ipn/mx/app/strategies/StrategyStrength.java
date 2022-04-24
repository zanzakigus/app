package ipn.mx.app.strategies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import ipn.mx.app.HistoryDetection;
import ipn.mx.app.Index;
import ipn.mx.app.PeticionAPI;
import ipn.mx.app.R;
import ipn.mx.app.SettingHeadset;
import ipn.mx.app.User;
import ipn.mx.app.misc.BoxHelper;
import ipn.mx.app.neurosky.NeuroSkyManager;
import ipn.mx.app.signs.Login;

public class StrategyStrength extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = StrategyStrength.class.getSimpleName();

    public static String SHARED_PREFS;
    public static String EMAIL_KEY;
    public static String PASSWORD_KEY;

    SharedPreferences sharedpreferences;

    private Button btnHome, btnGraph, btnNotification, btnUser, btnTellMe;
    TextView tvNoInfo;
    LinearLayout vertical_scroll;
    TextView tvTextInst;
    Handler handler = new Handler();

    //logged variables
    private String loggedEmail;
    private String loggedPassword;

    Context context;
    TextToSpeech speaker;

    int i;
    BoxHelper<Boolean> primerRonda, lecturaInstruc;
    MediaPlayer mediaPlayer;

    ArrayList<String> strenghts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.strategy_strengths);
        NeuroSkyManager.displaystrategy = true;
        context = this;

        primerRonda = new BoxHelper<>(false);
        lecturaInstruc = new BoxHelper<>(false);
        strenghts = new ArrayList<>();
        mediaPlayer = new MediaPlayer();


        btnHome = findViewById(R.id.icon_home);
        btnGraph = findViewById(R.id.icon_graph);
        btnNotification = findViewById(R.id.icon_notifications);
        btnUser = findViewById(R.id.icon_user);
        btnTellMe = findViewById(R.id.button_tell_me);

        tvNoInfo = findViewById(R.id.no_info);
        vertical_scroll = findViewById(R.id.vertical_scroll);
        tvTextInst = findViewById(R.id.text_strategy);


        btnHome.setOnClickListener(this);
        btnGraph.setOnClickListener(this);
        btnNotification.setOnClickListener(this);
        btnUser.setOnClickListener(this);
        btnTellMe.setOnClickListener(this);

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

        initSpeaker();

        StrategyStrength strategyStrenght = new StrategyStrength();
        strategyStrenght.context = this;
        strategyStrenght.tvNoInfo = tvNoInfo;
        strategyStrenght.vertical_scroll = vertical_scroll;
        strategyStrenght.speaker = speaker;
        strategyStrenght.tvTextInst = tvTextInst;
        strategyStrenght.primerRonda = primerRonda;
        strategyStrenght.lecturaInstruc = lecturaInstruc;
        strategyStrenght.strenghts = strenghts;

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

        mediaPlayer = MediaPlayer.create(this, R.raw.strengths_strategy);
        mediaPlayer.setLooping(true); // Set looping
        mediaPlayer.setVolume(100, 100);
        mediaPlayer.start();
    }

    @Override
    public void onClick(View v) {
        if (btnHome == v) {
            Intent intent = new Intent(this, Index.class);
            startActivity(intent);
            finish();
            stopSounds();
        } else if (btnGraph == v) {
            Intent intent = new Intent(this, HistoryDetection.class);
            startActivity(intent);
            finish();
            stopSounds();
        } else if (btnNotification == v) {
            Intent intent = new Intent(this, SettingHeadset.class);
            startActivity(intent);
            finish();
            stopSounds();
        } else if (btnUser == v) {
            Intent intent = new Intent(this, User.class);
            startActivity(intent);
            finish();
            stopSounds();
        } else if (btnTellMe == v) {
            sayStrengths();
        }

    }

    public void initSpeaker() {
        speaker = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    speaker.setLanguage(new Locale(getResources().getString(R.string.locale_lenguage_mx)));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        speaker.speak(tvTextInst.getText().toString(), TextToSpeech.QUEUE_FLUSH, null, TextToSpeech.ACTION_TTS_QUEUE_PROCESSING_COMPLETED);
                    } else {
                        speaker.speak(tvTextInst.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
                    }

                }
            }
        });
        speaker.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
                Log.i("TextToSpeech", "On Start");
            }

            @Override
            public void onDone(String utteranceId) {
                Log.i("TextToSpeech", "On Done " + utteranceId);
                lecturaInstruc.set(true);
            }

            @Override
            public void onError(String utteranceId) {
                Log.i("TextToSpeech", "On Error");
            }
        });
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
                strenghts.add(fortalezas.getJSONObject(i).getString("fortaleza_texto"));
                ConstraintLayout fortaleza = (ConstraintLayout) LayoutInflater.from(context).inflate(R.layout.component_question, null);

                ConstraintLayout.LayoutParams newLayoutParams = new ConstraintLayout.LayoutParams(
                        ConstraintLayout.LayoutParams.WRAP_CONTENT,
                        ConstraintLayout.LayoutParams.WRAP_CONTENT);
                newLayoutParams.topMargin = 20;
                fortaleza.setLayoutParams(newLayoutParams);

                TextView texto = fortaleza.findViewById(R.id.text_question);
                texto.setText(fortalezas.getJSONObject(i).getString("fortaleza_texto"));

                fortaleza.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (primerRonda.get()) {
                            speaker.speak(texto.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);

                        }

                    }

                });
                vertical_scroll.addView(fortaleza);
            }

            sayStrengths();
        }
    }

    public void sayStrengths() {
        i = 0;

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "handler");
                if (i < strenghts.size()) {
                    if (lecturaInstruc.get()) {
                        lecturaInstruc.set(false);
                        Log.i(TAG, "handler");

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            speaker.speak(strenghts.get(i), TextToSpeech.QUEUE_FLUSH, null, TextToSpeech.ACTION_TTS_QUEUE_PROCESSING_COMPLETED);
                        } else {
                            speaker.speak(strenghts.get(i), TextToSpeech.QUEUE_FLUSH, null);
                        }

                        i++;
                    }
                    handler.postDelayed(this, 1000);

                } else {
                    primerRonda.set(true);
                    handler.removeCallbacks(this);
                }

            }
        }, 1000);
    }

    public void stopSounds(){
        NeuroSkyManager.displaystrategy = false;
        speaker.stop();
        mediaPlayer.stop();
    }

}
