package ipn.mx.app.strategies;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.Locale;

import ipn.mx.app.HistoryDetection;
import ipn.mx.app.Index;
import ipn.mx.app.R;
import ipn.mx.app.SettingHeadset;
import ipn.mx.app.User;
import ipn.mx.app.global.GlobalInfo;
import ipn.mx.app.neurosky.NeuroSkyManager;

public class StrategyBreathing extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = StrategyBreathing.class.getSimpleName();
    private ConstraintLayout contentLayout;
    private TextView statusText, textInstructions;
    private View outerCircleView, innerCircleView;
    private Button btnHome,btnGraph,btnNotification,btnUser;

    TextToSpeech speaker;

    private Animation animationInhaleText, animationExhaleText,
            animationInhaleInnerCircle, animationExhaleInnerCircle;
    private Animation
            animationInitialInhaleInnerCircle, animationInitialExhaleInnerCircle,
            animationInitial, animationInitialText, doneCicleAnimation, doneTextAnimation;
    private Handler handler = new Handler();

    private int holdDuration = 0;
    int i = GlobalInfo.DEFAULT_DURATION_PREPA;

    CountDownTimer countDownTimer;

    boolean banderaIniciado = false, banderaCronometro = false, doneTime = false;
    MediaPlayer mediaPlayer;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.strategy_breathing);

        NeuroSkyManager.displaystrategy = true;

        contentLayout = findViewById(R.id.lt_content);
        //contentLayout.setOnTouchListener(contentTouchListener);

        statusText = findViewById(R.id.txt_status);
        textInstructions = findViewById(R.id.text_instructions);

        outerCircleView = findViewById(R.id.view_circle_outer);
        innerCircleView = findViewById(R.id.view_circle_inner);

        btnHome = findViewById(R.id.icon_home);
        btnGraph = findViewById(R.id.icon_graph);
        btnNotification = findViewById(R.id.icon_notifications);
        btnUser = findViewById(R.id.icon_user);


        btnHome.setOnClickListener(this);
        btnGraph.setOnClickListener(this);
        btnNotification.setOnClickListener(this);
        btnUser.setOnClickListener(this);


        prepareAnimations();

        innerCircleView.setOnClickListener(this);

        speaker=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    speaker.setLanguage(new Locale(getResources().getString(R.string.locale_lenguage_mx)));
                }
            }
        });

        speaker.setPitch(0.9f);

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

        //Done animation

        doneCicleAnimation = AnimationUtils.loadAnimation(this, R.anim.anim_inner_circle_inhale);
        doneCicleAnimation.setFillAfter(true);
        doneCicleAnimation.setDuration(GlobalInfo.DEFAULT_DURATION);

        doneTextAnimation = AnimationUtils.loadAnimation(this, R.anim.anim_text_inhale);
        doneTextAnimation.setFillAfter(true);



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
                String add = "00:" + realtime;
                if (realtime < 10) {
                    add = "00:0" + realtime;
                } else if (realtime == 60) {
                    add = "01:00";
                }
                textInstructions.setText(add);
            }

            public void onFinish() {
                textInstructions.setText(R.string.strategy_breath_almost);
                speaker.speak(textInstructions.getText().toString(),TextToSpeech.QUEUE_FLUSH,null);
                doneTime = true;
            }
        };


    }

    private Animation.AnimationListener inhaleAnimationListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
            if (!banderaCronometro) {
                Log.d(TAG, "inhale animation start");
                banderaCronometro = true;
                countDownTimer.start();

            }
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            Log.d(TAG, "inhale animation end");

            if (holdDuration != 0) {
                statusText.setText(R.string.text_hold);
                speaker.speak(statusText.getText().toString(),TextToSpeech.QUEUE_FLUSH,null);
            }


            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    statusText.setText(R.string.text_breath_out);
                    speaker.speak(statusText.getText().toString(),TextToSpeech.QUEUE_FLUSH,null);
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
            if (!doneTime) {
                if (holdDuration != 0) {

                    statusText.setText(R.string.text_hold);
                    speaker.speak(statusText.getText().toString(),TextToSpeech.QUEUE_FLUSH,null);
                }
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        statusText.setText(R.string.text_breath_in);
                        speaker.speak(statusText.getText().toString(),TextToSpeech.QUEUE_FLUSH,null);
                        statusText.startAnimation(animationInhaleText);
                        innerCircleView.startAnimation(animationInhaleInnerCircle);
                    }
                }, holdDuration);
            } else {
                textInstructions.setText(R.string.strategy_breath_done);
                speaker.speak(textInstructions.getText().toString(),TextToSpeech.QUEUE_FLUSH,null);
                statusText.setText(R.string.strategy_breath_done);
                innerCircleView.startAnimation(doneCicleAnimation);
                statusText.startAnimation(doneTextAnimation);
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
                    speaker.speak(textInstructions.getText().toString(),TextToSpeech.QUEUE_FLUSH,null);
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
            if (i > -3) {
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
            if (i > -3) {
                innerCircleView.startAnimation(animationInitialInhaleInnerCircle);
                i--;
            } else if (i == -3) {
                statusText.setText(R.string.text_breath_in);
                speaker.speak(statusText.getText().toString(),TextToSpeech.QUEUE_FLUSH,null);
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
        if (innerCircleView == v && !banderaIniciado) {
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

                    if (i > 0) {
                        statusText.setText("" + i);
                        i--;
                        handler.postDelayed(this, 1000);
                    } else if (i == 0) {

                        statusText.setText("Inicial");
                        statusText.setTextColor(getResources().getColor(R.color.color_button_green));
                        textInstructions.setText(R.string.text_relax_and_confortable);
                        speaker.speak(getResources().getString(R.string.text_relax_and_confortable),TextToSpeech.QUEUE_FLUSH,null);
                        innerCircleView.startAnimation(animationInitial);
                        statusText.startAnimation(animationInitialText);

                    }
                }
            }, 1000);
        } else if (btnHome == v) {
            Intent intent = new Intent(this, Index.class);
            startActivity(intent);
            stopSounds();
            finish();

        } else if (btnGraph == v) {
            Intent intent = new Intent(this, HistoryDetection.class);
            startActivity(intent);
            stopSounds();
            finish();
        } else if (btnNotification == v) {
            Intent intent = new Intent(this, SettingHeadset.class);
            startActivity(intent);
            stopSounds();
            finish();
        } else if (btnUser == v) {
            Intent intent = new Intent(this, User.class);
            startActivity(intent);
            stopSounds();
            finish();
        }

    }

    public void stopSounds(){
        NeuroSkyManager.displaystrategy = false;
        speaker.stop();
        mediaPlayer.stop();
    }

}
