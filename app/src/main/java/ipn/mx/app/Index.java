package ipn.mx.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Index extends AppCompatActivity implements View.OnClickListener {

    EditText edtCorreo, edtContra;
    View btnNext;
    TextView tevNombreUsuario;

    // creating constant keys for shared preferences.
    public static String SHARED_PREFS;

    // key for storing email.
    public static String EMAIL_KEY;

    // key for storing password.
    public static String PASSWORD_KEY;

    // variable for shared preferences.
    SharedPreferences sharedpreferences;

    //logged variables
    private String loggedEmail;
    private String loggedPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);

/*        btnNext = findViewById(R.id.arrow);
        edtCorreo = findViewById(R.id.email_input);
        edtContra = findViewById(R.id.password_input);*/
        tevNombreUsuario = findViewById(R.id.title_username);

        /*btnNext.setOnClickListener(this);*/

        // initializing shared preferences keys.
        SHARED_PREFS = this.getResources().getString(R.string.shared_key);
        EMAIL_KEY = this.getResources().getString(R.string.logged_email_key);
        PASSWORD_KEY = this.getResources().getString(R.string.logged_email_key);

        // initializing our shared preferences.
        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

        loggedEmail = sharedpreferences.getString(EMAIL_KEY, null);
        loggedPassword = sharedpreferences.getString(PASSWORD_KEY, null);
        tevNombreUsuario.setText(loggedPassword);
        if (loggedPassword == null || loggedEmail == null) {
            Intent intent = new Intent(this, Login.class);
            /*intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);*/
            startActivity(intent);
            finish();
        }else{

        }

    }

    @Override
    public void onClick(View v) {
/*        if (v == btnNext) {
            if (edtCorreo.getText().toString().equals("")) {
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
            }
        }*/
    }
}
