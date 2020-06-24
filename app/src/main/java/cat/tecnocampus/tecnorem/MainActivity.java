package cat.tecnocampus.tecnorem;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import cat.tecnocampus.tecnorem.Sensors.Accelerometer;

public class MainActivity extends Activity {

    private static final String TAG = "MyActivity";


    private TextView chrono;
    private Button btStartPause;
    private NumberPicker npRowSpeed;
    private long millisecondTime, startTime, timeBuff, updateTime = 0L;
    private Handler handler;
    private int seconds, minutes, milliseconds;
    private boolean timeRunning = false;

    private Timer timer;
    private MediaPlayer mp;
    private TimerTask tone;
    private int periodTime = 6000;

    private Accelerometer accelerometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        accelerometer = new Accelerometer(this);

        //initializeViews();
        accelerometer.initializeViews();

        npRowSpeed = findViewById(R.id.npRowSpeed);

        npRowSpeed.setMinValue(10);
        npRowSpeed.setMaxValue(60);
        npRowSpeed.setWrapSelectorWheel(false);
        npRowSpeed.setOnValueChangedListener(onValueChangeListener);

        chrono = (TextView)findViewById(R.id.txtTimer);
        btStartPause = (Button)findViewById(R.id.btStartPause);

        handler = new Handler();

        btStartPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(timeRunning){ //stop timer
                    stopRegister();
                }
                else{ //continue timer
                    startRegister();
                }
            }
        });


    }



    public void stopRegister(){
        timeBuff += millisecondTime;
        handler.removeCallbacks(runnable);
        //sensorManager.unregisterListener(this);
        accelerometer.stopAccelerometer();
        timeRunning = false;
        btStartPause.setText("START");
        //displayCleanValues();
        accelerometer.displayCleanValues();
        mp.stop();
        tone.cancel();
        mp.release();
        timer.cancel();

        npRowSpeed.setVisibility(View.VISIBLE);
    }

    public void startRegister(){
        startTime = SystemClock.uptimeMillis();
        handler.postDelayed(runnable,0);
        //sensorManager.registerListener(this, accelerometer,12000);
        accelerometer.startAccelerometer();
        timeRunning = true;
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
        //sensorManager.registerListener(this, accelerometer, 12000);
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



    public Runnable runnable = new Runnable() {
        @Override
        public void run() {
            millisecondTime = SystemClock.uptimeMillis() - startTime;
            updateTime = timeBuff + millisecondTime;

            seconds = (int) (updateTime / 1000);
            minutes = seconds / 60;
            seconds = seconds % 60;
            milliseconds = (int) (updateTime % 1000);

            chrono.setText("" +minutes+ ":" +String.format("%02d", seconds) +":" +String.format("%03d", milliseconds));

            handler.postDelayed(this,0);
        }
    };

}
