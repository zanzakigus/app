package ipn.mx.app.neural;


import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;

import io.reactivex.rxjava3.annotations.NonNull;
import ipn.mx.app.R;
import ipn.mx.app.neurosky.library.NeuroSky;
import ipn.mx.app.neurosky.library.exception.BluetoothNotEnabledException;
import ipn.mx.app.neurosky.library.listener.ExtendedDeviceMessageListener;
import ipn.mx.app.neurosky.library.message.enums.BrainWave;
import ipn.mx.app.neurosky.library.message.enums.Signal;
import ipn.mx.app.neurosky.library.message.enums.State;

import android.os.Handler;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Set;


public class FitNeural1 extends AppCompatActivity implements View.OnClickListener {

    private ProgressBar progressBar;
    private TextView progressText;
    View btnNext;
    int i = 0;

    State estadoDiadema = State.UNKNOWN;
    BluetoothAdapter bluetoothAdapter;
    private NeuroSky neuroSky;
    private final static String LOG_TAG = "NeuroSky";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.section_positive_fit);

        neuroSky = createNeuroSky();
        neuroSky.connect();
        if(neuroSky.isConnected()){
            Toast myToast = Toast.makeText(this, "si rsts", Toast.LENGTH_LONG);
            myToast.show();
        }else{
            Toast myToast = Toast.makeText(this, "ni a", Toast.LENGTH_LONG);
            myToast.show();
        }

        // set the id for the progressbar and progress text
        progressBar = findViewById(R.id.progress_bar);
        progressText = findViewById(R.id.progress_text);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // set the limitations for the numeric
                // text under the progress bar
                if (i <= 100) {
                    progressText.setText("" + i);
                    progressBar.setProgress(i);
                    i++;
                    handler.postDelayed(this, 200);
                } else {
                    handler.removeCallbacks(this);
                }
            }
        }, 200);
    }

    @Override
    public void onClick(View v) {
        if (v == btnNext) {
            if (!estadoDiadema.equals(State.CONNECTED)) {
                Toast myToast = Toast.makeText(this, R.string.no_connect, Toast.LENGTH_LONG);
                myToast.show();
            } else {
                Intent intent = new Intent(this, FitNeural1.class);
                startActivity(intent);
                finish();
            }


        }
    }

    @NonNull
    private NeuroSky createNeuroSky() {
        return new NeuroSky(new ExtendedDeviceMessageListener() {
            @Override
            public void onStateChange(State state) {
                handleStateChange(state);
            }

            @Override
            public void onSignalChange(Signal signal) {
                handleSignalChange(signal);
            }

            @Override
            public void onBrainWavesChange(Set<BrainWave> brainWaves) {
                handleBrainWavesChange(brainWaves);
            }
        });
    }

    private void handleStateChange(final State state) {
        if (estadoDiadema.equals(State.UNKNOWN) || estadoDiadema.equals(State.DISCONNECTED) || estadoDiadema.equals(State.NOT_PAIRED)) {
            try {
                neuroSky.connect();
            } catch (BluetoothNotEnabledException e) {
                Toast myToast = Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG);
                myToast.show();
                Log.d(LOG_TAG, e.getMessage());
            }
        }

        Toast myToast;
        switch (state) {
            case CONNECTED:
                myToast = Toast.makeText(this, R.string.connected_diadema, Toast.LENGTH_LONG);
                myToast.show();
                Log.d(LOG_TAG, LOG_TAG + ": " + estadoDiadema.toString());
                neuroSky.start();
            case NOT_FOUND:
                myToast = Toast.makeText(this, R.string.no_found_diadema, Toast.LENGTH_LONG);
                myToast.show();
                Log.d(LOG_TAG, LOG_TAG + ": " + estadoDiadema.toString());
                break;
            case CONNECTING:
                myToast = Toast.makeText(this, R.string.searching_diadema, Toast.LENGTH_LONG);
                myToast.show();
                Log.d(LOG_TAG, LOG_TAG + ": " + estadoDiadema.toString());
                break;
        }
        estadoDiadema = state;
        Log.d(LOG_TAG, state.toString());
    }

    private void handleSignalChange(final Signal signal) {
        Log.d(LOG_TAG + "-SIGNALS", String.format("%s: %d", signal.toString(), signal.getValue()));
    }


    private void handleBrainWavesChange(final Set<BrainWave> brainWaves) {
        // Request a string response from the provided URL.
        HashMap<String, String> params = new HashMap<String, String>();

        for (BrainWave brainWave : brainWaves) {
            params.put(brainWave.toString(), String.valueOf(brainWave.getValue()));
            Log.d(LOG_TAG, String.format("brain: %s: %d", brainWave.toString(), brainWave.getValue()));
            //Log.d(LOG_TAG, String.format("brain: %s: %d", brainWave.toString(), brainWave.getValue()));
        }
        params.put("tipo","1");

        RequestQueue queue = Volley.newRequestQueue(this);
        //String url ="http://192.168.3.20:8000/polls?tipo="+tipo_seleccionado;
        String url = this.getResources().getString(R.string.server_host) + "/usuario";

        JsonObjectRequest stringRequest = null;


        stringRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        // Display the first 500 characters of the response string.


                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                System.out.println(error);
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        // Add the request to the RequestQueue.
        queue.add(stringRequest);


    }
}
