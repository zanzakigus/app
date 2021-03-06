package ipn.mx.app.neuralfit;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import ipn.mx.app.Index;
import ipn.mx.app.R;
import ipn.mx.app.global.GlobalInfo;
import ipn.mx.app.neurosky.NeuroSkyManager;
import ipn.mx.app.neurosky.library.NeuroSky;

public class TrainNegativeVisual extends AppCompatActivity implements View.OnClickListener {


    TextView tvRecomendation1, tvRecomendation2, tvRecomendation3;
    ArrayList<String[]> textRecomendations;

    View btnNext;
    static int i = 1;
    boolean enviado = false;
    private ProgressBar progressBar;
    private TextView progressText;
    private NeuroSky neuroSky;

    Dialog dialog;
    Button btnContinuarDialog;

    ImageView youtubeLogo;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.train_negative_visual);
        dialog = new Dialog(this);

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
        youtubeLogo = findViewById(R.id.youtube_logo);

        btnNext.setOnClickListener(this);
        progressBar.setOnClickListener(this);
        youtubeLogo.setOnClickListener(this);

        tvRecomendation1 = findViewById(R.id.train_negative_visual_recomendations_1);
        tvRecomendation2 = findViewById(R.id.train_negative_visual_recomendations_2);
        tvRecomendation3 = findViewById(R.id.train_negative_visual_recomendations_3);

        tvRecomendation1.setOnClickListener(this);
        tvRecomendation2.setOnClickListener(this);
        tvRecomendation3.setOnClickListener(this);


        // Poner el texto de los video recomendados
        textRecomendations = new ArrayList<String[]>();

        String[] auxCadenas = new String[3];
        auxCadenas[0] = getResources().getString(R.string.train_negative_visual_recomendations_1);
        auxCadenas[1] = getResources().getString(R.string.train_negative_visual_recomendations_2);
        auxCadenas[2] = getResources().getString(R.string.train_negative_visual_recomendations_3);

        int indice = 0;
        for (String auxCadena : auxCadenas) {
            indice = auxCadena.lastIndexOf(" ");
            String[] urlSeparada = new String[2];
            urlSeparada[0] = auxCadena.substring(0, indice);
            urlSeparada[1] = auxCadena.substring(indice + 1);
//            Log.d("Cadena separada", "Cadena creada, nombre:" + urlSeparada[0] + "url:" + urlSeparada[1]);
            textRecomendations.add(urlSeparada);
        }

        tvRecomendation1.setText("  " + textRecomendations.get(0)[0]);
        tvRecomendation2.setText("  " + textRecomendations.get(1)[0]);
        tvRecomendation3.setText("  " + textRecomendations.get(2)[0]);
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
                            if (!neuroSky.isConnected()) {
                                internalNotification();
                                handler.removeCallbacks(this);
                            } else {
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
        } else if (tvRecomendation1 == v) {
            Uri urlVideo = Uri.parse(textRecomendations.get(0)[1]);
            Intent videoIntent = new Intent(Intent.ACTION_VIEW, urlVideo);
            try {
                startActivity(videoIntent);
            } catch (ActivityNotFoundException e) {
                Log.d("Al abrir el video", "onClick: " + e.toString());
                Toast.makeText(this, getResources().getString(R.string.train_negative_visual_error_open_video), Toast.LENGTH_LONG).show();
            }
        } else if (tvRecomendation2 == v) {
            Uri urlVideo = Uri.parse(textRecomendations.get(1)[1]);
            Intent videoIntent = new Intent(Intent.ACTION_VIEW, urlVideo);
            try {
                startActivity(videoIntent);
            } catch (ActivityNotFoundException e) {
                Log.d("Al abrir el video", "onClick: " + e.toString());
                Toast.makeText(this, getResources().getString(R.string.train_negative_visual_error_open_video), Toast.LENGTH_LONG).show();
            }
        } else if (tvRecomendation3 == v) {
            Uri urlVideo = Uri.parse(textRecomendations.get(2)[1]);
            Intent videoIntent = new Intent(Intent.ACTION_VIEW, urlVideo);
            try {
                startActivity(videoIntent);
            } catch (ActivityNotFoundException e) {
                Log.d("Al abrir el video", "onClick: " + e.toString());
                Toast.makeText(this, getResources().getString(R.string.train_negative_visual_error_open_video), Toast.LENGTH_LONG).show();
            }
        } else if (v == youtubeLogo) {
            Uri urlVideo = Uri.parse("https://youtu.be/");
            Intent videoIntent = new Intent(Intent.ACTION_VIEW, urlVideo);
            try {
                startActivity(videoIntent);
            } catch (ActivityNotFoundException e) {
                Log.d("Al abrir youtube", "onClick: " + e.toString());
                Toast.makeText(this, getResources().getString(R.string.train_negative_visual_error_open_video), Toast.LENGTH_LONG).show();
            }
        } else if (v == btnContinuarDialog) {
            Intent intent = new Intent(this, Index.class);
            startActivity(intent);
            finish();
        }
    }

    private void internalNotification() {
        NeuroSkyManager.resetData();
        dialog.setContentView(R.layout.alert_dialog_desconnection);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        btnContinuarDialog = dialog.findViewById(R.id.btn_continuar);
        btnContinuarDialog.setOnClickListener(this);
        dialog.show();
    }

}


