package com.goodenoughapps.rally;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

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

    /**
     * Returns a bounding box for a list of places, with margins
     * @param points The places to create a bounding box for
     * @return The bounding box object
     */
    public static LatLngBounds getBounds(List<Place> points) {

        double west = 0.0;
        double east = 0.0;
        double north = 0.0;
        double south = 0.0;

        for (int lc = 0; lc < points.size(); lc++)
        {
            LatLng loc = points.get(lc).getLatLng();
            if (lc == 0)
            {
                north = loc.latitude;
                south = loc.latitude;
                west = loc.longitude;
                east = loc.longitude;
            }
            else
            {
                if (loc.latitude > north)
                {
                    north = loc.latitude;
                }
                else if (loc.latitude < south)
                {
                    south = loc.latitude;
                }
                if (loc.longitude < west)
                {
                    west = loc.longitude;
                }
                else if (loc.longitude > east)
                {
                    east = loc.longitude;
                }
            }
        }

        LatLng northEast = new LatLng(north, east);
        LatLng southWest = new LatLng(south, west);

        return new LatLngBounds(southWest, northEast);

    }

}
