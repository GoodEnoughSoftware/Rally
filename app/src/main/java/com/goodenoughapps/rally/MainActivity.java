package com.goodenoughapps.rally;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.goodenoughapps.rally.domain.PlaceType;
import com.goodenoughapps.rally.views.RallyPlace;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
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

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private FloatingActionButton addLocationFAB;
    private FloatingActionButton myLocationFAB;
    private Button doneButton;
    private Button clearButton;
    private Activity activity;
    private List<RallyPlace> places;
    private LinearLayout placesLinearLayout;
    private RelativeLayout confirmRelativeLayout;
    private TextView learnView;
    private int classIndex = -1;
    private final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 71;
    private GoogleApiClient mGoogleApiClient;
    private final int PERMISSIONS_REQUEST_CODE = 24;

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

        confirmRelativeLayout = (RelativeLayout) findViewById(R.id.confirmationRelativeLayout);
        addLocationFAB = (FloatingActionButton) findViewById(R.id.add_location_fab);
        myLocationFAB = (FloatingActionButton) findViewById(R.id.my_location_fab);
        learnView = (TextView) findViewById(R.id.learn_view);
        doneButton = (Button) findViewById(R.id.confirmationButton);
        clearButton = (Button) findViewById(R.id.clearAllButton);
        confirmRelativeLayout.setVisibility(View.GONE);
        addLocationFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPlaceSearch();
            }
        });
        myLocationFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "My Location Fab", Toast.LENGTH_SHORT).show();
            }
        });
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDoneClicked();
            }
        });
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                while (places.size() > 0) {
                    removePlace(0);
                }
            }
        });

        this.places = new ArrayList<>();

        // Initial UI config
        updateLocationList();

        // Show intro dialog if first time opening application
        onAppStartup();

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                addNewLocation(new RallyPlace(place));
                if (classIndex != -1) {
                    removePlace(classIndex);
                    classIndex = -1;
                }
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Show an error message dialog.
            } else if (resultCode == RESULT_CANCELED) {
            }
        } else if (requestCode == PERMISSIONS_REQUEST_CODE) {
            enableLocation();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS_REQUEST_CODE);
            }
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            RallyPlace place = new RallyPlace(mLastLocation);
            addNewLocation(place);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 15));
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        
    }

    /**
     * Methods which gets called once the user is done entering places
     */
    public void onDoneClicked() {
        new MaterialDialog.Builder(this)
                .title(R.string.location_dialog_title)
                .items(R.array.location_types)
                .positiveText(R.string.choose)
                .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        return true;
                    }
                })
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        int selected = dialog.getSelectedIndex();
                        String type = PlaceType.values()[selected].getCodeString();

                        LatLng midPoint = Util.getMidPoint(places);

                        Uri gmmIntentUri = Uri.parse("geo:" + midPoint.latitude + "," + midPoint.longitude + "?q=" + type);
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        startActivity(mapIntent);

                    }
                })
                .show();

    }

    /**
     * Method which gets called once a place is added
     * @param rallyPlace The place to add
     */
    public void addNewLocation(RallyPlace rallyPlace) {

        // Add the location to the places list
        places.add(rallyPlace);

        // Draw a marker on the map
        LatLng location = rallyPlace.getLatLng();
        mMap.addMarker(new MarkerOptions().position(location).title(rallyPlace.getTitle()));

        // Reposition the map by changing the bounding box
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(Util.getBounds(places), 128));

        // If only one place, zoom out
        if (places.size() == 1) {
            mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        }

        // Update the list of locations UI
        updateLocationList();

    }

    /**
     * Reset map markers by clearing the map
     */
    public void resetMapMarkers() {

        mMap.clear();

        for (RallyPlace place : places) {
            LatLng location = place.getLatLng();
            mMap.addMarker(new MarkerOptions().position(location).title(place.getTitle()));
        }

    }

    /**
     * Updates the list of locations at the top of the screen
     */
    public void updateLocationList() {

        // Hide and show things based on number of places
        if (places.size() > 0) {
            placesLinearLayout.setVisibility(View.VISIBLE);
            confirmRelativeLayout.setVisibility(View.VISIBLE);
            learnView.setVisibility(View.GONE);
        } else {
            placesLinearLayout.setVisibility(View.GONE);
            learnView.setVisibility(View.VISIBLE);
        }

        // Reset the entire list UI
        placesLinearLayout.removeAllViews();

        // For each place in places
        for (int i = 0; i < places.size(); i++) {

            final Integer index = new Integer(i);

            RallyPlace place = places.get(i);

            // Inflate a UI view
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            LinearLayout entryLinearLayout = (LinearLayout) inflater.inflate(R.layout.place_list_view, null);

            // Add the place name
            TextView nameTextView = (TextView) entryLinearLayout.findViewById(R.id.placeNameTextView);
            nameTextView.setText(place.getTitle());
            nameTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openPlaceSearch();
                    classIndex = index;
                }
            });
            // Attach a remove listener
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

    /**
     * Removes a place from the map, UI list, and internal list of places
     * @param index The location of the place to remove, from the internal list
     */
    public void removePlace(int index) {

        places.remove(index);
        updateLocationList();
        resetMapMarkers();

        if (places.size() != 0) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(Util.getBounds(places), 128));
            if (places.size() == 1) {
                mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
            }
        } else if (places.size() == 0) {
            confirmRelativeLayout.setVisibility(View.GONE);
            // If only one place, zoom out
            mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS_REQUEST_CODE);
            }
        } else {
            enableLocation();
        }
    }

    public void enableLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        } else {
            mMap.setMyLocationEnabled(true);
        }
    }

    public void openPlaceSearch() {
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

    /**
     * Shows a dialog with introduction during the first use of the application
     */
    public void onAppStartup() {

        SharedPreferences prefs = getSharedPreferences("RALLY_PREFS", MODE_PRIVATE);

        if (!prefs.getBoolean("returning", false)) {

            SharedPreferences.Editor edit = prefs.edit();
            edit.putBoolean("returning", true);
            edit.apply();

            new MaterialDialog.Builder(this)
                    .title(R.string.startup_dialog_title)
                    .content(R.string.startup_dialog_content)
                    .negativeText(R.string.dismiss)
                    .show();

        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
