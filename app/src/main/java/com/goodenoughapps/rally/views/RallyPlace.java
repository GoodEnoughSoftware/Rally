package com.goodenoughapps.rally.views;

import android.location.Location;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by tejpatel on 1/7/17.
 */

public class RallyPlace {

    private boolean isPlace;
    private Location location;
    private Place place;

    public RallyPlace(Location location) {
        this.location = location;
        this.isPlace = false;
    }

    public RallyPlace(Place place) {
        this.place = place;
        this.isPlace = true;
    }

    public LatLng getLatLng() {
        if(this.isPlace) {
            return this.place.getLatLng();
        } else {
            return new LatLng(this.location.getLatitude(), this.location.getLongitude());
        }
    }

    public String getTitle() {
        if(this.isPlace) {
            return this.place.getAddress().toString();
        } else {
            return this.location.toString();
        }
    }

    public boolean isPlace() { return isPlace; }

}
