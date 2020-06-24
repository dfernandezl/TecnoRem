package cat.tecnocampus.tecnorem.Metronome;

import android.content.Context;
import android.media.MediaPlayer;

import java.util.Timer;
import java.util.TimerTask;

import cat.tecnocampus.tecnorem.R;

public class Metronome {

    Context context;

    private Timer timer;
    private MediaPlayer mp;
    private TimerTask tone;
    private int periodTime = 6000;

    public Metronome(Context context) {
        this.context = context;
    }

    public void startMetronome(){
        timer = new Timer("MetronomeTimer", true);
        mp = MediaPlayer.create(context, R.raw.beep);
        tone = new TimerTask(){
            @Override
            public void run(){
                //Play sound
                mp.start();
            }
        };
        timer.scheduleAtFixedRate(tone, 0, periodTime);
    }

    public void stopMetronome(){
        mp.stop();
        tone.cancel();
        mp.release();
        timer.cancel();
    }

    public void setPeriodTime(int periodTime) {
        this.periodTime = periodTime;
    }

}
