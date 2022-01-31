package ipn.mx.app.neural;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import ipn.mx.app.R;
import ipn.mx.app.SignUp2;

public class FitConnect extends AppCompatActivity implements View.OnClickListener {
    View btnNext, btnConnect;
    boolean boolConnect = false;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.section_fit_ini);

        btnNext = findViewById(R.id.arrow);
        btnConnect = findViewById(R.id.button_connect);

    }

    @Override
    public void onClick(View v) {
        if (v == btnNext) {
            if(!boolConnect){
                Toast myToast = Toast.makeText(this, R.string.missing_password, Toast.LENGTH_LONG);
                myToast.show();
            }
            /*if (edtCorreo.getText().toString().equals("")) {
                Toast myToast = Toast.makeText(this, R.string.missing_email, Toast.LENGTH_LONG);
                myToast.show();
            } else if (edtContra.getText().toString().equals("")) {
                Toast myToast = Toast.makeText(this, R.string.missing_password, Toast.LENGTH_LONG);
                myToast.show();
            } else {
                Intent intent = new Intent(this, SignUp2.class);
                intent.putExtra("correo", edtCorreo.getText().toString());
                intent.putExtra("contra", edtContra.getText().toString());
                startActivity(intent);
                finish();
            }*/
        }
    }
}
