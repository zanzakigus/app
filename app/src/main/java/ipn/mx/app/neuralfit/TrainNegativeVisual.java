package ipn.mx.app.neuralfit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import ipn.mx.app.R;
import ipn.mx.app.global.GlobalInfo;
import ipn.mx.app.neurosky.NeuroSkyManager;
import ipn.mx.app.neurosky.library.NeuroSky;

public class TrainNegativeVisual extends AppCompatActivity implements View.OnClickListener {


    View btnNext;
    int i = 1;
    boolean enviado = false;
    private ProgressBar progressBar;
    private TextView progressText;
    private NeuroSky neuroSky;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.train_negative_visual);

        neuroSky = NeuroSkyManager.getNeuroSky();
        if (neuroSky.isConnected()) {
            neuroSky.start();
        } else {
            Intent intent = new Intent(this, FitConnect.class);
            startActivity(intent);
            finish();
            Toast myToast = Toast.makeText(this, R.string.no_connect, Toast.LENGTH_LONG);
            myToast.show();
        }

        // set the id for the progressbar and progress text
        progressBar = findViewById(R.id.progress_bar);
        progressText = findViewById(R.id.progress_text);
        btnNext = findViewById(R.id.arrow);

        btnNext.setOnClickListener(this);
        progressBar.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btnNext) {
            if (!NeuroSkyManager.getNeuroSky().isConnected()) {
                Toast myToast = Toast.makeText(this, R.string.no_connect, Toast.LENGTH_LONG);
                myToast.show();
            } else if (i != GlobalInfo.getTrainSectionTime()) {
                Toast.makeText(this, R.string.first_send_waves, Toast.LENGTH_LONG).show();
            } else {
                Intent intent = new Intent(this, TrainNegativePersonal.class);
                startActivity(intent);
                finish();
            }
        } else if (v == progressBar) {

            if (enviado) {
                Toast myToast = Toast.makeText(this, R.string.sent_waves_try, Toast.LENGTH_LONG);
                myToast.show();
            } else {
                Toast myToast = Toast.makeText(this, R.string.sending_waves, Toast.LENGTH_LONG);
                myToast.show();

                NeuroSkyManager.enviarWavesTipoNegativo();

                final Handler handler = new Handler();
                Context context = this;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // set the limitations for the numeric
                        // text under the progress bar
                        int time = GlobalInfo.getTrainSectionTime();
                        if (i <= time) {
                            progressText.setText("" + i);
                            int progress = (i * 100) / time;
                            progressBar.setProgress(progress);
                            i++;
                            handler.postDelayed(this, 1000);
                        } else {
                            NeuroSkyManager.stopSendingWaves();
                            enviado = true;
                            Toast myToast = Toast.makeText(context, R.string.sent_waves_succed, Toast.LENGTH_LONG);
                            myToast.show();
                            handler.removeCallbacks(this);
                        }
                    }
                }, 1000);
            }
        }
    }
}

