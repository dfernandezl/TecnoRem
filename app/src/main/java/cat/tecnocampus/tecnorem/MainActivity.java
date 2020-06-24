package cat.tecnocampus.tecnorem;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import cat.tecnocampus.tecnorem.Chronometer.Chronometer;
import cat.tecnocampus.tecnorem.Sensors.Accelerometer;

public class MainActivity extends Activity {

    private static final String TAG = "MyActivity";

    private NumberPicker npRowSpeed;

    private Button btStartPause;

    private Timer timer;
    private MediaPlayer mp;
    private TimerTask tone;
    private int periodTime = 6000;

    private Accelerometer accelerometer;
    private Chronometer chronometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        accelerometer = new Accelerometer(this);
        chronometer = new Chronometer(this);

        accelerometer.initializeViews();

        npRowSpeed = findViewById(R.id.npRowSpeed);

        npRowSpeed.setMinValue(10);
        npRowSpeed.setMaxValue(60);
        npRowSpeed.setWrapSelectorWheel(false);
        npRowSpeed.setOnValueChangedListener(onValueChangeListener);

        btStartPause = (Button)findViewById(R.id.btStartPause);



        btStartPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(chronometer.isTimeRunning()){ //stop timer
                    stopRegister();
                }
                else{ //continue timer
                    startRegister();
                }
            }
        });
    }

    public void stopRegister(){
        chronometer.stopChronometer();
        accelerometer.stopAccelerometer();
        btStartPause.setText("START");
        accelerometer.displayCleanValues();
        mp.stop();
        tone.cancel();
        mp.release();
        timer.cancel();

        npRowSpeed.setVisibility(View.VISIBLE);
    }

    public void startRegister(){
        chronometer.startChronometer();
        accelerometer.startAccelerometer();
        btStartPause.setText("STOP");

        npRowSpeed.setVisibility(View.GONE);

        timer = new Timer("MetronomeTimer", true);
        mp = MediaPlayer.create(this, R.raw.beep);
        tone = new TimerTask(){
            @Override
            public void run(){
                //Play sound
                mp.start();
            }
        };
        timer.scheduleAtFixedRate(tone, 0, periodTime);
    }

    //onResume() register the accelerometer for listening the events
    protected void onResume(){
        super.onResume();
        accelerometer.startAccelerometer();
    }

    //onPause() unregister the accelerometer for stop listening the events
    protected void onPause() {
        super.onPause();
        stopRegister();
    }


    NumberPicker.OnValueChangeListener onValueChangeListener = new NumberPicker.OnValueChangeListener() {
        @Override
        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
            TextView txtRowSpeedSelector = findViewById(R.id.txtRowSpeedSelector);
            txtRowSpeedSelector.setText("Selected speed: "+newVal);
            periodTime = (60 / newVal)*1000;
        }
    };

}
