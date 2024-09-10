package api;

import java.sql.Date;
import java.sql.Time;

public class RecordPojo {

    private int timeRecordsId;
    private int userId;
    private String  clockInTime;
    private String clockOutTime;
    private double locationX;
    private double locationY;
    private String status;
    private String clockInDate;

    // Getters and Setters
    public int getTimeRecordsId() {
        return timeRecordsId;
    }

    public void setTimeRecordsId(int timeRecordsId) {
        this.timeRecordsId = timeRecordsId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String  getClockInTime() {
        return clockInTime;
    }

    public void setClockInTime(String clockInTime) {
        this.clockInTime = clockInTime;
    }

    public String getClockOutTime() {
        return clockOutTime;
    }

    public void setClockOutTime(String clockOutTime) {
        this.clockOutTime = clockOutTime;
    }

    public double getLocationX() {
        return locationX;
    }

    public void setLocationX(double locationX) {
        this.locationX = locationX;
    }

    public double getLocationY() {
        return locationY;
    }

    public void setLocationY(double locationY) {
        this.locationY = locationY;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getClockInDate() {
        return clockInDate;
    }

    public void setClockInDate(String clockInDate) {
        this.clockInDate = clockInDate;
    }
}
