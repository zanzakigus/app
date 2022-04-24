package ipn.mx.app.updateinfo;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;

import ipn.mx.app.Index;
import ipn.mx.app.PeticionAPI;
import ipn.mx.app.R;
import ipn.mx.app.signs.Login;

public class UpdateStrengths extends AppCompatActivity implements View.OnClickListener, TextView.OnEditorActionListener {

    public static String SHARED_PREFS;
    public static String EMAIL_KEY;
    public static String PASSWORD_KEY;


    SharedPreferences sharedpreferences;
    EditText edtCadena;
    ImageView imvAgregar,imvQuestion;
    LinearLayout scrollView;
    HashSet<String> tagCadenas = new HashSet<>();
    Context context;
    Button btnUpdate;

    //logged variables
    private String loggedEmail;
    private String loggedPassword;

    Dialog dialog;
    Button btnContinuarDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_strengths);

        dialog = new Dialog(this);

        context = this;


        edtCadena = findViewById(R.id.cadena_input);
        imvAgregar = findViewById(R.id.agregar);
        scrollView = findViewById(R.id.scrollview);
        btnUpdate = findViewById(R.id.update_stre);
        imvQuestion = findViewById(R.id.question);


        // Agregar accion al boton

        imvAgregar.setOnClickListener(this);
        edtCadena.setOnEditorActionListener(this);
        btnUpdate.setOnClickListener(this);
        imvQuestion.setOnClickListener(this);

        SHARED_PREFS = this.getResources().getString(R.string.shared_key);
        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        EMAIL_KEY = this.getResources().getString(R.string.logged_email_key);
        PASSWORD_KEY = this.getResources().getString(R.string.logged_password_key);


        // initializing our shared preferences.
        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

        loggedEmail = sharedpreferences.getString(EMAIL_KEY, null);
        loggedPassword = sharedpreferences.getString(PASSWORD_KEY, null);

        if (loggedPassword == null || loggedEmail == null) {
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
            finish();
            return;
        }


        // Se hace peticion para obtener la informacion del usuario
        PeticionAPI api = new PeticionAPI(this);
        HashMap<String, String> params = new HashMap<>();
        params.put("correo", loggedEmail);
        params.put("password", loggedPassword);

        UpdateStrengths updateStrengths = new UpdateStrengths();
        updateStrengths.context = this;
        updateStrengths.edtCadena = edtCadena;
        updateStrengths.tagCadenas = tagCadenas;
        updateStrengths.scrollView = scrollView;
        Class[] parameterTypes = new Class[2];
        parameterTypes[0] = JSONObject.class;
        parameterTypes[1] = Context.class;
        Method functionToPass = null;
        try {
            functionToPass = UpdateStrengths.class.getMethod("putInfoOnScreen", parameterTypes);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        api.peticionGET(this.getResources().getString(R.string.server_host) + "/fortalezas", params, updateStrengths, functionToPass);

    }

    @Override
    public void onClick(View v) {

        if (v == btnUpdate) {
            if (tagCadenas.isEmpty()) {
                Toast myToast = Toast.makeText(this, R.string.misssing_strengths, Toast.LENGTH_LONG);
                myToast.show();
            } else {

                PeticionAPI api = new PeticionAPI(this);
                HashMap<String, String> params = new HashMap<>();

                params.put("correo", loggedEmail);
                params.put("password", loggedPassword);
                params.put("strengths", tagCadenas.toString());

                UpdateStrengths updateStrengths = new UpdateStrengths();
                Class[] parameterTypes = new Class[2];
                parameterTypes[0] = JSONObject.class;
                parameterTypes[1] = Context.class;
                Method functionToPass = null;
                try {
                    functionToPass = UpdateStrengths.class.getMethod("updateStrengths", parameterTypes);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }

                api.peticionPOST(this.getResources().getString(R.string.server_host) + "/fortalezas", params, updateStrengths, functionToPass);
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
            Toast.makeText(context, R.string.text_empty_strengt, Toast.LENGTH_LONG).show();
            return;
        }
        edtCadena.setText("");

        Space space1 = new Space(context);
        Space space2 = new Space(context);
        Space space3 = new Space(context);

        TextView newInfo = new TextView(context);
        newInfo.setText(cadena);
        newInfo.setTextAppearance(context, R.style.text_gray);
        newInfo.setPadding(0, 0, 20, 0);
        newInfo.setGravity(Gravity.CENTER);

        ImageView icon = new ImageView(context);
        icon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_remove_outline_circle_red));
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

        LinearLayout newTag = new LinearLayout(context);
        newTag.setBackground(context.getResources().getDrawable(R.drawable.background_estrategy));
        newTag.setOrientation(LinearLayout.HORIZONTAL);
        newTag.setGravity(Gravity.CENTER);
        newTag.setPadding(50, 15, 30, 15);
        newTag.addView(newInfo);
        newTag.addView(icon);

        icon.getLayoutParams().width = 70;
        icon.getLayoutParams().height = 70;
        icon.requestLayout();

        if (scrollView.getChildCount() == 0) {
            System.out.println("aqui toy en 0" + cadena);
            LinearLayout ly = new LinearLayout(context);
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


            LinearLayout ly2 = new LinearLayout(context);
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
        /*Toast myToast = Toast.makeText(context, R.string.text_add_strength, Toast.LENGTH_SHORT);
        myToast.show();*/
    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        boolean handled = false;
        if (edtCadena.getId() == textView.getId() && (i == EditorInfo.IME_ACTION_DONE || i == EditorInfo.IME_NULL)) {
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

    public void putInfoOnScreen(JSONObject response, Context context) throws JSONException {
        if (response.has("error")) {
            Log.e("ERROR", "ERROR putInfoOnScreen: " + response.getString("error"));
            Toast myToast = Toast.makeText(context, "ERROR " + (500) + " putInfoOnScreen: " + response.getString("error"), Toast.LENGTH_LONG);
            myToast.show();
            return;
        }

        int status = response.getInt("status");
        String message = response.getString("message");
        if (status != 200) {
            Log.w("WARNING", "WARNING putInfoOnScreen: " + message);
            Toast myToast = Toast.makeText(context, "ERROR " + (status) + " putInfoOnScreen: " + message, Toast.LENGTH_LONG);
            myToast.show();
            return;
        }

        JSONArray fortalezas = response.getJSONArray("message");


        for (int i = 0; i < fortalezas.length(); i++) {
            addStrength(fortalezas.getJSONObject(i).getString("fortaleza_texto"));
        }

    }

    public void updateStrengths(JSONObject response, Context context) throws JSONException {

        if (response.has("error")) {
            Log.e("ERROR", "ERROR updateStrengths: " + response.getString("error"));
            Toast myToast = Toast.makeText(context, "ERROR " + (500) + " updateStrengths: " + response.getString("error"), Toast.LENGTH_LONG);
            myToast.show();
            return;
        }
        int status = response.getInt("status");
        String message = response.getString("message");
        if (status != 200) {
            Log.e("ERROR", "ERROR updateStrengths: " + message);
            Toast myToast = Toast.makeText(context, "ERROR " + (status) + " updateStrengths: " + message, Toast.LENGTH_LONG);
            myToast.show();
            return;
        }
        Toast myToast = Toast.makeText(context, R.string.text_update_stre_succed, Toast.LENGTH_LONG);
        myToast.show();

        Intent intent = new Intent(context, Index.class);
        context.startActivity(intent);
        Log.i("INFO", "INFO: " + R.string.text_update_stre_succed);
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