package com.goodenoughapps.rally;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Utility methods for calculations used within Rally
 * @author Aaron Vontell
 */

public class Util {

    /**
     * Returns the midpoint of multiple LatLng points
     * @param points The points to find the middle point from
     * @return The LatLng object which is the middle point
     */
    public static LatLng getMidPoint(List<Place> points) {

        double latSum = 0;
        double lngSum = 0;

        for(Place point  : points) {

            latSum += point.getLatLng().latitude;
            lngSum += point.getLatLng().longitude;

        }

        return new LatLng(latSum / (double) points.size(), lngSum / (double) points.size());

    }

}
