package ipn.mx.app;

import android.app.Activity;
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

import ipn.mx.app.global.GlobalInfo;
import ipn.mx.app.neurosky.NeuroSkyManager;
import ipn.mx.app.neurosky.library.NeuroSky;
import ipn.mx.app.service.ClasifyService;

public class ManualDetection extends AppCompatActivity implements View.OnClickListener{


    static int i = 1;
    boolean enviado = false;
    private ProgressBar progressBar;
    private TextView progressText;
    private NeuroSky neuroSky;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manual_detection);

        NeuroSkyManager.stopSendingWaves();
        Intent stopHeadset = new Intent(this, ClasifyService.class);
        stopService(stopHeadset);

        progressBar = findViewById(R.id.progress_bar);
        progressText = findViewById(R.id.progress_text);

        // set the id for the progressbar and progress text
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == progressBar) {

            if (enviado) {
                Toast myToast = Toast.makeText(this, R.string.sent_waves_try, Toast.LENGTH_LONG);
                myToast.show();
            } else {
                Toast myToast = Toast.makeText(this, R.string.sending_waves, Toast.LENGTH_LONG);
                myToast.show();

                NeuroSkyManager.enviarWavesIdentificar();

                final Handler handler = new Handler();
                Context context = this;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // set the limitations for the numeric
                        // text under the progress bar
                        int time = GlobalInfo.getClasifyTimeDelay() / 1000;
                        if (i <= time) {
                            progressText.setText("" + i);
                            int progress = (i * 100) / time;
                            progressBar.setProgress(progress);
                            i++;
                            handler.postDelayed(this, 1000);
                        } else {
                            i = 0;
                            NeuroSkyManager.stopSendingWaves();
                            Toast myToast = Toast.makeText(context, R.string.sent_waves_succed, Toast.LENGTH_LONG);
                            myToast.show();
                            handler.removeCallbacks(this);
                            Intent intent = new Intent(context, Index.class);
                            ((Activity) context).startActivity(intent);
                            ((Activity) context).finish();
                        }
                    }
                }, 1000);
            }
        }
    }
}
