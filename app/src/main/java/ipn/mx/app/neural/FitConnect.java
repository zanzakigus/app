package ipn.mx.app.neural;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Set;

import io.reactivex.rxjava3.annotations.NonNull;
import ipn.mx.app.R;
import ipn.mx.app.SignUp2;
import ipn.mx.app.neurosky.library.NeuroSky;
import ipn.mx.app.neurosky.library.exception.BluetoothNotEnabledException;
import ipn.mx.app.neurosky.library.listener.ExtendedDeviceMessageListener;
import ipn.mx.app.neurosky.library.message.enums.BrainWave;
import ipn.mx.app.neurosky.library.message.enums.Signal;
import ipn.mx.app.neurosky.library.message.enums.State;

public class FitConnect extends AppCompatActivity implements View.OnClickListener {
    View btnNext, btnConnect;

    State estadoDiadema = State.UNKNOWN;
    BluetoothAdapter bluetoothAdapter;
    private NeuroSky neuroSky;
    private final static String LOG_TAG = "NeuroSky";


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.section_fit_ini);

        neuroSky = createNeuroSky();


        btnNext = findViewById(R.id.arrow);
        btnConnect = findViewById(R.id.button_connect);

        btnNext.setOnClickListener(this);
        btnConnect.setOnClickListener(this);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    }

    @Override
    public void onClick(View v) {
        if (v == btnNext) {
            if (!estadoDiadema.equals(State.CONNECTED)) {
                Toast myToast = Toast.makeText(this, R.string.no_connect, Toast.LENGTH_LONG);
                myToast.show();
            }else {
                Intent intent = new Intent(this, FitNeural1.class);
                startActivity(intent);
                finish();
            }


        } else if (v == btnConnect) {
            if (!bluetoothAdapter.isEnabled()) {
                bluetoothAdapter.enable();
            }
            try {
                neuroSky.connect();
            } catch (BluetoothNotEnabledException e) {
                Toast myToast = Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG);
                myToast.show();
                Log.d(LOG_TAG, e.getMessage());
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
        for (BrainWave brainWave : brainWaves) {
            Log.d(LOG_TAG + "-WAVES", String.format("brain: %s: %d", brainWave.toString(), brainWave.getValue()));
        }

    }


}
