package ipn.mx.app.strategies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import ipn.mx.app.HistoryDetection;
import ipn.mx.app.Index;
import ipn.mx.app.R;
import ipn.mx.app.SettingHeadset;
import ipn.mx.app.User;
import ipn.mx.app.signs.Login;

public class StrategyTemporal extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = StrategyPhrases.class.getSimpleName();

    public static String SHARED_PREFS;
    public static String EMAIL_KEY;
    public static String PASSWORD_KEY;

    SharedPreferences sharedpreferences;

    private Button btnHome, btnGraph, btnNotification, btnUser;

    //logged variables
    private String loggedEmail;
    private String loggedPassword;

    VideoView videoView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.strategy_temporal);

        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.estrategia_temporal);

        videoView = findViewById(R.id.video);
        videoView.setVideoURI(videoUri);
        videoView.setMediaController(new MediaController(this));

        btnHome = findViewById(R.id.icon_home);
        btnGraph = findViewById(R.id.icon_graph);
        btnNotification = findViewById(R.id.icon_notifications);
        btnUser = findViewById(R.id.icon_user);

        btnHome.setOnClickListener(this);
        btnGraph.setOnClickListener(this);
        btnNotification.setOnClickListener(this);
        btnUser.setOnClickListener(this);

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
    }

    @Override
    public void onClick(View v) {
        if (btnHome == v) {
            Intent intent = new Intent(this, Index.class);
            startActivity(intent);
            finish();
        } else if (btnGraph == v) {
            Intent intent = new Intent(this, HistoryDetection.class);
            startActivity(intent);
            finish();
        } else if (btnNotification == v) {
            Intent intent = new Intent(this, SettingHeadset.class);
            startActivity(intent);
            finish();
        } else if (btnUser == v) {
            Intent intent = new Intent(this, User.class);
            startActivity(intent);
            finish();
        }
    }
}
