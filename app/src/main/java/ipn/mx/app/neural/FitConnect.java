package ipn.mx.app.neural;

import android.bluetooth.BluetoothAdapter;

import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import ipn.mx.app.R;
import ipn.mx.app.neurosky.NeuroSkyManager;
import ipn.mx.app.neurosky.library.NeuroSky;
import ipn.mx.app.neurosky.library.exception.BluetoothNotEnabledException;

public class FitConnect extends AppCompatActivity implements View.OnClickListener {
    View btnNext, btnConnect;

    BluetoothAdapter bluetoothAdapter;
    NeuroSky neuroSky;
    private final static String LOG_TAG = "NeuroSky";


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.section_fit_ini);

        NeuroSkyManager neuroSkyManager = new NeuroSkyManager(this);
        neuroSky = NeuroSkyManager.getNeuroSky();
        btnNext = findViewById(R.id.arrow);
        btnConnect = findViewById(R.id.button_connect);

        btnNext.setOnClickListener(this);
        btnConnect.setOnClickListener(this);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    }

    @Override
    public void onClick(View v) {
        if (v == btnNext) {
            if (!NeuroSkyManager.getNeuroSky().isConnected()) {
                Toast myToast = Toast.makeText(this, R.string.no_connect, Toast.LENGTH_LONG);
                myToast.show();
            } else {
                Intent intent = new Intent(this, FitNeural1.class);
                startActivity(intent);
                finish();
            }


        } else if (v == btnConnect) {
            if (neuroSky != null && !neuroSky.isConnected()) {
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
            } else {
                Toast myToast = Toast.makeText(this, R.string.connected_diadema, Toast.LENGTH_LONG);
                myToast.show();
            }

        }
    }


}
