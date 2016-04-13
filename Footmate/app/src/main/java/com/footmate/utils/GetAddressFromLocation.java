package com.footmate.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by patas tech on 2/24/2016.
 */
public class GetAddressFromLocation {
    public static String getAddress(Context ctx, double latitude, double longitude) {
    StringBuilder result = new StringBuilder();
    try {
        Geocoder geocoder = new Geocoder(ctx, Locale.getDefault());
        List<Address>
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
        if (addresses.size() > 0) {
            Address address = addresses.get(0);

            String a = address.getSubLocality();
//                Log.e(address.getAddressLine(1)+""+address.getAddressLine(0),"-------------------------"+address.toString());
//                Log.d("",""address.)
            String locality=address.getLocality();
            String city=address.getCountryName();
            String region_code=address.getCountryCode();
            String zipcode=address.getPostalCode();
            double lat =address.getLatitude();
            double lon= address.getLongitude();
            if(a != null)
                result.append(a+" ");
            if(locality != null)
                result.append(locality+" ");
            result.append(city+" "+ region_code+" ");
            if(zipcode != null)
                result.append(zipcode);

        }
    } catch (IOException e) {
        Log.e("tag", e.getMessage());
    }

    return result.toString();
}
}