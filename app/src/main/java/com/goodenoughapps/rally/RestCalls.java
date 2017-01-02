package com.goodenoughapps.rally;

import com.goodenoughapps.rally.domain.PlaceType;
import com.goodenoughapps.rally.domain.Suggestion;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

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

            // Create a JSON object and parse it
            JSONObject jObj = new JSONObject(result);
            JSONArray jArr = jObj.getJSONArray("results");

            List<Suggestion> suggestions = new ArrayList<>();

            for(int i = 0; i < jArr.length(); i++) {

                JSONObject placeObj = jArr.getJSONObject(i);
                String address = placeObj.getString("formatted_address");
                String name = placeObj.getString("name");
                double latitude = placeObj.getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                double longitude = placeObj.getJSONObject("geometry").getJSONObject("location").getDouble("lng");

                Suggestion suggest = new Suggestion(name, address, new LatLng(latitude, longitude));
                suggestions.add(suggest);

            }

            return suggestions;


        } catch (Exception e) {
            return new ArrayList<>();
        }

    }


}
