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
    private Sensor accelerometer, gravity, magnetic;

    private TextView currentAcce;

    private Context context;

    private float deltaX = 0;
    private float deltaY = 0;
    private float deltaZ = 0;
    private float deltaAcce = 0;

    private float[] gravityValues = null;
    private float[] magneticValues = null;

    private float vibrateThreshold = 0;

    public Vibrator v;

    public Accelerometer(Context context) {
        this.context = context;

        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if(sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null){
            // accelerometer OK
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            gravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
            magnetic = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

            vibrateThreshold = 2;
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
        sensorManager.registerListener((SensorEventListener) this, gravity, 12000);
        sensorManager.registerListener((SensorEventListener)this, magnetic, 12000);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        // clean current values
        displayCleanValues();
        // display the current x,y,z accelerometer values
        displayCurrentValues();

        if ((gravityValues != null) && (magneticValues != null)
                && (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)) {

            float[] deviceRelativeAcceleration = new float[4];
            deviceRelativeAcceleration[0] = event.values[0];
            deviceRelativeAcceleration[1] = event.values[1];
            deviceRelativeAcceleration[2] = event.values[2];
            deviceRelativeAcceleration[3] = 0;

            // Change the device relative acceleration values to earth relative values
            // X axis -> East
            // Y axis -> North Pole
            // Z axis -> Sky

            float[] R = new float[16], I = new float[16], earthAcc = new float[16];

            SensorManager.getRotationMatrix(R, I, gravityValues, magneticValues);

            float[] inv = new float[16];

            android.opengl.Matrix.invertM(inv, 0, R, 0);
            android.opengl.Matrix.multiplyMV(earthAcc, 0, inv, 0, deviceRelativeAcceleration, 0);

            deltaX = Math.abs(lastX - earthAcc[0]);
            deltaY = Math.abs(lastY - earthAcc[1]);
            deltaZ = Math.abs(lastZ - earthAcc[2]);

            // if the change is below 2, it is just plain noise
            if (deltaX < 2)
                deltaX = 0;
            if (deltaY < 2)
                deltaY = 0;
            if (deltaZ < 2)
                deltaZ = 0;

            lastX = earthAcc[0];
            lastY = earthAcc[1];
            lastZ = earthAcc[2];

            vibrate();

        } else if (event.sensor.getType() == Sensor.TYPE_GRAVITY) {
            gravityValues = event.values;
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            magneticValues = event.values;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    // if the change in the accelerometer value is big enough, then vibrate!
    // our threshold is MaxValue/2
    public void vibrate() {
        if (deltaX > vibrateThreshold || deltaY > vibrateThreshold || deltaZ > vibrateThreshold) {
            v.vibrate(50);
        }
    }

    public void initializeViews(){
        currentAcce = (TextView) ((Activity)context).findViewById(R.id.currentAcce);
    }

    public void displayCleanValues() {
        currentAcce.setText("0.0");
    }

    // display the current x,y,z accelerometer values
    public void displayCurrentValues() {
        currentAcce.setText(Float.toString(deltaAcce));
    }
}
