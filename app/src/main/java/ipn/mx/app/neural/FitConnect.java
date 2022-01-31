package ipn.mx.app.neural;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import ipn.mx.app.R;

public class FitConnect extends AppCompatActivity implements View.OnClickListener {
    View btnNext, btnConnect;
    boolean boolConnect = false;
    BluetoothAdapter bluetoothAdapter;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.section_fit_ini);

        btnNext = findViewById(R.id.arrow);
        btnConnect = findViewById(R.id.button_connect);

        btnNext.setOnClickListener(this);
        btnConnect.setOnClickListener(this);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    }

    @Override
    public void onClick(View v) {
        if (v == btnNext) {
            if(!boolConnect){
                Toast myToast = Toast.makeText(this, R.string.no_connect, Toast.LENGTH_LONG);
                myToast.show();
            }
        }else if(v == btnConnect){
            if(!bluetoothAdapter.isEnabled()){
                bluetoothAdapter.enable();
            }

            Toast myToast = Toast.makeText(this, R.string.no_connect, Toast.LENGTH_LONG);
            myToast.show();
        }
    }
}
