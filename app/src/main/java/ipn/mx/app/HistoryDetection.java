package ipn.mx.app;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import ipn.mx.app.chartcustom.MarkerViewPointer;
import ipn.mx.app.global.GlobalInfo;

public class HistoryDetection extends AppCompatActivity implements View.OnClickListener {

    //logged variables
    public static String SHARED_PREFS;
    public static String EMAIL_KEY;
    public static String PASSWORD_KEY;
    public static String NOMBRE_KEY;
    private final String TAG = "HistoryDetection";
    Button btnHome, btnGraph, btnNotification, btnUser;
    LinearLayout vertical_scroll;
    Switch aSwitch;
    EditText fecha_ini, fecha_fin;
    TextView pieTitle, lineTitle, noInfo;
    Context context;
    CheckBox cbPositive, cbNegative;


    // variable for shared preferences.
    SharedPreferences sharedpreferences;
    String fInicial, fFinal;
    ArrayList<ConstraintLayout> emocionesViews = new ArrayList<>();
    JSONArray emocionesJSONArray = new JSONArray();
    private LineChart lineChart;
    private PieChart pieChart;
    private String loggedEmail;
    private String loggedPassword;
    private String loggedNombre;
    private int anno_ini, mes_ini, dia_ini, anno_fin, mes_fin, dia_fin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_detections);

        context = this;

        btnHome = findViewById(R.id.icon_home);
        btnGraph = findViewById(R.id.icon_graph);
        btnNotification = findViewById(R.id.icon_notifications);
        btnUser = findViewById(R.id.icon_user);
        vertical_scroll = findViewById(R.id.vertical_scroll);
        aSwitch = findViewById(R.id.swt_history);
        fecha_ini = findViewById(R.id.fecha_ini_input);
        fecha_fin = findViewById(R.id.fecha_fin_input);
        lineTitle = findViewById(R.id.title_line_chart);
        pieTitle = findViewById(R.id.title_pie_chart);
        lineChart = findViewById(R.id.chart1);
        pieChart = findViewById(R.id.chart2);
        noInfo = findViewById(R.id.no_info);
        cbNegative = findViewById(R.id.checkbox_negative);
        cbPositive = findViewById(R.id.checkbox_positive);

        cbNegative.setChecked(true);
        cbPositive.setChecked(true);


        fecha_ini.setKeyListener(null);
        fecha_fin.setKeyListener(null);


        fecha_ini.setOnClickListener(this);
        fecha_fin.setOnClickListener(this);
        btnHome.setOnClickListener(this);
        btnGraph.setOnClickListener(this);
        btnNotification.setOnClickListener(this);
        btnUser.setOnClickListener(this);
        aSwitch.setOnClickListener(this);
        cbPositive.setOnClickListener(this);
        cbNegative.setOnClickListener(this);


        final Calendar c = Calendar.getInstance();
        dia_fin = c.get(Calendar.DAY_OF_MONTH);
        mes_fin = c.get(Calendar.MONTH);
        anno_fin = c.get(Calendar.YEAR);

        c.add(Calendar.DAY_OF_YEAR, -7);
        dia_ini = c.get(Calendar.DAY_OF_MONTH);
        mes_ini = c.get(Calendar.MONTH);
        anno_ini = c.get(Calendar.YEAR);


        String Day = formatoNumeroDosCifras(dia_ini);
        String Month = formatoNumeroDosCifras(mes_ini + 1);
        fInicial = Day + "/" + Month + "/" + anno_ini;
        fecha_ini.setText(fInicial);


        Day = formatoNumeroDosCifras(dia_fin);
        Month = formatoNumeroDosCifras(mes_fin + 1);
        fFinal = Day + "/" + Month + "/" + anno_fin;
        fecha_fin.setText(fFinal);


        // initializing shared preferences keys.
        SHARED_PREFS = this.getResources().getString(R.string.shared_key);
        EMAIL_KEY = this.getResources().getString(R.string.logged_email_key);
        PASSWORD_KEY = this.getResources().getString(R.string.logged_password_key);
        NOMBRE_KEY = this.getResources().getString(R.string.logged_nombre);

        // initializing our shared preferences.
        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

        loggedEmail = sharedpreferences.getString(EMAIL_KEY, null);
        loggedPassword = sharedpreferences.getString(PASSWORD_KEY, null);

        getHistory();


    }


    @Override
    public void onClick(View v) {

        // Esto no afecta nada en el codigo, lo pongo aqui para copiarlo y pegarlo en las demas Clases
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
        } else if (fecha_ini == v) {
            Log.d(TAG, "onClick()-fecha_ini: ");

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                    Calendar ini = Calendar.getInstance();
                    ini.set(Calendar.DAY_OF_MONTH, day);
                    ini.set(Calendar.MONTH, month);  // 0-11 so 1 less
                    ini.set(Calendar.YEAR, year);

                    Calendar fin = Calendar.getInstance();
                    fin.set(Calendar.DAY_OF_MONTH, dia_fin);
                    fin.set(Calendar.MONTH, mes_fin);
                    fin.set(Calendar.YEAR, anno_fin);
                    long diff = fin.getTimeInMillis() - ini.getTimeInMillis();
                    Log.d(TAG, "onClick()-fecha_ini: " + diff);
                    if (diff < 0) {
                        Toast myToast = Toast.makeText(context, R.string.text_date_no_mayor_ini, Toast.LENGTH_LONG);
                        myToast.show();
                    } else {
                        String Day = formatoNumeroDosCifras(day);
                        String Month = formatoNumeroDosCifras(month + 1);
                        fInicial = Day + "/" + Month + "/" + year;
                        fecha_ini.setText(fInicial);
                        anno_ini = year;
                        mes_ini = month;
                        dia_ini = day;
                        getHistory();

                    }


                }
            }, anno_ini, mes_ini, dia_ini);
            datePickerDialog.show();
        } else if (fecha_fin == v) {
            Log.d(TAG, "onClick()-fecha_fin: ");
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                    Calendar ini = Calendar.getInstance();
                    ini.set(Calendar.DAY_OF_MONTH, dia_ini);
                    ini.set(Calendar.MONTH, mes_ini);  // 0-11 so 1 less
                    ini.set(Calendar.YEAR, anno_ini);

                    Calendar fin = Calendar.getInstance();
                    fin.set(Calendar.DAY_OF_MONTH, day);
                    fin.set(Calendar.MONTH, month);
                    fin.set(Calendar.YEAR, year);
                    long diff = fin.getTimeInMillis() - ini.getTimeInMillis();
                    if (diff < 0) {
                        Toast myToast = Toast.makeText(context, R.string.text_date_no_mayor_fin, Toast.LENGTH_LONG);
                        myToast.show();
                    } else {
                        String Day = formatoNumeroDosCifras(day);
                        String Month = formatoNumeroDosCifras(month + 1);
                        fFinal = Day + "/" + Month + "/" + year;
                        fecha_fin.setText(fFinal);
                        anno_fin = year;
                        mes_fin = month;
                        dia_fin = day;
                        getHistory();
                    }


                }
            }, anno_fin, mes_fin, dia_fin);
            datePickerDialog.show();
        } else if (aSwitch == v) {
            aSwitch.setText(aSwitch.isChecked() ? "Graficas" : "Listado");
            cbPositive.setVisibility(aSwitch.isChecked() ? View.GONE : View.VISIBLE);
            cbNegative.setVisibility(aSwitch.isChecked() ? View.GONE : View.VISIBLE);
            getHistory();
        }else if (cbPositive == v || cbNegative == v){
            boolean checked = ((CheckBox) v).isChecked();
            CheckBox checkBox = (CheckBox) v;
            if(!cbNegative.isChecked() && !cbPositive.isChecked()){
                Toast myToast = Toast.makeText(context, R.string.text_non_selected, Toast.LENGTH_LONG);
                myToast.show();
                checkBox.setChecked(true);
            }else{
                getHistory();
            }
        }
    }


    private String formatoNumeroDosCifras(int num) {
        return (num < 10) ? "0" + num : Integer.toString(num);
    }

    private void getHistory() {

        Log.d(TAG, "getHistory() ");
        PeticionAPI api = new PeticionAPI(context);
        HashMap<String, String> params = new HashMap<>();
        params.put("correo", loggedEmail);
        params.put("password", loggedPassword);
        params.put("fecha_ini", fInicial);
        params.put("fecha_fin", fFinal);
        int tipo = GlobalInfo.TIPO_ALL;
        if(!aSwitch.isChecked()){
            if( cbNegative.isChecked() && !cbPositive.isChecked()){
                tipo = GlobalInfo.TIPO_NEGATIVO;

            }else if( !cbNegative.isChecked() && cbPositive.isChecked()){
                tipo = GlobalInfo.TIPO_POSITIVO;

            }else if( cbNegative.isChecked() && cbPositive.isChecked()){
                tipo = GlobalInfo.TIPO_ALL;
            }
        }



        params.put("tipo", String.valueOf(tipo));

        HistoryDetection historyDetection = new HistoryDetection();
        historyDetection.vertical_scroll = vertical_scroll;
        historyDetection.pieTitle = pieTitle;
        historyDetection.lineTitle = lineTitle;
        historyDetection.lineChart = lineChart;
        historyDetection.pieChart = pieChart;
        historyDetection.emocionesViews = emocionesViews;
        historyDetection.dia_ini = dia_ini;
        historyDetection.mes_ini = mes_ini;
        historyDetection.anno_ini = anno_ini;
        historyDetection.dia_fin = dia_fin;
        historyDetection.mes_fin = mes_fin;
        historyDetection.anno_fin = anno_fin;
        historyDetection.context = context;
        historyDetection.noInfo = noInfo;

        Class[] parameterTypes = new Class[2];
        parameterTypes[0] = JSONObject.class;
        parameterTypes[1] = Context.class;
        Method functionToPass = null;
        String functionName = aSwitch.isChecked() ? "grafica" : "listado";
        try {
            functionToPass = HistoryDetection.class.getMethod(functionName, parameterTypes);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        api.peticionGET(this.getResources().getString(R.string.server_host) + "/emocion_detectada", params, historyDetection, functionToPass);
    }

    public void listado(JSONObject response, Context context) throws JSONException {
        Log.d(TAG, "lstado() ");
        if (response.has("error")) {
            Log.e(TAG, "ERROR listado: " + response.getString("error"));
            Toast myToast = Toast.makeText(context, "ERROR " + (500) + " listado: " + response.getString("error"), Toast.LENGTH_LONG);
            myToast.show();
            return;
        }
        int status = response.getInt("status");

        if (status != 200) {
            String message = response.getString("message");
            Log.e(TAG, "ERROR listado: " + message);
            Toast myToast = Toast.makeText(context, "ERROR " + (status) + " listado: " + message, Toast.LENGTH_LONG);
            myToast.show();
            return;
        }
        lineChart.setVisibility(View.GONE);
        pieChart.setVisibility(View.GONE);
        lineTitle.setVisibility(View.GONE);
        pieTitle.setVisibility(View.GONE);
        for (ConstraintLayout emocion : emocionesViews) {
            vertical_scroll.removeView(emocion);
        }
        emocionesViews.clear();

        JSONArray emociones = response.getJSONArray("message");
        if (emociones.length() == 0) {
            noInfo.setVisibility(View.VISIBLE);
        } else {
            noInfo.setVisibility(View.GONE);
            Log.d(TAG, "lstado() " + emociones.length());
            for (int i = 0; i < emociones.length(); i++) {
                ConstraintLayout emocion = (ConstraintLayout) LayoutInflater.from(context).inflate(R.layout.component_detection, null);
                emocionesViews.add(emocion);
                ConstraintLayout.LayoutParams newLayoutParams = new ConstraintLayout.LayoutParams(
                        ConstraintLayout.LayoutParams.WRAP_CONTENT,
                        ConstraintLayout.LayoutParams.WRAP_CONTENT);
                newLayoutParams.topMargin = 20;
                emocion.setLayoutParams(newLayoutParams);

                TextView fecha = emocion.findViewById(R.id.text_date);
                View icon = emocion.findViewById(R.id.icon_burn);
                if(emociones.getJSONObject(i).getInt("id_emocion")==1){
                    icon.setBackgroundResource(R.drawable.icon_snowflake);
                }
                fecha.setText(emociones.getJSONObject(i).getString("fecha_deteccion"));
                vertical_scroll.addView(emocion);
            }
        }


    }

    public void grafica(JSONObject response, Context context) throws JSONException {
        Log.d(TAG, "grafica() ");
        if (response.has("error")) {
            Log.e(TAG, "ERROR grafica: " + response.getString("error"));
            Toast myToast = Toast.makeText(context, "ERROR " + (500) + " grafica: " + response.getString("error"), Toast.LENGTH_LONG);
            myToast.show();
            return;
        }
        int status = response.getInt("status");

        if (status != 200) {
            String message = response.getString("message");
            Log.e(TAG, "ERROR grafica: " + message);
            Toast myToast = Toast.makeText(context, "ERROR " + (status) + " grafica: " + message, Toast.LENGTH_LONG);
            myToast.show();
            return;
        }
        for (ConstraintLayout emocion : emocionesViews) {
            vertical_scroll.removeView(emocion);
        }
        emocionesViews.clear();
        emocionesJSONArray = response.getJSONArray("message");

        if (emocionesJSONArray.length() == 0) {
            noInfo.setVisibility(View.VISIBLE);
        } else {
            noInfo.setVisibility(View.GONE);
            generateDataLine();
            generateDataPie();
        }


    }

    private void generateDataPie() {
        Log.d(TAG, "generateDataPie(): ");

        Calendar ini = Calendar.getInstance();
        ini.set(Calendar.DAY_OF_MONTH, dia_ini);
        ini.set(Calendar.MONTH, mes_ini);  // 0-11 so 1 less
        ini.set(Calendar.YEAR, anno_ini);

        Calendar fin = Calendar.getInstance();
        fin.set(Calendar.DAY_OF_MONTH, dia_fin);
        fin.set(Calendar.MONTH, mes_fin);
        fin.set(Calendar.YEAR, anno_fin);
        long diff = fin.getTimeInMillis() - ini.getTimeInMillis();
        long dias = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);

        int tipo;

        if (90 < dias) {
            tipo = 0; //por mes

        } else if (2 < dias) {
            tipo = 2; // por dias
        } else {
            tipo = 3; // por horas
        }
        int count = 0;
        String fechaAnter = null;
        String day = null;
        String month = null;
        String year;
        String hour = null;
        Calendar calendar = Calendar.getInstance();
        Log.d(TAG, "generateDataPie() lenght: " + emocionesJSONArray.length());

        ArrayList<PieEntry> entries = new ArrayList<>();
        for (int i = 0; i < emocionesJSONArray.length(); i++) {
            String fecha = null;
            try {
                fecha = emocionesJSONArray.getJSONObject(i).getString("fecha_deteccion");
            } catch (Exception e) {
                Log.e(TAG, "generateDataPie() ");
                e.printStackTrace();
            }
            if (i == 0) {
                fechaAnter = fecha;
                day = fechaAnter.substring(0, 2);
                month = fechaAnter.substring(3, 5);
                year = fechaAnter.substring(6, 10);
                hour = fechaAnter.substring(11, 13);
                //01/05/2020 10:15
                calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day));
                calendar.set(Calendar.MONTH, Integer.parseInt(month) - 1);  // 0-11 so 1 less
                calendar.set(Calendar.YEAR, Integer.parseInt(year));
                calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
            }
            String dataAnt;
            int iIni = 0, iFin, finFormat, iniFormat = 0;
            if (tipo == 3) {
                iIni = 11;
                iFin = 13;
                finFormat = 13;
                dataAnt = hour;

            } else if (tipo == 2) {
                iFin = 2;
                dataAnt = day;
                finFormat = 10;

            } else {
                iIni = 3;
                iFin = 5;
                iniFormat = 3;
                finFormat = 10;
                dataAnt = month;

            }
            String data = fecha.substring(iIni, iFin);
            if (!data.equals(dataAnt)) {
                entries.add(new PieEntry(count, fechaAnter.substring(iniFormat, finFormat)));
                count = 0;
                if (emocionesJSONArray.length() == i + 1) {
                    entries.add(new PieEntry(count + 1, fecha.substring(iniFormat, finFormat)));
                }
            } else if (emocionesJSONArray.length() == i + 1) {
                entries.add(new PieEntry(count + 1, fechaAnter.substring(iniFormat, finFormat)));
            }

            fechaAnter = fecha;
            day = fecha.substring(0, 2);
            month = fecha.substring(3, 5);
            year = fecha.substring(6, 10);
            hour = fecha.substring(11, 13);
            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day));
            calendar.set(Calendar.MONTH, Integer.parseInt(month) - 1);  // 0-11 so 1 less
            calendar.set(Calendar.YEAR, Integer.parseInt(year));
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
            count++;
        }


        PieDataSet d = new PieDataSet(entries, "");

        // space between slices
        d.setSliceSpace(2f);
        d.setColors(ColorTemplate.COLORFUL_COLORS);
        d.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        d.setValueTextSize(11f);
        d.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);


        pieChart.setData(new PieData(d));
        pieChart.animateX(1000);
        pieChart.getDescription().setEnabled(false);
        pieChart.setRotationEnabled(true);
        pieChart.setHighlightPerTapEnabled(true);

        pieTitle.setVisibility(View.VISIBLE);
        pieChart.setVisibility(View.VISIBLE);
        Legend l = pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);


    }


    private void generateDataLine() {

        Log.d(TAG, "generateDataLine(): ");
        ArrayList<Entry> values1 = new ArrayList<>();

        Calendar ini = Calendar.getInstance();
        ini.set(Calendar.DAY_OF_MONTH, dia_ini);
        ini.set(Calendar.MONTH, mes_ini);  // 0-11 so 1 less
        ini.set(Calendar.YEAR, anno_ini);

        Calendar fin = Calendar.getInstance();
        fin.set(Calendar.DAY_OF_MONTH, dia_fin);
        fin.set(Calendar.MONTH, mes_fin);
        fin.set(Calendar.YEAR, anno_fin);
        long diff = fin.getTimeInMillis() - ini.getTimeInMillis();
        long dias = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);

        int tipo;
        String format = "dd MMM";
        if (90 < dias) {
            tipo = 0; //por mes
            format = "MMM";
        } else if (2 < dias) {
            tipo = 2; // por dias
        } else {
            format = "dd MMM HH:mm";
            tipo = 3; // por horas
        }
        int count = 0;
        String fechaAnter;
        String day = null;
        String month = null;
        String year;
        String hour = null;
        Calendar calendar = Calendar.getInstance();
        Log.d(TAG, "generateDataLine() lenght: " + emocionesJSONArray.length());
        for (int i = 0; i < emocionesJSONArray.length(); i++) {
            String fecha = null;
            try {
                fecha = emocionesJSONArray.getJSONObject(i).getString("fecha_deteccion");
            } catch (Exception e) {
                Log.e(TAG, "generateDataLine() ");
                e.printStackTrace();
            }
            if (i == 0) {
                fechaAnter = fecha;
                day = fechaAnter.substring(0, 2);
                month = fechaAnter.substring(3, 5);
                year = fechaAnter.substring(6, 10);
                hour = fechaAnter.substring(11, 13);
                //01/05/2020 10:15
                calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day));
                calendar.set(Calendar.MONTH, Integer.parseInt(month) - 1);  // 0-11 so 1 less
                calendar.set(Calendar.YEAR, Integer.parseInt(year));
                calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
            }
            String dataAnt;
            int iIni = 0, iFin;
            if (tipo == 3) {
                iIni = 11;
                iFin = 13;
                dataAnt = hour;

            } else if (tipo == 2) {
                iFin = 2;
                dataAnt = day;

            } else {
                iIni = 3;
                iFin = 5;
                dataAnt = month;

            }

            String data = fecha.substring(iIni, iFin);
            long now;
            if (!data.equals(dataAnt)) {
                now = TimeUnit.MILLISECONDS.toHours(calendar.getTimeInMillis());
                values1.add(new Entry(now, count));
                count = 0;
                if (emocionesJSONArray.length() == i + 1) {
                    day = fecha.substring(0, 2);
                    month = fecha.substring(3, 5);
                    year = fecha.substring(6, 10);
                    hour = fecha.substring(11, 13);
                    calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day));
                    calendar.set(Calendar.MONTH, Integer.parseInt(month) - 1);  // 0-11 so 1 less
                    calendar.set(Calendar.YEAR, Integer.parseInt(year));
                    calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
                    now = TimeUnit.MILLISECONDS.toHours(calendar.getTimeInMillis());
                    values1.add(new Entry(now, 1));
                }
            } else if (emocionesJSONArray.length() == i + 1) {
                now = TimeUnit.MILLISECONDS.toHours(calendar.getTimeInMillis());
                values1.add(new Entry(now, count + 1));
            }


            day = fecha.substring(0, 2);
            month = fecha.substring(3, 5);
            year = fecha.substring(6, 10);
            hour = fecha.substring(11, 13);
            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day));
            calendar.set(Calendar.MONTH, Integer.parseInt(month) - 1);  // 0-11 so 1 less
            calendar.set(Calendar.YEAR, Integer.parseInt(year));
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
            count++;
        }

        LineDataSet d1 = new LineDataSet(values1, "Emociones negativas");
        d1.setLineWidth(2.5f);
        d1.setCircleRadius(4.5f);
        d1.setHighLightColor(Color.rgb(244, 117, 117));
        d1.setDrawValues(false);
        d1.setColors(Color.rgb(196, 0, 0));
        d1.setCircleColor(Color.rgb(196, 0, 0));


        ArrayList<ILineDataSet> sets = new ArrayList<>();
        sets.add(d1);


        LineData lineData = new LineData(sets);
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f); // one hour
        String finalFormat = format;
        xAxis.setValueFormatter(new IAxisValueFormatter() {

            private final SimpleDateFormat mFormat = new SimpleDateFormat(finalFormat, Locale.ENGLISH);

            @Override
            public String getFormattedValue(float value, AxisBase axis) {

                long millis = TimeUnit.HOURS.toMillis((long) value);
                return mFormat.format(new Date(millis));
            }
        });
        lineChart.setVisibility(View.VISIBLE);
        lineTitle.setVisibility(View.VISIBLE);

        lineChart.setData(lineData);
        // create marker to display box when values are selected
        MarkerViewPointer mv = new MarkerViewPointer(context, R.layout.custom_marker_view);
        // Set the marker to the chart
        mv.setChartView(lineChart);
        lineChart.setMarker(mv);
        lineChart.animateX(1000);
        lineChart.getDescription().setEnabled(false);

    }
}
