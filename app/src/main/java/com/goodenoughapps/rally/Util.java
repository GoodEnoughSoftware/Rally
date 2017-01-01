package com.goodenoughapps.rally;

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
    public static LatLng getMidPoint(List<LatLng> points) {

        double latSum = 0;
        double lngSum = 0;

        for(LatLng point  : points) {

            latSum += point.latitude;
            lngSum += point.longitude;

        }

        return new LatLng(latSum / (double) points.size(), lngSum / (double) points.size());

    }

}
