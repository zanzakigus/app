package ipn.mx.app;


import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;

public class SignUp2 extends AppCompatActivity implements View.OnClickListener {

    EditText edtNombre, edtAPaterno, edtAMaterno, edtFNacimiento;
    View btnNext;

    String correo, contra;

    private int anno, mes, dia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_2);

        correo = getIntent().getStringExtra("correo");
        contra = getIntent().getStringExtra("contra");

        edtNombre = findViewById(R.id.name_input);
        edtAPaterno = findViewById(R.id.a_paterno_input);
        edtAMaterno = findViewById(R.id.a_materno_input);
        edtFNacimiento = findViewById(R.id.fecha_nacimiento_input);
        btnNext = findViewById(R.id.arrow);

        // Quitar lo editable
        // https://stackoverflow.com/questions/3928711/how-to-make-edittext-not-editable-through-xml-in-android/6174440#6174440
        edtFNacimiento.setKeyListener(null);

        // Agregar accion para que aparezca el calendario
        edtFNacimiento.setOnClickListener(this);

        // Agregar accion al boton
        btnNext.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btnNext) {
            if (edtNombre.getText().toString().equals("")) {
                Toast myToast = Toast.makeText(this, R.string.missing_name, Toast.LENGTH_LONG);
                myToast.show();
            } else if (edtAPaterno.getText().toString().equals("")) {
                Toast myToast = Toast.makeText(this, R.string.missing_a_paterno, Toast.LENGTH_LONG);
                myToast.show();
            } else if (edtAMaterno.getText().toString().equals("")) {
                Toast myToast = Toast.makeText(this, R.string.missing_a_materno, Toast.LENGTH_LONG);
                myToast.show();
            } else if (edtFNacimiento.getText().toString().equals("")) {
                Toast myToast = Toast.makeText(this, R.string.missing_f_nacimiento, Toast.LENGTH_LONG);
                myToast.show();
            } else {
                Log.d("Email", correo);
                Log.d("Password", contra);
                Log.d("Name", edtNombre.getText().toString());
                Log.d("aPaterno", edtAPaterno.getText().toString());
                Log.d("aMaterno", edtAMaterno.getText().toString());
                Log.d("fNacimiento", edtFNacimiento.getText().toString());
            }
        } else if (v == edtFNacimiento) {
            final Calendar c = Calendar.getInstance();
            dia = c.get(Calendar.DAY_OF_MONTH);
            mes = c.get(Calendar.MONTH);
            anno = c.get(Calendar.YEAR);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    String Day = formatoNumeroDosCifras(day);
                    String Month = formatoNumeroDosCifras(month + 1);
                    String fNacimiento = Day + "/" + Month + "/" + year;
                    edtFNacimiento.setText(fNacimiento);
                }
            }, dia, mes, anno);
            datePickerDialog.show();
        }
    }

    private String formatoNumeroDosCifras(int num) {
        return (num < 10) ? "0" + num : Integer.toString(num);
    }
}
