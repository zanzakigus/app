package ipn.mx.app.strategies;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import ipn.mx.app.R;
import ipn.mx.app.global.GlobalInfo;
import ipn.mx.app.neurosky.NeuroSkyManager;

public class StrategyBreathing extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = StrategyBreathing.class.getSimpleName();
    private ConstraintLayout contentLayout;
    private TextView statusText,textInstructions;
    private View outerCircleView, innerCircleView;
    private Animation animationInhaleText, animationExhaleText,
            animationInhaleInnerCircle, animationExhaleInnerCircle;

    private Animation
            animationInitialInhaleInnerCircle, animationInitialExhaleInnerCircle,animationInitial,animationInitialText;
    private Handler handler = new Handler();

    private int holdDuration = 0;
    int i = GlobalInfo.DEFAULT_DURATION_PREPA;

    CountDownTimer countDownTimer;

    boolean banderaIniciado = false, banderaCronometro = false, doneTime=false;
    MediaPlayer mediaPlayer;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.strategy_breathing);

        contentLayout = findViewById(R.id.lt_content);
        //contentLayout.setOnTouchListener(contentTouchListener);

        statusText = findViewById(R.id.txt_status);
        textInstructions = findViewById(R.id.text_instructions);

        outerCircleView = findViewById(R.id.view_circle_outer);
        innerCircleView = findViewById(R.id.view_circle_inner);


        prepareAnimations();

        innerCircleView.setOnClickListener(this);


    }


    private void setOuterCircleBackground(int backgroundResId) {
        outerCircleView.setBackgroundResource(backgroundResId);
    }

    private void setInhaleDuration(int duration) {
        animationInhaleText.setDuration(duration);
        animationInhaleInnerCircle.setDuration(duration);
    }

    private void setExhaleDuration(int duration) {
        animationExhaleText.setDuration(duration);
        animationExhaleInnerCircle.setDuration(duration);
    }


    private void prepareAnimations() {
        int inhaleDuration = GlobalInfo.DEFAULT_DURATION;
        int exhaleDuration = GlobalInfo.DEFAULT_DURATION;



        // Initial - make small_one
        animationInitial = AnimationUtils.loadAnimation(this, R.anim.anim_inner_circle_exhale);
        animationInitial.setFillAfter(true);
        animationInitial.setAnimationListener(initialAnimationListener);
        animationInitial.setDuration(GlobalInfo.DEFAULT_DURATION);

        animationInitialText = AnimationUtils.loadAnimation(this, R.anim.anim_text_exhale);
        animationInitialText.setFillAfter(true);
        animationInitialText.setDuration(GlobalInfo.DEFAULT_DURATION);


        // Initial - make large
        animationInitialInhaleInnerCircle = AnimationUtils.loadAnimation(this, R.anim.anim_inner_initial_circle_inhale);
        animationInitialInhaleInnerCircle.setFillAfter(true);
        animationInitialInhaleInnerCircle.setAnimationListener(inhaleInitialAnimationListener);
        animationInitialInhaleInnerCircle.setDuration(2000);

        // Initial - make small
        animationInitialExhaleInnerCircle = AnimationUtils.loadAnimation(this, R.anim.anim_inner_initial_circle_exhale);
        animationInitialExhaleInnerCircle.setFillAfter(true);
        animationInitialExhaleInnerCircle.setAnimationListener(exhaleInitialAnimationListener);
        animationInitialExhaleInnerCircle.setDuration(3000);

        // Inhale - make large
        animationInhaleText = AnimationUtils.loadAnimation(this, R.anim.anim_text_inhale);
        animationInhaleText.setFillAfter(true);
        animationInhaleText.setAnimationListener(inhaleAnimationListener);

        animationInhaleInnerCircle = AnimationUtils.loadAnimation(this, R.anim.anim_inner_circle_inhale);
        animationInhaleInnerCircle.setFillAfter(true);
        animationInhaleInnerCircle.setAnimationListener(inhaleAnimationListener);

        setInhaleDuration(inhaleDuration);

        // Exhale - make small
        animationExhaleText = AnimationUtils.loadAnimation(this, R.anim.anim_text_exhale);
        animationExhaleText.setFillAfter(true);
        animationExhaleText.setAnimationListener(exhaleAnimationListener);

        animationExhaleInnerCircle = AnimationUtils.loadAnimation(this, R.anim.anim_inner_circle_exhale);
        animationExhaleInnerCircle.setFillAfter(true);
        animationExhaleInnerCircle.setAnimationListener(exhaleAnimationListener);

        setExhaleDuration(exhaleDuration);

         countDownTimer = new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                long realtime = millisUntilFinished / 1000;
                String add = "00:"+realtime;
                if(realtime<10){
                    add = "00:0"+realtime;
                }else if (realtime ==60){
                    add = "01:00";
                }
                textInstructions.setText(add);
            }

            public void onFinish() {
                textInstructions.setText("Casi termina");
                doneTime = true;
            }
        };





    }

    private Animation.AnimationListener inhaleAnimationListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
            if(!banderaCronometro){
                Log.d(TAG, "inhale animation start");
                banderaCronometro = true;
                countDownTimer.start();

            }
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            Log.d(TAG, "inhale animation end");

                if(holdDuration!=0){
                    statusText.setText(R.string.text_hold);
                }


                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        statusText.setText(R.string.text_breath_out);
                        statusText.startAnimation(animationExhaleText);
                        innerCircleView.startAnimation(animationExhaleInnerCircle);
                    }
                }, holdDuration);



        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    };

    private Animation.AnimationListener exhaleAnimationListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            Log.d(TAG, "exhale animation end");
            if(!doneTime){
                if(holdDuration!=0){
                    statusText.setText(R.string.text_hold);
                }
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        statusText.setText(R.string.text_breath_in);
                        statusText.startAnimation(animationInhaleText);
                        innerCircleView.startAnimation(animationInhaleInnerCircle);
                    }
                }, holdDuration);
            }else {
                textInstructions.setText("Bien hecho");
                mediaPlayer.stop();
            }

        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    };

    private Animation.AnimationListener initialAnimationListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            Log.d(TAG, "initialAnimation end");
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    textInstructions.setText(R.string.text_focus_on_breathing);
                    innerCircleView.startAnimation(animationInitialInhaleInnerCircle);
                }
            }, holdDuration);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    };


    private Animation.AnimationListener inhaleInitialAnimationListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            Log.d(TAG, "initialAnimation end");
            if(i>-3){
                innerCircleView.startAnimation(animationInitialExhaleInnerCircle);
                i--;
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    };

    private Animation.AnimationListener exhaleInitialAnimationListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            Log.d(TAG, "initialAnimation end");
            if(i>-3){
                innerCircleView.startAnimation(animationInitialInhaleInnerCircle);
                i--;
            }else if(i == -3){
                statusText.setText(R.string.text_breath_in);
                statusText.setTextColor(getResources().getColor(R.color.green_transp));
                statusText.startAnimation(animationInhaleText);
                innerCircleView.startAnimation(animationInhaleInnerCircle);
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    };


    @Override
    public void onClick(View v) {
        if(innerCircleView == v && !banderaIniciado){
            banderaIniciado = true;
            mediaPlayer = MediaPlayer.create(this, R.raw.breathing);
            mediaPlayer.setLooping(true); // Set looping
            mediaPlayer.setVolume(100, 100);
            mediaPlayer.start();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // set the limitations for the numeric
                    // text under the progress bar
                    int time = GlobalInfo.DEFAULT_DURATION_PREPA;

                    if (i >0) {
                        statusText.setText("" + i);
                        i--;
                        handler.postDelayed(this, 1000);
                    } else if (i==0) {
                        statusText.setText("Inicial");
                        statusText.setTextColor(getResources().getColor(R.color.color_button_green));
                        textInstructions.setText(R.string.text_relax_and_confortable);
                        innerCircleView.startAnimation(animationInitial);
                        statusText.startAnimation(animationInitialText);
                    }
                }
            }, 1000);
        }

    }
}
