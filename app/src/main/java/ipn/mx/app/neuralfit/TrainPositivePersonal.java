package ipn.mx.app.neuralfit;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import ipn.mx.app.Index;
import ipn.mx.app.R;
import ipn.mx.app.global.GlobalInfo;
import ipn.mx.app.neurosky.NeuroSkyManager;
import ipn.mx.app.neurosky.library.NeuroSky;

public class TrainPositivePersonal extends AppCompatActivity implements View.OnClickListener {

    final int CANTIDAD_CONSEJOS = 6;
    LinearLayout vertical_scroll;

    View btnNext;
    int i = 1;
    boolean enviado = false;
    private ProgressBar progressBar;
    private TextView progressText;
    private NeuroSky neuroSky;

    Dialog dialog;
    Button btnContinuarDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.train_positive_personal);

        dialog = new Dialog(this);

        vertical_scroll = findViewById(R.id.vertical_scroll);

        agregarConsejos();

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
            } else if (!enviado) {
                Toast.makeText(this, R.string.first_send_waves, Toast.LENGTH_LONG).show();
            } else {
                Intent intent = new Intent(this, TrainFinished.class);
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

                NeuroSkyManager.enviarWavesTipoPositivo();

                final Handler handler = new Handler();
                Context context = this;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // set the limitations for the numeric
                        // text under the progress bar
                        int time = GlobalInfo.getTrainSectionTime();
                        if (i <= time) {
                            if(!neuroSky.isConnected()){
                                internalNotification();
                                handler.removeCallbacks(this);
                            }else{
                                progressText.setText("" + i);
                                int progress = (i * 100) / time;
                                progressBar.setProgress(progress);
                                i++;
                                handler.postDelayed(this, 1000);
                            }

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
        }else if (v == btnContinuarDialog){
            Intent intent = new Intent(this, Index.class);
            startActivity(intent);
            finish();
        }
    }

    private void agregarConsejos() {

        String consejos[] = new String[CANTIDAD_CONSEJOS];
        consejos[0] = getResources().getString(R.string.train_positive_personal_advice_1);
        consejos[1] = getResources().getString(R.string.train_positive_personal_advice_2);
        consejos[2] = getResources().getString(R.string.train_positive_personal_advice_3);
        consejos[3] = getResources().getString(R.string.train_positive_personal_advice_4);
        consejos[4] = getResources().getString(R.string.train_positive_personal_advice_5);
        consejos[5] = getResources().getString(R.string.train_positive_personal_advice_6);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 0, 40);

        for (String consejo : consejos) {
            ConstraintLayout newConsejo = (ConstraintLayout) getLayoutInflater().inflate(R.layout.component_question, null);
            newConsejo.setLayoutParams(layoutParams);
            newConsejo.setMinHeight(100);

            TextView textConsejo = (TextView) newConsejo.getViewById(R.id.text_question);
            textConsejo.setText(consejo);

            vertical_scroll.addView(newConsejo);
        }
    }

    private void internalNotification(){
        NeuroSkyManager.resetData();
        dialog.setContentView(R.layout.alert_dialog_desconnection);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        btnContinuarDialog = dialog.findViewById(R.id.btn_continuar);
        btnContinuarDialog.setOnClickListener(this);
        dialog.show();
    }
}
