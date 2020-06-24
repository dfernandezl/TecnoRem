package cat.tecnocampus.tecnorem.Chronometer;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.widget.TextView;

import cat.tecnocampus.tecnorem.R;

public class Chronometer{

    private Context context;

    private TextView chrono;

    private long millisecondTime, startTime, timeBuff, updateTime = 0L;
    private Handler handler;
    private int seconds, minutes, milliseconds;
    private boolean timeRunning = false;

    public Chronometer(Context context) {
        this.context = context;
        chrono = (TextView)((Activity)context).findViewById(R.id.txtTimer);
        handler = new Handler();
    }

    public void startChronometer(){
        startTime = SystemClock.uptimeMillis();
        handler.postDelayed(runnable,0);
        timeRunning = true;
    }

    public void stopChronometer(){
        timeBuff += millisecondTime;
        handler.removeCallbacks(runnable);
        timeRunning = false;
    }

    public boolean isTimeRunning() {
        return timeRunning;
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
