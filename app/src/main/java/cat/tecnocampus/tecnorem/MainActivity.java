package cat.tecnocampus.tecnorem;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import cat.tecnocampus.tecnorem.Chronometer.Chronometer;
import cat.tecnocampus.tecnorem.Metronome.Metronome;
import cat.tecnocampus.tecnorem.Sensors.Accelerometer;

public class MainActivity extends Activity {

    private Button btStartPause, increase, decrease, btRestart;

    private Accelerometer accelerometer;
    private Chronometer chronometer;
    private Metronome metronome;

    private Context context;

    private int minteger = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        accelerometer = new Accelerometer(this);
        chronometer = new Chronometer(this);
        metronome = new Metronome(this);

        accelerometer.initializeViews();
        accelerometer.setDesiredStrokesPerMinute(minteger);

        btStartPause = (Button)findViewById(R.id.btStartPause);
        btRestart = (Button)findViewById(R.id.btRestart);
        increase = (Button)findViewById(R.id.increase);
        decrease = (Button)findViewById(R.id.decrease);

        btStartPause.setBackgroundColor(Color.GREEN);

        context = this;

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

        btRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!chronometer.isTimeRunning()){
                    try {
                        chronometer.clearChronoText();
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                    chronometer = new Chronometer(context);
                }
            }
        });

    }

    public void stopRegister(){
        chronometer.stopChronometer();
        accelerometer.stopAccelerometer();
        metronome.stopMetronome();
        btStartPause.setText("START");
        btStartPause.setBackgroundColor(Color.GREEN);
        accelerometer.displayCleanValues();
        btRestart.setEnabled(true);
    }

    public void startRegister(){
        chronometer.startChronometer();
        accelerometer.startAccelerometer();
        metronome.startMetronome();
        btStartPause.setText("STOP");
        btStartPause.setBackgroundColor(Color.RED);
        btRestart.setEnabled(false);
    }

    //onResume() register the accelerometer for listening the events
    protected void onResume(){
        super.onResume();
        accelerometer.startAccelerometer();
    }

    //onPause() unregister the accelerometer for stop listening the events
    protected void onPause() {
        if(chronometer.isTimeRunning()){
            stopRegister();
        }
        super.onPause();
    }

    public void increaseInteger(View view){
        minteger = minteger + 1;
        display(minteger);

        if(minteger == 60){
            increase.setEnabled(false);
        } else {
            increase.setEnabled(true);
            decrease.setEnabled(true);
        }
    }

    public void decreaseInteger(View view){
        minteger = minteger - 1;
        display(minteger);

        if(minteger == 10){
            decrease.setEnabled(false);
        } else {
            decrease.setEnabled(true);
            increase.setEnabled(true);
        }

    }

    public void display(int number){
        TextView displayInteger = (TextView) findViewById(R.id.integer_number);
        displayInteger.setText(""+number);
        accelerometer.setDesiredStrokesPerMinute(number);
        if(chronometer.isTimeRunning()){
            metronome.stopMetronome();
        }
        metronome.setPeriodTime((60 / number)*1000);
        if(chronometer.isTimeRunning()){
            metronome.startMetronome();
        }
    }
}
