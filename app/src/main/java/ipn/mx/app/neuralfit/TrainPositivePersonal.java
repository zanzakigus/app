package ipn.mx.app.neuralfit;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import ipn.mx.app.R;

public class TrainPositivePersonal extends AppCompatActivity implements View.OnClickListener {


    final int CANTIDAD_CONSEJOS = 6;
    LinearLayout vertical_scroll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.train_positive_personal);


        vertical_scroll = findViewById(R.id.vertical_scroll);

        agregarConsejos();

    }

    @Override
    public void onClick(View view) {

    }

    private void agregarConsejos() {

        String consejos[] = new String[CANTIDAD_CONSEJOS];
        consejos[0] = getResources().getString(R.string.train_positive_personal_advice_1);
        consejos[1] = getResources().getString(R.string.train_positive_personal_advice_2);
        consejos[2] = getResources().getString(R.string.train_positive_personal_advice_3);
        consejos[3] = getResources().getString(R.string.train_positive_personal_advice_4);
        consejos[4] = getResources().getString(R.string.train_positive_personal_advice_5);
        consejos[5] = getResources().getString(R.string.train_positive_personal_advice_6);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 0, 40);

        for (String consejo : consejos) {
            ConstraintLayout newConsejo = (ConstraintLayout) getLayoutInflater().inflate(R.layout.component_question, null);
            newConsejo.setLayoutParams(layoutParams);
            newConsejo.setMinHeight(100);

            TextView textConsejo = (TextView) newConsejo.getViewById(R.id.text_question);
            textConsejo.setText(consejo);

            vertical_scroll.addView(newConsejo);
        }

    }

}
