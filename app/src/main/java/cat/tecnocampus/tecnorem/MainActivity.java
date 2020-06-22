package cat.tecnocampus.tecnorem;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity implements SensorEventListener {

    private float lastX, lastY, lastZ;

    private SensorManager sensorManager;
    private Sensor accelerometer;

    private float deltaX = 0;
    private float deltaY = 0;
    private float deltaZ = 0;
    private float deltaAcce = 0;

    private TextView currentX, currentY, currentZ, currentAcce;

    private float vibrateThreshold = 0;

    public Vibrator v;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeViews();

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

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if(sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null){
            // accelerometer OK
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            vibrateThreshold = 3;
        }
        else {
            // fail, accelerometer not available (print some message!)
        }

        //initialize vibration
        v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
    }

    public void initializeViews(){
        currentX = (TextView) findViewById(R.id.currentX);
        currentY = (TextView) findViewById(R.id.currentY);
        currentZ = (TextView) findViewById(R.id.currentZ);
        currentAcce = (TextView) findViewById(R.id.currentAcce);
    }

    public void stopRegister(){
        timeBuff += millisecondTime;
        handler.removeCallbacks(runnable);
        sensorManager.unregisterListener(this);
        timeRunning = false;
        btStartPause.setText("START");
        displayCleanValues();
        mp.stop();
        tone.cancel();
        mp.release();
        timer.cancel();

        npRowSpeed.setVisibility(View.VISIBLE);
    }

    public void startRegister(){
        startTime = SystemClock.uptimeMillis();
        handler.postDelayed(runnable,0);
        sensorManager.registerListener(this, accelerometer,12000);
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

    @Override
    public void onSensorChanged(SensorEvent event) {

        // clean current values
        displayCleanValues();
        // display the current x,y,z accelerometer values
        displayCurrentValues();


        // get the change of the x,y,z values of the accelerometer
        deltaX = Math.abs(lastX - event.values[0]);
        deltaY = Math.abs(lastY - event.values[1]);
        deltaZ = Math.abs(lastZ - event.values[2]);

        deltaAcce = (float)Math.sqrt(deltaX*deltaX + deltaY*deltaY + deltaZ*deltaZ);

        // if the change is below 2, it is just plain noise
        if (deltaX < 0.5)
            deltaX = 0;
        if (deltaY < 0.5)
            deltaY = 0;
        if (deltaZ < 0.5)
            deltaZ = 0;

        if(deltaAcce < 0.5)
            deltaAcce = 0;

        // set the last know values of x,y,z
        lastX = event.values[0];
        lastY = event.values[1];
        lastZ = event.values[2];

        vibrate();
        //Log.i(TAG, "Sensor Timestamp: " + event.timestamp);
    }

    // if the change in the accelerometer value is big enough, then vibrate!
    // our threshold is MaxValue/2
    public void vibrate() {
        if (deltaAcce > vibrateThreshold) {
            v.vibrate(50);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void displayCleanValues() {
        currentX.setText("0.0");
        currentY.setText("0.0");
        currentZ.setText("0.0");
        currentAcce.setText("0.0");
    }

    // display the current x,y,z accelerometer values
    public void displayCurrentValues() {
        currentX.setText(Float.toString(deltaX));
        currentY.setText(Float.toString(deltaY));
        currentZ.setText(Float.toString(deltaZ));
        currentAcce.setText(Float.toString(deltaAcce));
    }

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
