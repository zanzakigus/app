package ipn.mx.app.tutorial;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import ipn.mx.app.InfoAppView;
import ipn.mx.app.R;
import ipn.mx.app.SettingHeadset;
import ipn.mx.app.neuralfit.FitConnect;

public class TutorialHeadset extends AppCompatActivity implements View.OnClickListener {

    VideoView videoView;
    TextView fullscreen;
    View arrow;

    final String urlTutorialHeadset = "https://youtu.be/RyEQG6QNmvY";

    static String logeado;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorial_headset);

        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.tutorial_diadema);

        logeado = getIntent().getStringExtra("logeado");

        videoView = findViewById(R.id.video);
        videoView.setVideoURI(videoUri);
        videoView.setMediaController(new MediaController(this));

        fullscreen = findViewById(R.id.tutorial_headset_fullscreen);
        fullscreen.setOnClickListener(this);

        arrow = findViewById(R.id.arrow);
        arrow.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (fullscreen == v) {
            Uri urlVideo = Uri.parse(urlTutorialHeadset);
            Intent videoIntent = new Intent(Intent.ACTION_VIEW, urlVideo);
            try {
                startActivity(videoIntent);
            } catch (ActivityNotFoundException e) {
                Log.d("TutorialHeadset", "Fullscreen onClick: " + e.toString());
                Toast.makeText(this, getResources().getString(R.string.tutorial_headset_fullscreen_error), Toast.LENGTH_LONG).show();
            }
        } else if (arrow == v) {
            Intent intent;
            if (logeado != null)
                intent = new Intent(this, FitConnect.class);
            else
                intent = new Intent(this, SettingHeadset.class);
            startActivity(intent);
        }
    }
}
