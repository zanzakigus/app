package ipn.mx.app.test;

import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import ipn.mx.app.R;

public class TestDinamicObject extends AppCompatActivity implements View.OnClickListener {

    EditText edtCadena;
    ImageView imvAgregar;
    LinearLayout scrollView;
    int numCadenas = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_dinamic_object);

        edtCadena = findViewById(R.id.cadena_input);
        imvAgregar = findViewById(R.id.agregar);
        scrollView = findViewById(R.id.scrollview);

        imvAgregar.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String cadena = edtCadena.getText().toString();
        if(imvAgregar == v){
            if(cadena.length() == 0){
                Toast.makeText(this, "LLene la cadena gilipolla", Toast.LENGTH_LONG).show();
                return;
            }

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
                    Toast.makeText(view.getContext(), "Borra la tag que dice: " + cadena, Toast.LENGTH_LONG).show();
                }
            });

            LinearLayout newTag = new LinearLayout(this);
            newTag.setBackground(getResources().getDrawable(R.drawable.background_estrategy));
            newTag.setOrientation(LinearLayout.HORIZONTAL);
            newTag.setGravity(Gravity.CENTER);
            newTag.setPadding(50,15,30,15);
            newTag.addView(newInfo);
            newTag.addView(icon);

            icon.getLayoutParams().width = 70;
            icon.getLayoutParams().height = 70;
            icon.requestLayout();

            if(scrollView.getChildCount() == 0){
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

            if(!validarTamano(newTag)){
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
        }
    }

    private boolean validarTamano(LinearLayout newTag){
        LinearLayout ultimoHijo = (LinearLayout) scrollView.getChildAt(scrollView.getChildCount()  -1);
        int hijos = ultimoHijo.getChildCount();
        int maxWidth = ultimoHijo.getWidth();
        int actualWidth = 0;
        boolean result = false;
        // Son 170 de espacio que ya se tiene en la tag y por cada letra se considero 20 de espacio
        int newWidth = edtCadena.getText().toString().length() * 20 + 170;
        for (int i = 1; i < hijos; i++) {
            LinearLayout ly = (LinearLayout) ultimoHijo.getChildAt(i++);
            actualWidth += ly.getWidth();
        }
        if(actualWidth + newWidth <= maxWidth - 40)
            result = true;
        return result;
    }
}