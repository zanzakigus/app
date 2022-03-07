package ipn.mx.app.neuralfit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import ipn.mx.app.R;

public class EndNegativeSection extends AppCompatActivity implements View.OnClickListener{

    View arrow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.end_negative_section);

        arrow = findViewById(R.id.arrow);
        arrow.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v == arrow){
            Intent intent = new Intent(this, TrainPositiveVisual.class);
            startActivity(intent);
        }
    }
}
