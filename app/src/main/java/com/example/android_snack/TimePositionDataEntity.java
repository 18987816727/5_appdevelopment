package com.example.android_snack;

public class TimePositionDataEntity {
    private int id;
    private String dateTime;
    private double latitude;
    private double longitude;
    private String ganZhi;
    private int isPush;

    public TimePositionDataEntity() {
        // 默认构造函数
    }

    public TimePositionDataEntity(int id,String dateTime, double latitude, double longitude, String ganZhi,int isPush) {
        this.id=id;
        this.dateTime = dateTime;
        this.latitude = latitude;
        this.longitude = longitude;
        this.ganZhi = ganZhi;
        this.isPush=isPush;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getGanZhi() {
        return ganZhi;
    }

    public void setGanZhi(String ganZhi) {
        this.ganZhi = ganZhi;
    }

    @Override
    public String toString() {
        return "TimePositionDataEntity{" +
                "id=" + id +
                ", dateTime='" + dateTime + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", ganZhi='" + ganZhi + '\'' +
                ", isPush=" + isPush +
                '}';
    }
}
