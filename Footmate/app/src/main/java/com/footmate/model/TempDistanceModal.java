package com.footmate.model;

import com.google.android.gms.maps.model.Marker;
import java.io.Serializable;

/**
 * Created by patas tech on 2/24/2016.
 */
public class TempDistanceModal implements Serializable {
    float distance = 0.0f ;
    Marker marker =  null ;
    String venue;
    String addres;
    String city;


    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getAddres() {
        return addres;
    }

    public void setAddres(String addres) {
        this.addres = addres;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
