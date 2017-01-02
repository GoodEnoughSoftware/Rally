package com.goodenoughapps.rally.domain;

import com.google.android.gms.maps.model.LatLng;

/**
 * A place suggestion received from Google Places API
 * @author Aaron Vontell
 */
public class Suggestion {

    private final String name;
    private final String address;
    private final LatLng location;

    public Suggestion(String name, String address, LatLng location) {
        this.name = name;
        this.address = address;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public LatLng getLocation() {
        return location;
    }
}
