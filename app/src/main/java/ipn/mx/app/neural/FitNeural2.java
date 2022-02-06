package ipn.mx.app.neural;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import ipn.mx.app.Index;
import ipn.mx.app.R;
import ipn.mx.app.neurosky.NeuroSkyManager;
import ipn.mx.app.neurosky.library.NeuroSky;

public class FitNeural2 extends AppCompatActivity implements View.OnClickListener {

    private ProgressBar progressBar;
    private TextView progressText;
    View btnNext;
    int i = 1;
    boolean enviado = false;


    private NeuroSky neuroSky;
    private final static String LOG_TAG = "NeuroSky";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.section_negative_fit);

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
            } else {
                Intent intent = new Intent(this, Index.class);
                startActivity(intent);
                finish();
            }
        }else if(v == progressBar){

            if(enviado){
                Toast myToast = Toast.makeText(this, R.string.sent_waves_try, Toast.LENGTH_LONG);
                myToast.show();
            }else{
                Toast myToast = Toast.makeText(this, R.string.sending_waves, Toast.LENGTH_LONG);
                myToast.show();

                NeuroSkyManager.enviarWavesTipoPositivo();

                final Handler handler = new Handler();
                Context context = this;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // set the limitations for the numeric
                        // text under the progress bar
                        if (i <= 60) {
                            progressText.setText("" + i);
                            int progress = (i*100)/60;
                            progressBar.setProgress(progress);
                            i++;
                            handler.postDelayed(this, 1000);
                        } else {
                            NeuroSkyManager.stopSendingWaves();
                            NeuroSkyManager.solicitarEntrenamiento();
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