package ipn.mx.app.neuralfit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import ipn.mx.app.Index;
import ipn.mx.app.R;

public class IncompleteTraining extends AppCompatActivity implements View.OnClickListener {

    static int training;

    ImageView firstIcon, secondIcon, thirdIcon, fourthIcon;
    Button btnContinuar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.incomplete_training);

        btnContinuar = findViewById(R.id.btn_continuar);
        btnContinuar.setOnClickListener(this);

        firstIcon = findViewById(R.id.icon_first_training);
        secondIcon = findViewById(R.id.icon_second_training);
        thirdIcon = findViewById(R.id.icon_third_training);
        fourthIcon = findViewById(R.id.icon_fourth_training);

        training = Integer.parseInt(getIntent().getStringExtra("training"));

        firstIcon.setImageDrawable((training >= 1)? getResources().getDrawable(R.drawable.ic_check_circle_green): getResources().getDrawable(R.drawable.ic_remove_circle_red));
        secondIcon.setImageDrawable((training >= 2)? getResources().getDrawable(R.drawable.ic_check_circle_green): getResources().getDrawable(R.drawable.ic_remove_circle_red));
        thirdIcon.setImageDrawable((training >= 3)? getResources().getDrawable(R.drawable.ic_check_circle_green): getResources().getDrawable(R.drawable.ic_remove_circle_red));
        fourthIcon.setImageDrawable((training >= 4)? getResources().getDrawable(R.drawable.ic_check_circle_green): getResources().getDrawable(R.drawable.ic_remove_circle_red));
    }

    @Override
    public void onClick(View v) {
        if(btnContinuar == v){
            if(training == 1){
                Intent intent = new Intent(this, TrainNegativePersonal.class);
                startActivity(intent);
                finish();
            } else if(training == 2){
                Intent intent = new Intent(this, TrainPositiveVisual.class);
                startActivity(intent);
                finish();
            } else if(training == 3){
                Intent intent = new Intent(this, TrainNegativePersonal.class);
                startActivity(intent);
                finish();
            } else {
                Intent intent = new Intent(this, Index.class);
                startActivity(intent);
                finish();
            }
        }
    }
}
