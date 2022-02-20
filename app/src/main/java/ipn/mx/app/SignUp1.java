package ipn.mx.app;


import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SignUp1 extends AppCompatActivity implements View.OnClickListener, TextView.OnEditorActionListener {

    EditText edtCorreo, edtContra;
    View btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_1);

        btnNext = findViewById(R.id.arrow);
        edtCorreo = findViewById(R.id.email_input);
        edtContra = findViewById(R.id.password_input);

        btnNext.setOnClickListener(this);
        edtContra.setOnEditorActionListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btnNext) {
            if (edtCorreo.getText().toString().equals("")) {
                Toast.makeText(this, R.string.missing_email, Toast.LENGTH_LONG).show();
                return;
            }
            if (edtContra.getText().toString().equals("")) {
                Toast.makeText(this, R.string.missing_password, Toast.LENGTH_LONG).show();
                return;
            }
            enterSignIn2();
        }
    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        boolean handled = false;
        if (edtContra.getId() == textView.getId() && i == EditorInfo.IME_ACTION_SEND) {
            enterSignIn2();
            handled = true;
        }
        return handled;
    }

    public void enterSignIn2() {
        Intent intent = new Intent(this, SignUp2.class);
        intent.putExtra("correo", edtCorreo.getText().toString());
        intent.putExtra("contra", edtContra.getText().toString());
        startActivity(intent);
        finish();
    }
}