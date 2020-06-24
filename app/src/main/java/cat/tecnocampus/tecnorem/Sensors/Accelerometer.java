package cat.tecnocampus.tecnorem.Sensors;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Vibrator;
import android.widget.TextView;

import cat.tecnocampus.tecnorem.R;

public class Accelerometer implements SensorEventListener {

    private float lastX, lastY, lastZ;
    private SensorManager sensorManager;
    private Sensor accelerometer;

    private TextView currentX, currentY, currentZ, currentAcce;

    private Context context;

    private float deltaX = 0;
    private float deltaY = 0;
    private float deltaZ = 0;
    private float deltaAcce = 0;


    private float vibrateThreshold = 0;

    public Vibrator v;

    public Accelerometer(Context context) {
        this.context = context;

        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if(sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null){
            // accelerometer OK
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            vibrateThreshold = 3;
        }
        else {
            // fail, accelerometer not available (print some message!)
        }

        //initialize vibration
        v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    public void stopAccelerometer(){
        sensorManager.unregisterListener((SensorEventListener) this);
    }

    public void startAccelerometer(){
        sensorManager.registerListener((SensorEventListener) this, accelerometer,12000);
    }


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

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    // if the change in the accelerometer value is big enough, then vibrate!
    // our threshold is MaxValue/2
    public void vibrate() {
        if (deltaAcce > vibrateThreshold) {
            v.vibrate(50);
        }
    }

    public void initializeViews(){
        currentX = (TextView) ((Activity)context).findViewById(R.id.currentX);
        currentY = (TextView) ((Activity)context).findViewById(R.id.currentY);
        currentZ = (TextView) ((Activity)context).findViewById(R.id.currentZ);
        currentAcce = (TextView) ((Activity)context).findViewById(R.id.currentAcce);
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
}
