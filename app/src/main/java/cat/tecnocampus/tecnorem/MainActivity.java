package cat.tecnocampus.tecnorem;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import cat.tecnocampus.tecnorem.Chronometer.Chronometer;
import cat.tecnocampus.tecnorem.Metronome.Metronome;
import cat.tecnocampus.tecnorem.Sensors.Accelerometer;
import cat.tecnocampus.tecnorem.Sensors.Gps;

public class MainActivity extends Activity {

    private NumberPicker npRowSpeed;

    private Button btStartPause;

    private Accelerometer accelerometer;
    private Chronometer chronometer;
    private Metronome metronome;
    private Gps gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        accelerometer = new Accelerometer(this);
        chronometer = new Chronometer(this);
        metronome = new Metronome(this);
        gps = new Gps(this);

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
        metronome.stopMetronome();
        btStartPause.setText("START");
        accelerometer.displayCleanValues();
        npRowSpeed.setVisibility(View.VISIBLE);
    }

    public void startRegister(){
        chronometer.startChronometer();
        accelerometer.startAccelerometer();
        metronome.startMetronome();
        btStartPause.setText("STOP");
        npRowSpeed.setVisibility(View.GONE);
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
            metronome.setPeriodTime((60 / newVal)*1000);
        }
    };

}
