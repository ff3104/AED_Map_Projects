package com.freefly3104.satoshi.mymap01;

/**
 * Created by satoshi on 2015/05/07.
 */
public class Info {

    private String name;
    private String address;
    private String facilityName;
    private String facilityPlace;
    private String contactPoint;
    private String contactTelephone;
    private String url;
    private double latitude;
    private double longitude;


    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public void setFacilityPlace(String facilityPlace) {
        this.facilityPlace = facilityPlace;
    }

    public void setContactPoint(String contactPoint) {
        this.contactPoint = contactPoint;
    }

    public void setContactTelephone(String contactTelephone) {
        this.contactTelephone = contactTelephone;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public String getFacilityPlace() {
        return facilityPlace;
    }

    public String getContactPoint() {
        return contactPoint;
    }

    public String getContactTelephone() {
        return contactTelephone;
    }

    public String getUrl() {
        return url;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
