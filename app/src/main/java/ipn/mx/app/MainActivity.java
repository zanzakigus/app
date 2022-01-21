package ipn.mx.app;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pantalla_carga);

        PeticionAPI api = new PeticionAPI(this);
        HashMap<String, String> params = new HashMap<>();
        params.put("valor1", "value1");
        params.put("valor2", "value2");
        params.put("valor3", "value3");

        MainActivity mainActivity = new MainActivity();
        Class[] parameterTypes = new Class[1];
        parameterTypes[0] = JSONObject.class;
        Method functionToPass = null;
        try {
            functionToPass = MainActivity.class.getMethod("Respuesta", JSONObject.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        api.peticionGET("http://192.168.1.71:8000/", params, mainActivity, functionToPass);

        /*
        Class[] parameterTypes = new Class[1];
        parameterTypes[0] = String.class;
        Method functionToPass = null;
        try {
             functionToPass = MainActivity.class.getMethod("printAdios", parameterTypes[0]);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        MainActivity mainActivity = new MainActivity();
        api.Prueba(mainActivity, functionToPass, "Segunda funcion"); */

        /*Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

    public void Respuesta(JSONObject response) throws JSONException {
        if (response.has("error")) {
            Log.e("ERROR", "ERROR Prueba: " + response.getString("error"));
            return;
        }
        Log.d("A ver si sale", "Respuesta: " + response.getString("message"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}