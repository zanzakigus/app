package ipn.mx.app;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
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
import java.util.Map;

public class PeticionAPI implements Response.ErrorListener, Response.Listener<JSONObject> {

    Context context;
    RequestQueue requestQueue;
    JSONObject reqJsonObj = new JSONObject();
    JSONObject resJsonObj;

    private Object classPetition;
    private Method functionToPass;

    public PeticionAPI(Context context) {
        this.context = context;
        requestQueue = Volley.newRequestQueue(context);
    }

    public void peticionGET(String URL, HashMap<String, String> params, Object object, Method method) {

        StringBuilder URLBuilder = new StringBuilder(URL);
        URLBuilder.append("?");
        for (String key : params.keySet()) {
            URLBuilder.append(key).append("=").append(params.get(key)).append("&");
        }
        // Se elimina el ultimo Amperson o en su defecto el signo de interrogacion en caso de que no haya parametros
        URLBuilder.deleteCharAt(URLBuilder.length() - 1);
        URL = URLBuilder.toString();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, new JSONObject(), this, this);

        // En caso de que la peticion se tarde mucho el Volley vuelve a mandar otra petición haciendo que al servidor le lleguen dos peticiones o mas
        // Con esto aumnetamos el tiempo de espera y hacemos que como mucho solo haga una petición. ASi evitamos duplicidades en el servidor.
        request.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        requestQueue.add(request);
        classPetition = object;
        functionToPass = method;
    }

    public void peticionPOST(String URL, HashMap<String, String> params, Object object, Method method) {

        reqJsonObj = new JSONObject();
        for (String key : params.keySet()) {
            try {
                reqJsonObj.put(key, params.get(key));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL, reqJsonObj, this, this);

        // En caso de que la peticion se tarde mucho el Volley vuelve a mandar otra petición haciendo que al servidor le lleguen dos peticiones o mas
        // Con esto aumnetamos el tiempo de espera y hacemos que como mucho solo haga una petición. ASi evitamos duplicidades en el servidor.
        request.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        requestQueue.add(request);
        classPetition = object;
        functionToPass = method;
    }

    public void peticionPUT(String URL, HashMap<String, String> params, Object object, Method method) {

        reqJsonObj = new JSONObject();
        for (String key : params.keySet()) {
            try {
                reqJsonObj.put(key, params.get(key));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, URL, reqJsonObj, this, this) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("Accept", "application/json");
                return params;
            }
        };

        // En caso de que la peticion se tarde mucho el Volley vuelve a mandar otra petición haciendo que al servidor le lleguen dos peticiones o mas
        // Con esto aumnetamos el tiempo de espera y hacemos que como mucho solo haga una petición. ASi evitamos duplicidades en el servidor.
        request.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        requestQueue.add(request);
        classPetition = object;
        functionToPass = method;
    }

    public void peticionPATCH(String URL, HashMap<String, String> params, Object object, Method method) {

        reqJsonObj = new JSONObject();
        for (String key : params.keySet()) {
            try {
                reqJsonObj.put(key, params.get(key));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PATCH, URL, reqJsonObj, this, this) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("Accept", "application/json");
                return params;
            }
        };

        // En caso de que la peticion se tarde mucho el Volley vuelve a mandar otra petición haciendo que al servidor le lleguen dos peticiones o mas
        // Con esto aumnetamos el tiempo de espera y hacemos que como mucho solo haga una petición. ASi evitamos duplicidades en el servidor.
        request.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        requestQueue.add(request);
        classPetition = object;
        functionToPass = method;
    }

    @Override
    public void onResponse(JSONObject response) {
        resJsonObj = new JSONObject();
        resJsonObj = response;
        Object[] parameters = new Object[2];
        parameters[0] = response;
        parameters[1] = context;

        try {
            functionToPass.invoke(classPetition, parameters);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        resJsonObj = new JSONObject();
        try {
            resJsonObj.put("error", error.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Object[] parameters = new Object[2];
        parameters[0] = resJsonObj;
        parameters[1] = context;

        try {
            functionToPass.invoke(classPetition, parameters);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
