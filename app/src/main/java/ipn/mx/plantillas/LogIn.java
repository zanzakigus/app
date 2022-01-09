package ipn.mx.plantillas;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LogIn extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_in);

        TextView txtEmail = findViewById(R.id.email_input);
        TextView txtPass = findViewById(R.id.password_input);
    }
}