package ipn.mx.app.strategies;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import ipn.mx.app.R;

public class StrategyTemporal extends AppCompatActivity implements View.OnClickListener {

    VideoView videoView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.strategy_temporal);

        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.estrategia_temporal);

        videoView = findViewById(R.id.video);
        videoView.setVideoURI(videoUri);
        videoView.setMediaController(new MediaController(this));
    }

    @Override
    public void onClick(View view) {

    }
}
