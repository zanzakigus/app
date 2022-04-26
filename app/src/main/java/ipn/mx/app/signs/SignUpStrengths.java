package ipn.mx.app.signs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;

import ipn.mx.app.Index;
import ipn.mx.app.PeticionAPI;
import ipn.mx.app.R;
import ipn.mx.app.neurosky.NeuroSkyManager;

public class SignUpStrengths extends AppCompatActivity implements View.OnClickListener, TextView.OnEditorActionListener {

    public static String SHARED_PREFS;
    public static String EMAIL_KEY;
    public static String PASSWORD_KEY;
    View btnNext;
    String correo, contra, nombre, apPaterno, apMaterno, fechaNacimiento;
    SharedPreferences sharedpreferences;
    EditText edtCadena;
    ImageView imvAgregar,imvQuestion;
    LinearLayout scrollView;
    HashSet<String> tagCadenas = new HashSet<>();

    Dialog dialog;
    Button btnContinuarDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_strengths);

        correo = getIntent().getStringExtra("correo");
        contra = getIntent().getStringExtra("contra");
        nombre = getIntent().getStringExtra("nombre");
        apPaterno = getIntent().getStringExtra("ap_paterno");
        apMaterno = getIntent().getStringExtra("ap_materno");
        fechaNacimiento = getIntent().getStringExtra("fecha_nacimiento");

        dialog = new Dialog(this);
        btnNext = findViewById(R.id.arrow);
        edtCadena = findViewById(R.id.cadena_input);
        imvAgregar = findViewById(R.id.agregar);
        scrollView = findViewById(R.id.scrollview);
        imvQuestion = findViewById(R.id.question);

        // Agregar accion al boton
        btnNext.setOnClickListener(this);
        imvAgregar.setOnClickListener(this);
        edtCadena.setOnEditorActionListener(this);
        imvQuestion.setOnClickListener(this);

        SHARED_PREFS = this.getResources().getString(R.string.shared_key);
        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        EMAIL_KEY = this.getResources().getString(R.string.logged_email_key);
        PASSWORD_KEY = this.getResources().getString(R.string.logged_password_key);
    }

    @Override
    public void onClick(View v) {

        if (v == btnNext) {
            if (tagCadenas.isEmpty()) {
                Toast myToast = Toast.makeText(this, R.string.misssing_strengths, Toast.LENGTH_LONG);
                myToast.show();
            } else {

                PeticionAPI api = new PeticionAPI(this);
                HashMap<String, String> params = new HashMap<>();

                params.put("correo", correo);
                params.put("nombre", nombre);
                params.put("ap_paterno", apPaterno);
                params.put("ap_materno", apMaterno);
                params.put("password", contra);
                params.put("fecha_nacimiento", fechaNacimiento);
                params.put("strengths", tagCadenas.toString());

                SignUpStrengths signUpStrengths = new SignUpStrengths();
                Class[] parameterTypes = new Class[2];
                parameterTypes[0] = JSONObject.class;
                parameterTypes[1] = Context.class;
                Method functionToPass = null;
                try {
                    functionToPass = SignUpStrengths.class.getMethod("onSignUp", parameterTypes);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }

                api.peticionPOST(this.getResources().getString(R.string.server_host) + "/usuario", params, signUpStrengths, functionToPass);
            }
        } else if (imvAgregar == v) {
            String cadena = edtCadena.getText().toString();
            if (tagCadenas.contains(cadena)) {
                Toast.makeText(this, R.string.text_add_strength_repeated, Toast.LENGTH_SHORT).show();
            } else {
                addStrength(cadena);
            }

        } else if (imvQuestion == v){
            internalNotification();
        }else if (btnContinuarDialog == v){
            dialog.dismiss();
        }
    }

    private void internalNotification(){
        dialog.setContentView(R.layout.alert_dialog_explanation_stren);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        btnContinuarDialog = dialog.findViewById(R.id.btn_continuar);
        btnContinuarDialog.setOnClickListener(this);
        dialog.show();
    }

    public void addStrength(String cadena) {
        if (cadena.length() == 0) {
            Toast.makeText(this, R.string.text_empty_strengt, Toast.LENGTH_LONG).show();
            return;
        }
        edtCadena.setText("");

        Space space1 = new Space(this);
        Space space2 = new Space(this);
        Space space3 = new Space(this);

        TextView newInfo = new TextView(this);
        newInfo.setText(cadena);
        newInfo.setTextAppearance(this, R.style.text_gray);
        newInfo.setPadding(0, 0, 20, 0);
        newInfo.setGravity(Gravity.CENTER);

        ImageView icon = new ImageView(this);
        icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_remove_outline_circle_red));
        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tagCadenas.remove(cadena);
                scrollView.removeAllViews();
                for (String tag : tagCadenas) {
                    addStrength(tag);
                }

            }
        });

        LinearLayout newTag = new LinearLayout(this);
        newTag.setBackground(getResources().getDrawable(R.drawable.background_estrategy));
        newTag.setOrientation(LinearLayout.HORIZONTAL);
        newTag.setGravity(Gravity.CENTER);
        newTag.setPadding(50, 10, 30, 10);
        newTag.addView(newInfo);
        newTag.addView(icon);

        icon.getLayoutParams().width = 70;
        icon.getLayoutParams().height = 70;
        icon.requestLayout();

        if (scrollView.getChildCount() == 0) {
            System.out.println("aqui toy en 0" + cadena);
            LinearLayout ly = new LinearLayout(this);
            ly.setOrientation(LinearLayout.HORIZONTAL);
            ly.setGravity(Gravity.CENTER);
            ly.setPadding(0, 10, 0, 10);
            ly.addView(space1);

            scrollView.addView(ly);
            ly.getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;
            space1.setLayoutParams(new LinearLayout.LayoutParams(0, 1, 1.0f));
        }

        LinearLayout ly = (LinearLayout) scrollView.getChildAt(scrollView.getChildCount() - 1);
        ly.addView(newTag);
        ly.addView(space3);


        if (!validarTamano(cadena, scrollView)) {
            System.out.println("aqui toy en termino" + cadena);
            ly.removeViewAt(ly.getChildCount() - 1);
            ly.removeViewAt(ly.getChildCount() - 1);


            LinearLayout ly2 = new LinearLayout(this);
            ly2.setOrientation(LinearLayout.HORIZONTAL);
            ly2.setGravity(Gravity.CENTER);
            ly2.setPadding(0, 10, 0, 10);
            ly2.addView(space2);
            ly2.addView(newTag);
            ly2.addView(space3);
            scrollView.addView(ly2);

            ly2.getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;
            space2.setLayoutParams(new LinearLayout.LayoutParams(0, 1, 1.0f));
        }

        // Esto ponerlo despues de agregarlo
        newTag.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
        newTag.requestLayout();

        space3.setLayoutParams(new LinearLayout.LayoutParams(0, 1, 1.0f));
        tagCadenas.add(cadena);
        /*Toast myToast = Toast.makeText(this, R.string.text_add_strength, Toast.LENGTH_SHORT);
        myToast.show();*/
    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        boolean handled = false;
        if (edtCadena.getId() == textView.getId() && (i == EditorInfo.IME_ACTION_NEXT || i == EditorInfo.IME_NULL)) {
            String cadena = edtCadena.getText().toString();
            if (tagCadenas.contains(cadena)) {
                Toast.makeText(this, R.string.text_add_strength_repeated, Toast.LENGTH_SHORT).show();
            } else if (cadena.length() != 0) {
                addStrength(cadena);
            }

            handled = true;
        }
        return handled;
    }

    public void onSignUp(JSONObject response, Context context) throws JSONException {

        if (response.has("error")) {
            Log.e("ERROR", "ERROR onSignUp: " + response.getString("error"));
            Toast myToast = Toast.makeText(context, "ERROR " + (500) + " onSignUp: " + response.getString("error"), Toast.LENGTH_LONG);
            myToast.show();
            return;
        }
        int status = response.getInt("status");
        String message = response.getString("message");
        if (status != 200) {
            Log.e("ERROR", "ERROR onSignUp: " + message);
            Toast myToast = Toast.makeText(context, "ERROR " + (status) + " onSignUp: " + message, Toast.LENGTH_LONG);
            myToast.show();
            return;
        }
        Toast myToast = Toast.makeText(context, "Usuario Creado", Toast.LENGTH_LONG);
        myToast.show();

        String Shared = context.getResources().getString(R.string.shared_key);
        SharedPreferences SharedP = context.getSharedPreferences(Shared, Context.MODE_PRIVATE);
        String emailKey = context.getResources().getString(R.string.logged_email_key);
        String passKey = context.getResources().getString(R.string.logged_password_key);

        SharedPreferences.Editor editor = SharedP.edit();
        editor.putString(emailKey, correo);
        editor.putString(passKey, contra);
        editor.apply();

        Intent intent = new Intent(context, Index.class);
        context.startActivity(intent);
        Log.i("INFO", "INFO: Usuario Creado");
        ((Activity) context).finish();
    }

    private boolean validarTamano(String cadena, LinearLayout scrollView) {
        LinearLayout ultimoHijo = (LinearLayout) scrollView.getChildAt(scrollView.getChildCount() - 1);
        int hijos = ultimoHijo.getChildCount();
        int maxWidth = scrollView.getWidth();
        int actualWidth = 0;
        boolean result = false;
        // Son 170 de espacio que ya se tiene en la tag y por cada letra se considero 20 de espacio
        int newWidth = cadena.length() * 20 + 170;

        for (int i = 1; i < hijos; i++) {
            LinearLayout ly = (LinearLayout) ultimoHijo.getChildAt(i++);

            actualWidth += ((TextView) ly.getChildAt(0)).getText().toString().length() * 20 + 170;
        }
        if (actualWidth + newWidth <= maxWidth - 40)
            result = true;
        return result;
    }

}
