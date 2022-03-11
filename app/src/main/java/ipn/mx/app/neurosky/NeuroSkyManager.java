package ipn.mx.app.neurosky;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import ipn.mx.app.PeticionAPI;
import ipn.mx.app.R;
import ipn.mx.app.global.GlobalInfo;
import ipn.mx.app.neurosky.library.NeuroSky;
import ipn.mx.app.neurosky.library.exception.BluetoothNotEnabledException;
import ipn.mx.app.neurosky.library.listener.ExtendedDeviceMessageListener;
import ipn.mx.app.neurosky.library.message.enums.BrainWave;
import ipn.mx.app.neurosky.library.message.enums.Signal;
import ipn.mx.app.neurosky.library.message.enums.State;
import ipn.mx.app.notification.GlobalNotificationManager;
import ipn.mx.app.notification.mock.NotificationManagerData;
import ipn.mx.app.signs.Login;

public class NeuroSkyManager {
    public static final int TIPO_NEGATIVA = 0;
    public static final int TIPO_POSITIVA = 1;
    private final static String LOG_TAG = "NeuroSky";
    private static NeuroSky neuroSky;
    private static State estadoDiadema = State.UNKNOWN;
    @SuppressLint("StaticFieldLeak")
    private static Context context;
    private static int tipo_seleccionado = -1;
    private static boolean clasificar = false;
    private static boolean estrendada = false;
    private static int trainFile = 0;
    private ArrayList<HashMap<String, String>> arrayWaves = new ArrayList<>();

    public NeuroSkyManager() {

    }

    public NeuroSkyManager(Context context) {
        neuroSky = new NeuroSky(new ExtendedDeviceMessageListener() {
            @Override
            public void onStateChange(State state) {
                handleStateChange(state);
            }

            @Override
            public void onSignalChange(Signal signal) {
                handleSignalChange(signal);
            }

            @Override
            public void onBrainWavesChange(Set<BrainWave> brainWaves) {
                handleBrainWavesChange(brainWaves);
            }
        });
        NeuroSkyManager.context = context;
    }

    public static void enviarWavesTipoPositivo() {
        tipo_seleccionado = TIPO_POSITIVA;
    }

    public static void enviarWavesTipoNegativo() {
        tipo_seleccionado = TIPO_NEGATIVA;
    }

    public static void enviarWavesIdentificar() {
        tipo_seleccionado = -1;
        clasificar = true;
    }

    public static void stopSendingWaves() {
        tipo_seleccionado = -1;
        clasificar = false;
    }

    public static void solicitarEntrenamiento() {
        tipo_seleccionado = -1;
        HashMap<String, String> params = new HashMap<String, String>();
        String Shared = context.getResources().getString(R.string.shared_key);
        SharedPreferences sharedP = context.getSharedPreferences(Shared, Context.MODE_PRIVATE);
        String emailKey = context.getResources().getString(R.string.logged_email_key);
        String passKey = context.getResources().getString(R.string.logged_password_key);
        String loggedEmail = sharedP.getString(emailKey, null);
        String loggedPassword = sharedP.getString(passKey, null);

        if (loggedPassword != null && loggedEmail != null) {
            PeticionAPI api = new PeticionAPI(context);
            params.put("correo", loggedEmail);
            params.put("password", loggedPassword);

            NeuroSkyManager neuroSkyManager = new NeuroSkyManager();
            Class[] parameterTypes = new Class[2];
            parameterTypes[0] = JSONObject.class;
            parameterTypes[1] = Context.class;
            Method functionToPass = null;

            try {
                functionToPass = NeuroSkyManager.class.getMethod("onFitResponse", JSONObject.class, Context.class);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }

            api.peticionPOST(context.getResources().getString(R.string.server_host) + "/fit_neural", params, neuroSkyManager, functionToPass);

        }

    }

    public static NeuroSky getNeuroSky() {
        return neuroSky;
    }

    public static void setNeuroSky(NeuroSky neuroSky) {
        NeuroSkyManager.neuroSky = neuroSky;
    }

    public static State getEstadoDiadema() {
        return estadoDiadema;
    }

