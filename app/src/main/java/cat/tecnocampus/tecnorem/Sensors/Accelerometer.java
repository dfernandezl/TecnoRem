package cat.tecnocampus.tecnorem.Sensors;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Vibrator;
import android.util.Log;
import android.widget.TextView;

import java.util.LinkedList;

import cat.tecnocampus.tecnorem.R;

public class Accelerometer implements SensorEventListener {

    final String TAG = "ACCELEROMETER";

    private float lastX, lastY, lastZ;
    private SensorManager sensorManager;
    private Sensor accelerometer, gravity, magnetic;

    private TextView txtStrokesPerMinute;

    private Context context;

    private float deltaX = 0;
    private float deltaY = 0;
    private float deltaZ = 0;
    private float lastAcce = 0;

    private int stroke, strokesPerMinute = 0;
    private MovingAverage movingAverage;

    private float[] gravityValues = null;
    private float[] magneticValues = null;

    private float vibrateThreshold = 0;

    private int desiredStrokesPerMinute = 0;

    private Gps gps;

    public Vibrator v;
    private float deltaAcce;

    public Accelerometer(Context context) {
        this.context = context;
        movingAverage = new MovingAverage(10);

        gps = new Gps(context);

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
        sensorManager.unregisterListener(this);
    }

    public void startAccelerometer(){
        sensorManager.registerListener(this, accelerometer,1000000);
        sensorManager.registerListener(this, gravity, 1000000);
        sensorManager.registerListener(this, magnetic, 1000000);
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

            deltaAcce = Math.abs(lastAcce - computeDeltaAcce());
            Log.d(TAG, ""+deltaAcce);

            // if the change is below 2, it is just plain noise
            if (deltaAcce == 0){
                stroke = 0;
            } else{
                stroke = 1;
            }

             strokesPerMinute = (int) (60 / (movingAverage.next(stroke) * 10));

            lastX = earthAcc[0];
            lastY = earthAcc[1];
            lastZ = earthAcc[2];

            lastAcce = computeDeltaAcce();

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
        if (lastAcce > vibrateThreshold) {
            v.vibrate(50);
        }
    }

    public void initializeViews(){
        txtStrokesPerMinute = (TextView) ((Activity)context).findViewById(R.id.currentAcce);
    }

    public void displayCleanValues() {
        txtStrokesPerMinute.setText("0.0");
    }

    // display the current x,y,z accelerometer values
    public void displayCurrentValues() {
        txtStrokesPerMinute.setText(Float.toString(strokesPerMinute));

        if(strokesPerMinute > desiredStrokesPerMinute){
            txtStrokesPerMinute.setTextColor(Color.GREEN);
        } else if (strokesPerMinute == desiredStrokesPerMinute){
            txtStrokesPerMinute.setTextColor(Color.BLUE);
        }
        else{
            txtStrokesPerMinute.setTextColor(Color.RED);
        }


    }

    private float computeDeltaAcce() {
        double scalarProduct = (gps.getSpeedX()*lastX) + (gps.getSpeedY()*lastY) + (gps.getSpeedZ()*lastZ);
        double speedModule = Math.sqrt(gps.getSpeedX()*gps.getSpeedX() + gps.getSpeedY()*gps.getSpeedZ() + gps.getSpeedZ()*gps.getSpeedZ());

        return (float) (scalarProduct / speedModule);
    }

    public void setDesiredStrokesPerMinute(int desiredStrokesPerMinute) {
        this.desiredStrokesPerMinute = desiredStrokesPerMinute;
    }
}

class MovingAverage {
    double sum;
    int size;
    LinkedList<Integer> list;

    /** Initialize your data structure here. */
    public MovingAverage(int size) {
        this.list = new LinkedList<>();
        this.size = size;
    }

    public double next(int val) {
        sum += val;
        list.offer(val);

        if(list.size()<=size){
            return sum/list.size();
        }

        sum -= list.poll();

        return sum/size;
    }
}
