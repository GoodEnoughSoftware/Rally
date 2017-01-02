package com.goodenoughapps.rally;

import com.goodenoughapps.rally.domain.PlaceType;
import com.goodenoughapps.rally.domain.Suggestion;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A collection of REST calls using OkHTTP for gather information from servers,
 * such as the Google Places RESTful API
 * @author Aaron Vontell
 */
public class RestCalls {

    /**
     * Returns a list of a suggestions for meeting up using the Google Places API
     * @param midpoint
     * @param type
     * @return
     */
    public static List<Suggestion> getSuggestions(LatLng midpoint, PlaceType type) {

        String URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
        URL += "key=" + "";
        URL += "&location=" + midpoint.latitude + "," + midpoint.longitude;
        URL += "&radius=5000"; // TODO: Change this from default
        URL += "&type=" + type.getCodeString();

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(URL)
                .build();

        try {

            Response response = client.newCall(request).execute();
            String result =  response.body().string();

            List<Suggestion> suggestions = new ArrayList<>();

            return suggestions;


        } catch (IOException e) {
            return new ArrayList<>();
        }

    }


}
