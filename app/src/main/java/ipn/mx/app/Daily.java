package ipn.mx.app;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.XmlResourceParser;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;

import ipn.mx.app.global.GlobalInfo;
import ipn.mx.app.service.SendNotificationsService;
import ipn.mx.app.signs.Login;
import ipn.mx.app.strategies.StrategyPhrases;

public class Daily extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener {

    private static final String TAG = Daily.class.getSimpleName();
    private static String nameBitacora = "_bitacora.txt";

    ArrayList<String[]> agradecimientos;
    File fileBitacora;
    String dateSearch = null;

    EditText dateInput;
    ImageView btnAgregar;

    Button btnHome, btnGraph, btnNotification, btnUser, btnDaily;

    Dialog dialog;
    Button dialogBtnAgregar;
    EditText dialogNuevoAgradecimiento;
    Button dialogBtnModificar;
    EditText dialogCambiarAgradecimiento;

    String viejoAgradecimiento = "";


    public static String SHARED_PREFS;
    public static String EMAIL_KEY;
    public static String PASSWORD_KEY;
    public static String NOMBRE_KEY;

    //Http request variables
    RequestQueue queue;
    String host;
    // variable for shared preferences.
    SharedPreferences sharedpreferences;

    //logged variables
    private String loggedEmail;
    private String loggedPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.daily);

        agradecimientos = new ArrayList<>();

        dateInput = findViewById(R.id.date_input);
        btnAgregar = findViewById(R.id.agregar);

        dateInput.setOnClickListener(this);
        btnAgregar.setOnClickListener(this);

        dateInput.setOnFocusChangeListener(this);
        dateInput.setKeyListener(null);

        dialog = new Dialog(this);

        btnHome = findViewById(R.id.icon_home);
        btnGraph = findViewById(R.id.icon_graph);
        btnNotification = findViewById(R.id.icon_notifications);
        btnUser = findViewById(R.id.icon_user);
        btnDaily = findViewById(R.id.icon_daily);
        btnDaily.setBackgroundResource(R.drawable.ic_bookmark_border);

        btnHome.setOnClickListener(this);
        btnGraph.setOnClickListener(this);
        btnNotification.setOnClickListener(this);
        btnUser.setOnClickListener(this);
        btnDaily.setOnClickListener(this);

        queue = Volley.newRequestQueue(this);
        host = this.getResources().getString(R.string.server_host);

        // initializing shared preferences keys.
        SHARED_PREFS = this.getResources().getString(R.string.shared_key);
        EMAIL_KEY = this.getResources().getString(R.string.logged_email_key);
        PASSWORD_KEY = this.getResources().getString(R.string.logged_password_key);
        NOMBRE_KEY = this.getResources().getString(R.string.logged_nombre);

        // initializing our shared preferences.
        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

        loggedEmail = sharedpreferences.getString(EMAIL_KEY, null);
        loggedPassword = sharedpreferences.getString(PASSWORD_KEY, null);

        GlobalInfo.setIniEnableNotifyConnHeadset(this);

        if (loggedPassword == null || loggedEmail == null) {
            Log.i("A Login", "onCreate: Deberia de irme a login");
            Intent intent = new Intent(this, Login.class);
            /*intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);*/
            startActivity(intent);
            finish();
            return;
        }

        nameBitacora = loggedEmail + nameBitacora;
        fileBitacora = new File(getFilesDir(), nameBitacora);

        if (!fileBitacora.exists()) {
            try {
                fileBitacora.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "onCreate: Archivo creado");

            try {
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput(nameBitacora, MODE_PRIVATE));
                outputStreamWriter.write("18/04/2022,Me encontre un billete de 500" + "\n");
                outputStreamWriter.write("19/04/2022,Encontre una nueva canci??n" + "\n");
                outputStreamWriter.write("20/04/2022,Acabe la aplicaci??n" + "\n");
                outputStreamWriter.write("21/04/2022,Me aumentar??n el sueldo" + "\n");
                outputStreamWriter.write("22/04/2022,Excente el examen de Distribuidos" + "\n");
                outputStreamWriter.write("23/04/2022,Mi crush me habl??" + "\n");
                outputStreamWriter.write("24/04/2022,Aprobe mi examen" + "\n");
                outputStreamWriter.write("25/04/2022,Agradezco a la vida" + "\n");
                outputStreamWriter.write("26/04/2022,Dormi mas de 2 horas" + "\n");
                outputStreamWriter.write("26/04/2022,Si me levante para ir a la escuela" + "\n");
                outputStreamWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//        else {
//            fileBitacora.delete();
//            try {
//                fileBitacora.createNewFile();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }

        updateGratitudes();
        onScreenGratitudes();
    }

    @Override
    public void onClick(View v) {
        if (btnHome == v) {
            Intent intent = new Intent(this, Index.class);
            startActivity(intent);
        } else if (btnGraph == v) {
            Intent intent = new Intent(this, HistoryDetection.class);
            startActivity(intent);
        } else if (btnNotification == v) {
            Intent intent = new Intent(this, SettingHeadset.class);
            startActivity(intent);
        } else if (btnUser == v) {
            Intent intent = new Intent(this, User.class);
            startActivity(intent);
        } else if (btnDaily == v) {
            Intent intent = new Intent(this, Daily.class);
            startActivity(intent);
        } else if (v == dateInput) {
            openCalendarDialog();
        } else if (v == dialogBtnAgregar) {
            String aux = dialogNuevoAgradecimiento.getText().toString().replace("\n", "\\n");
            Log.d(TAG, "onClick: " + aux);
            OutputStreamWriter outputStreamWriter = null;
            try {
                outputStreamWriter = new OutputStreamWriter(openFileOutput(nameBitacora, MODE_PRIVATE));
                outputStreamWriter.write(getBeforeGratitudes());
                outputStreamWriter.write(getTodayDate() + "," + aux + "\n");
                outputStreamWriter.flush();
                outputStreamWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            updateGratitudes();
            onScreenGratitudes();
            dialog.dismiss();
        } else if (v == btnAgregar) {
            internalNotification();
        } else if(v == dialogBtnModificar) {
            String textoArchivo = getBeforeGratitudes().replace(viejoAgradecimiento, dialogCambiarAgradecimiento.getText().toString());
            OutputStreamWriter outputStreamWriter = null;
            try {
                outputStreamWriter = new OutputStreamWriter(openFileOutput(nameBitacora, MODE_PRIVATE));
                outputStreamWriter.write(textoArchivo);
                outputStreamWriter.flush();
                outputStreamWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            updateGratitudes();
            onScreenGratitudes();
            dialog.dismiss();
        }
    }

    private String getBeforeGratitudes() {
        StringBuilder resul = new StringBuilder();
        for (String[] auxArray : agradecimientos) {
            resul.append(auxArray[0]).append(",").append(auxArray[1].replace("\n", "\\n")).append("\n");
        }
        return resul.toString();
    }

    private void updateGratitudes() {
        agradecimientos.clear();
        try {
            InputStream inputStream = openFileInput(nameBitacora);

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";

                while ((receiveString = bufferedReader.readLine()) != null) {
                    Log.d(TAG, "updateGratitudes: " + receiveString);
                    String[] auxArray = new String[2];
                    int firstIndex = receiveString.indexOf(",");
                    auxArray[0] = receiveString.substring(0, firstIndex);
                    auxArray[1] = receiveString.substring(firstIndex + 1).replace("\\n", "\n");
                    agradecimientos.add(auxArray);
                }
                inputStream.close();
            }
        } catch (FileNotFoundException e) {
            Log.e(TAG, "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e(TAG, "Can not read file: " + e.toString());
        }
    }

    private void onScreenGratitudes() {
        LinearLayout scrollView = findViewById(R.id.vertical_scroll);
        scrollView.removeAllViews();

        LayoutInflater layoutInflater = getLayoutInflater();
        LinearLayout view;

        // Esta hermosa seccion se le agradece a este link
        // https://www.codegrepper.com/code-examples/java/how+to+create+dynamic+view+in+android
        for (int i = agradecimientos.size() - 1; i >= 0; i--) {
            if (i < agradecimientos.size() - 10)
                break;

            String[] auxArray = agradecimientos.get(i);

            if (dateSearch != null && !dateSearch.equals(auxArray[0]))
                continue;

            view = (LinearLayout) layoutInflater.inflate(R.layout.component_daily, scrollView, false);

            TextView dateText = (TextView) view.findViewById(R.id.date_daily);
            dateText.setText(auxArray[0]);

            Context auxContext = this;

            ImageView imgChange = (ImageView) view.findViewById(R.id.ic_change);
            imgChange.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viejoAgradecimiento = auxArray[1];
                    dialog.setContentView(R.layout.alert_dialog_change_gratitud);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialogCambiarAgradecimiento = (EditText) dialog.findViewById(R.id.gratitud_input);
                    dialogCambiarAgradecimiento.setText(auxArray[1]);
                    dialogBtnModificar = dialog.findViewById(R.id.btn_cambiar);
                    dialogBtnModificar.setOnClickListener((View.OnClickListener) auxContext);
                    dialog.show();
                }
            });

            TextView gratitudeText = (TextView) view.findViewById(R.id.gratitude_daily);
            gratitudeText.setText(auxArray[1]);

            scrollView.addView(view);
        }
    }

    private void internalNotification() {
        dialog.setContentView(R.layout.alert_dialog_new_gratitud);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogBtnAgregar = dialog.findViewById(R.id.btn_continuar);
        dialogBtnAgregar.setOnClickListener(this);
        dialogNuevoAgradecimiento = dialog.findViewById(R.id.gratitud_input);
        dialog.show();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (v == dateInput && hasFocus) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            openCalendarDialog();
        }
    }

    public void openCalendarDialog() {
        final Calendar c = Calendar.getInstance();
        int dia = c.get(Calendar.DAY_OF_MONTH);
        int mes = c.get(Calendar.MONTH);
        int anno = c.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                String Day = formatoNumeroDosCifras(day);
                String Month = formatoNumeroDosCifras(month + 1);
                String fBusqueda = Day + "/" + Month + "/" + year;
                dateInput.setText(fBusqueda);
                dateSearch = fBusqueda;
                onScreenGratitudes();
            }
        }, anno, mes, dia);
        datePickerDialog.show();
    }

    private String getTodayDate() {
        final Calendar c = Calendar.getInstance();
        int dia = c.get(Calendar.DAY_OF_MONTH);
        int mes = c.get(Calendar.MONTH);
        int anno = c.get(Calendar.YEAR);

        String Day = formatoNumeroDosCifras(dia);
        String Month = formatoNumeroDosCifras(mes + 1);
        String date = Day + "/" + Month + "/" + anno;

        return date;
    }

    private String formatoNumeroDosCifras(int num) {
        return (num < 10) ? "0" + num : Integer.toString(num);
    }
}
