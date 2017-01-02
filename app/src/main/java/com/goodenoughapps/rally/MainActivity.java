package com.goodenoughapps.rally;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FloatingActionButton addLocationFab;
    private FloatingActionButton doneFab;
    private Activity activity;
    private List<Place> places;
    private LinearLayout placesLinearLayout;

    private final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 71;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        placesLinearLayout = (LinearLayout) findViewById(R.id.placesListLinearLayout);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        this.activity = this;

        addLocationFab = (FloatingActionButton) findViewById(R.id.locationPickerFAB);
        doneFab = (FloatingActionButton) findViewById(R.id.confirmLocationsFAB);
        addLocationFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                    .build(activity);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }
            }
        });
        doneFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDoneClicked();
            }
        });

        this.places = new ArrayList<>();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                addNewLocation(place);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Show an error message dialog.
                // Log.i(TAG, status.getStatusMessage());

            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * Methods which gets called once the user is done entering places
     */
    public void onDoneClicked() {

        LatLng midPoint = Util.getMidPoint(places);

        Log.i("MIDPOINT", midPoint.toString());

        // Search for restaurants in San Francisco
        Uri gmmIntentUri = Uri.parse("geo:" + midPoint.latitude + "," + midPoint.longitude + "?q=cafe");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);

    }

    /**
     * Method which gets called once a place is added
     * @param place The place to add
     */
    public void addNewLocation(Place place) {

        // Add the location to the places list
        places.add(place);

        // Draw a marker on the map
        LatLng location = place.getLatLng();
        mMap.addMarker(new MarkerOptions().position(location).title(place.getName().toString()));

        // Reposition the map by changing the bounding box
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(Util.getBounds(places), 128));

        // Update the list of locations UI
        updateLocationList();

    }

    /**
     * Updates the list of locations at the top of the screen
     */
    public void updateLocationList() {

        // Reset the entire list UI
        placesLinearLayout.removeAllViews();

        // For each place in places
        for(int i=0; i<places.size(); i++) {

            Place place = places.get(i);

            // Inflate a UI view
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            LinearLayout entryLinearLayout = (LinearLayout) inflater.inflate(R.layout.place_list_view, null);

            // Add the place name
            ((TextView) entryLinearLayout.findViewById(R.id.placeNameTextView)).setText(place.getName());

            // Attach a remove listener
            final Integer index = new Integer(i);
            ((ImageButton) entryLinearLayout.findViewById(R.id.imageButton)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    removePlace(index);
                }
            });

            // Add the view to the list UI
            placesLinearLayout.addView(entryLinearLayout);

        }

    }

    public void removePlace(int index) {

        places.remove(index);
        updateLocationList();

        if(places.size() != 0) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(Util.getBounds(places), 128));
        }

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }
}
