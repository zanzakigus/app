package ipn.mx.app;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import ipn.mx.app.global.GlobalInfo;
import ipn.mx.app.usuario.Usuario;

public class PeticionAPI implements Response.ErrorListener, Response.Listener<JSONObject> {

    Context context;
    RequestQueue requestQueue;
    JSONObject reqJsonObj = new JSONObject();
    JSONObject resJsonObj;
    final int TIMEOUT = new GlobalInfo().TIMEOUT_REQUEST;
    final int INTENTS = new GlobalInfo().MAX_INTENTS;

    PeticionAPI(Context context) {
        this.context = context;
        requestQueue = Volley.newRequestQueue(context);
    }

    public JSONObject peticionGET(String URL, HashMap<String, String> params) throws InterruptedException, JSONException {

        resJsonObj = new JSONObject();
        for (String key : params.keySet())
            reqJsonObj.put(key, params.get(key));

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, reqJsonObj, this, this);
        requestQueue.add(request);

        int intents = 0;
        while (intents < INTENTS && resJsonObj.length() == 0) {
            Log.d("Esperando respuesta", "Esperando respuesta");
            Thread.sleep(TIMEOUT);
            intents++;
        }
        if (intents == INTENTS) {
            resJsonObj.put("error", "TIMEOUT GONE");
        }
        return resJsonObj;
    }

    public void Prueba(Object object, Method method, String name) {
        Object[] parameters = new Object[1];
        parameters[0] = name;
        try {
            method.invoke(object, parameters);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResponse(JSONObject response) {
        resJsonObj = new JSONObject();
        resJsonObj = response;
        Log.d("OK", "Me respondio");
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        resJsonObj = new JSONObject();
        try {
            resJsonObj.put("error", error.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
