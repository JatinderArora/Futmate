package com.footmate.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by sahil on 08-02-2016.
 */
public class SelectListModal implements Serializable{
    String Vanue = "";
    String Address = "";
    String Latitude = "";
    String Longitude = "";
    String Distance = "";
    String city = "";

    ArrayList<SelectListModal> placeslist;

    public ArrayList<SelectListModal> getPlaceslist() {
        return placeslist;
    }

    public void setPlaceslist(ArrayList<SelectListModal> placeslist) {
        this.placeslist = placeslist;
    }

    public String getVanue() {
        return Vanue;
    }

    public void setVanue(String vanue) {
        Vanue = vanue;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getDistance() {
        return Distance;
    }

    public void setDistance(String distance) {
        Distance = distance;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
