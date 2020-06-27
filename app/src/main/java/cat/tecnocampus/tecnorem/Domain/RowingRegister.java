package cat.tecnocampus.tecnorem.Domain;

import android.location.Location;

public class RowingRegister {

    private long currentPositionTime, lastPositionTime, deltaPositionTime;
    private double currentLongitude, currentLatitude, lastLongitude, lastLatitude;
    private float[] results;

    public RowingRegister() {

    }
}
