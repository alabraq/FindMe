package com.example.findme;

public class LocationsClass {

    private String senderNameSurname;
    private String senderPhoto;
    private Double latitude;
    private Double longitude;
    private Double altitude;
    private String date;


    public String getSenderNameSurname() {
        return senderNameSurname;
    }

    public void setSenderNameSurname(String senderNameSurname) {
        this.senderNameSurname = senderNameSurname;
    }


    public String getSenderPhoto() {
        return senderPhoto;
    }

    public void setSenderPhoto(String senderPhoto) {
        this.senderPhoto = senderPhoto;
    }


    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }


    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }


    public Double getAltitude() {
        return altitude;
    }

    public void setAltitude(Double altitude) {
        this.altitude = altitude;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
