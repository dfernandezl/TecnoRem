package cat.tecnocampus.tecnorem.Domain;

import android.location.Location;

public class RowingRegister {

    private long currentPositionTime, lastPositionTime, deltaPositionTime;
    private double currentLongitude, currentLatitude, lastLongitude, lastLatitude;
    private float[] results;

    public RowingRegister() {

    }

    public void setCurrentPositionTime(long currentPositionTime) {
        lastPositionTime = this.currentPositionTime;
        this.currentPositionTime = currentPositionTime;
    }

    public void setCurrentLongitude(double currentLongitude) {
        lastLongitude = this.currentLongitude;
        this.currentLongitude = currentLongitude;
    }

    public void setCurrentLatitude(double currentLatitude) {
        lastLatitude = this.currentLatitude;
        this.currentLatitude = currentLatitude;
    }

    public void computeDeltaTime(long currentPositionTime, long lastPositionTime){
        this.deltaPositionTime = (currentPositionTime - lastPositionTime) / 2000;
    }

    public void computeDeltaPosition(){
        Location.distanceBetween(lastLatitude, lastLongitude, currentLatitude, currentLongitude, results);
    }

    public void computeSpeed(){
        float speed;
        speed = results[0] / this.deltaPositionTime;
    }
}