    public static void setEstadoDiadema(State estadoDiadema) {
        NeuroSkyManager.estadoDiadema = estadoDiadema;
    }

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        NeuroSkyManager.context = context;
    }

    public static int getTipo_seleccionado() {
        return tipo_seleccionado;
    }

    public static boolean isClasificar() {
        return clasificar;
    }

    public static boolean isEstrendada() {
        return estrendada;
    }

    public static void setEstrendada(boolean estrendada) {
        NeuroSkyManager.estrendada = estrendada;
    }

    public static int getTrainFile() {
        return trainFile;
    }

    public static void setTrainFile(int trainFile) {
        NeuroSkyManager.trainFile = trainFile;
    }

    private void handleStateChange(final State state) {
        if (estadoDiadema.equals(State.UNKNOWN) || estadoDiadema.equals(State.DISCONNECTED) || estadoDiadema.equals(State.NOT_PAIRED)) {
            try {
                neuroSky.connect();
            } catch (BluetoothNotEnabledException e) {
                Toast myToast = Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG);
                myToast.show();
                Log.d(LOG_TAG, e.getMessage());
            }
        }

        Toast myToast;
        switch (state) {
            case CONNECTED:
                myToast = Toast.makeText(context, R.string.connected_diadema, Toast.LENGTH_LONG);
                myToast.show();
                Log.d(LOG_TAG, LOG_TAG + ": " + estadoDiadema.toString());
            case NOT_FOUND:
                myToast = Toast.makeText(context, R.string.no_found_diadema, Toast.LENGTH_LONG);
                myToast.show();
                Log.d(LOG_TAG, LOG_TAG + ": " + estadoDiadema.toString());
                neuroSky.connect();
                break;
            case CONNECTING:
                myToast = Toast.makeText(context, R.string.searching_diadema, Toast.LENGTH_LONG);
                myToast.show();
                Log.d(LOG_TAG, LOG_TAG + ": " + estadoDiadema.toString());
                break;
        }
        estadoDiadema = state;
        Log.d(LOG_TAG, state.toString());
    }

    private void handleSignalChange(final Signal signal) {
        /*Log.d(LOG_TAG + "-SIGNALS", String.format("%s: %d", signal.toString(), signal.getValue()));*/
    }

    private void handleBrainWavesChange(final Set<BrainWave> brainWaves) {

        // Request a string response from the provided URL.
        HashMap<String, String> params = new HashMap<String, String>();
        HashMap<String, String> waves = new HashMap<String, String>();

        for (BrainWave brainWave : brainWaves) {
            /*params.put(brainWave.toString(), String.valueOf(brainWave.getValue()));*/
            waves.put(brainWave.toString(), String.valueOf(brainWave.getValue()));
            /*Log.d(LOG_TAG + "-WAVES", String.format("brain: %s: %d", brainWave.toString(), brainWave.getValue()));*/
        }

        String Shared = context.getResources().getString(R.string.shared_key);
        SharedPreferences sharedP = context.getSharedPreferences(Shared, Context.MODE_PRIVATE);
        String emailKey = context.getResources().getString(R.string.logged_email_key);
        String passKey = context.getResources().getString(R.string.logged_password_key);
        String loggedEmail = sharedP.getString(emailKey, null);
        String loggedPassword = sharedP.getString(passKey, null);

        if (loggedPassword != null && loggedEmail != null) {
            if ((tipo_seleccionado != -1 && !estrendada) || clasificar) {
                PeticionAPI api = new PeticionAPI(context);
                params.put("correo", loggedEmail);
                params.put("password", loggedPassword);
                params.put("tipo", String.valueOf(tipo_seleccionado));
                Log.d(LOG_TAG + "-WAVES", "brain: Enviando waves.");

                NeuroSkyManager neuroSkyManager = new NeuroSkyManager();
                Class[] parameterTypes = new Class[2];
                parameterTypes[0] = JSONObject.class;
                parameterTypes[1] = Context.class;
                Method functionToPass = null;
                String methodName = "onSentWaves";
                String link = "/waves_data";
                boolean enviar = false;
                if (clasificar) {
                    methodName = "onClassifyResponse";
                    link = "/classify_neural";
                    if (GlobalInfo.getClasifyTimeDelayCounter() == GlobalInfo.getClasifyTimeDelay() / 1000) {
                        enviar = true;
                        GlobalInfo.setClasifyTimeDelayCounter(0);
                        params.put("array_waves", arrayWaves.toString());
                        arrayWaves = new ArrayList<>();
                    } else {
                        GlobalInfo.setClasifyTimeDelayCounter(GlobalInfo.getClasifyTimeDelayCounter() + 1);
                        arrayWaves.add(waves);
                    }
                } else {
                    if (GlobalInfo.getTrainSectionTime() == GlobalInfo.getTrainSectionTimeCounter()) {
                        enviar = true;
                        GlobalInfo.setTrainSectionTimeCounter(0);
                        params.put("array_waves", arrayWaves.toString());
                        arrayWaves = new ArrayList<>();
                    } else {
                        GlobalInfo.setTrainSectionTimeCounter(GlobalInfo.getTrainSectionTimeCounter() + 1);
                        arrayWaves.add(waves);
                    }
                }
                try {
                    functionToPass = NeuroSkyManager.class.getMethod(methodName, parameterTypes);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }


                if (enviar) {
                    api.peticionPOST(context.getResources().getString(R.string.server_host) + link, params, neuroSkyManager, functionToPass);
                }


            }
        } else {
            Intent intent = new Intent(context, Login.class);
            context.startActivity(intent);
            Log.i("INFO", "INFO: Usuario no Loggeado");
            ((Activity) context).finish();

            Toast myToast = Toast.makeText(context, "Usuario no Loggeado", Toast.LENGTH_LONG);
            myToast.show();
        }


    }

    public void onFitResponse(JSONObject response, Context context) throws JSONException {
        if (response.has("error")) {
            Log.e("ERROR", "ERROR onFitResponse: " + response.getString("error"));
            Toast myToast = Toast.makeText(context, "ERROR " + (500) + " onFitResponse: " + response.getString("error"), Toast.LENGTH_LONG);
            myToast.show();
            return;
        }
        int status = response.getInt("status");
        String message = response.getString("message");
        if (status != 200) {
            Log.e("ERROR", "ERROR onFitResponse: " + message);
            Toast myToast = Toast.makeText(context, "ERROR " + (status) + " onFitResponse: " + message, Toast.LENGTH_LONG);
            myToast.show();
            return;
        }

        estrendada = true;
        Toast myToast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        myToast.show();

    }

    public void onClassifyResponse(JSONObject response, Context context) throws JSONException {
        if (response.has("error")) {
            Log.e("ERROR", "ERROR onClassifyResponse: " + response.getString("error"));
            Toast myToast = Toast.makeText(context, "ERROR " + (500) + " onClassifyResponse: " + response.getString("error"), Toast.LENGTH_LONG);
            myToast.show();
            return;
        }
        int status = response.getInt("status");
        String message = response.getString("message");
        if (status != 200) {
            Log.e("ERROR", "ERROR onClassifyResponse: " + message);
            Toast myToast = Toast.makeText(context, "ERROR " + (status) + " onClassifyResponse: " + message, Toast.LENGTH_LONG);
            myToast.show();
            return;
        }
        if (message.equals("0")) {
            NotificationManagerData nmd = new NotificationManagerData(context);
            GlobalNotificationManager gnm = new GlobalNotificationManager(context, nmd);
            gnm.generateNotification();
            Toast myToast = Toast.makeText(context, message, Toast.LENGTH_LONG);
            myToast.show();
        }


    }

    public void onSentWaves(JSONObject response, Context context) throws JSONException {
        if (response.has("error")) {
            Log.e("ERROR", "ERROR onSentWaves: " + response.getString("error"));
            Toast myToast = Toast.makeText(context, "ERROR " + (500) + " onSentWaves: " + response.getString("error"), Toast.LENGTH_LONG);
            myToast.show();
            return;
        }
        int status = response.getInt("status");
        String message = response.getString("message");
        if (status != 200) {
            Log.e("ERROR", "ERROR onSentWaves: " + message);
            Toast myToast = Toast.makeText(context, "ERROR " + (status) + " onSentWaves: " + message, Toast.LENGTH_LONG);
            myToast.show();
            return;
        }
        Log.d(LOG_TAG + "-WAVES SENT", "brain: Enviada con exito.");

    }
}
