package ipn.mx.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import ipn.mx.app.neuralfit.FitConnect;
import ipn.mx.app.neurosky.NeuroSkyManager;
import ipn.mx.app.service.ClasifyService;
import ipn.mx.app.signs.Login;
import ipn.mx.app.tutorial.TutorialHeadset;

public class InfoAppView extends AppCompatActivity implements View.OnClickListener {

    // creating constant keys for shared preferences.
    public static String SHARED_PREFS;
    public static String EMAIL_KEY;
    public static String PASSWORD_KEY;
    View Arrow;
    // variable for shared preferences.
    SharedPreferences sharedpreferences;
    //logged variables
    private String loggedEmail;
    private String loggedPassword;

    TextView tvLogOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_app_view);

        tvLogOut = findViewById(R.id.log_out);
        tvLogOut.setOnClickListener(this);

        Arrow = findViewById(R.id.arrow);
        Arrow.setOnClickListener(this);

        SHARED_PREFS = this.getResources().getString(R.string.shared_key);
        EMAIL_KEY = this.getResources().getString(R.string.logged_email_key);
        PASSWORD_KEY = this.getResources().getString(R.string.logged_password_key);

        // initializing our shared preferences.
        sharedpreferences = getSharedPreferences(this.getResources().getString(R.string.shared_key), Context.MODE_PRIVATE);
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
        if (v == Arrow) {
            Intent intent = new Intent(this, TutorialHeadset.class);
            startActivity(intent);
        } else if(v == tvLogOut){
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.clear();
            editor.apply();

            NeuroSkyManager.stopSendingWaves();
            if (NeuroSkyManager.getNeuroSky() != null)
                if (NeuroSkyManager.getNeuroSky().isConnected())
                    NeuroSkyManager.getNeuroSky().disconnect();

            Intent stopHeadset = new Intent(this, ClasifyService.class);
            stopService(stopHeadset);

            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
            finish();
        }
    }
}
