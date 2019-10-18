package com.example.letsdoit;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import java.util.Timer;
import java.util.TimerTask;


/**
 * A Map Class which opens the map to show the
 * current location and the location of markers
 * @authors: Umit Civi & Abdulkadir Erol
 * @version: 01.05.2019 (Last Edited: 11.05.2019)
 */
public class Map extends Activity {

    MyLocationNewOverlay mLocationOverlay;
    MapView mapview;
    static Timer myTimer;
    TimerTask myTimerTask;
    Toast myToast;
    static WallCoordinates locations;
    boolean locationPermissionGranted;
    Button camera;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        locations = new WallCoordinates();
        locationPermissionGranted = false;
        myToast = new Toast(this);

        permissionsGranted();
        //osmdroid configurations
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        //Setting the package name as user agent to avoid getting banned
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);

        //inflate and create the map
        setContentView(R.layout.activity_map);
        mapview = (MapView)findViewById(R.id.mapview);
        mapview.setTileSource(TileSourceFactory.MAPNIK);
        mapview.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER);
        mapview.setMultiTouchControls(true);

        //Starting point of the map
        IMapController mapController = mapview.getController();
        mapController.setZoom(18.5);
        GeoPoint startPoint = new GeoPoint(39.866, 32.748693);
        mapController.setCenter(startPoint);

        //Getting users location
        mLocationOverlay = new MyLocationNewOverlay(mapview);
        mLocationOverlay.enableMyLocation();
        mLocationOverlay.enableFollowLocation();
        mapview.getOverlays().add(mLocationOverlay);

        //for loop for markers
        for (int i = 0; i < locations.allWallCoordinates.size(); i++) {
            Marker wall1 = new Marker(mapview);
            wall1.setPosition( new GeoPoint( locations.returnPoint(i).getLongitude(),
                    locations.returnPoint(i).getLatitude()));
            wall1.setTitle(( locations.allWallCoordinates.get(i)).getName());
            wall1.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
            wall1.setIcon(getResources().getDrawable(R.drawable.brick, null));
            mapview.getOverlays().add(wall1);
        }

        camera = findViewById(R.id.camera_button);
        camera.setEnabled(false);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCameraPage();
            }
        });

        checkForPermissions();

    }

    @Override
    public void onPause(){
        super.onPause();
        mapview.onPause();
        stopTimer();
        myToast.cancel();
        mLocationOverlay.disableMyLocation();
    }

    @Override
    public void onStop()
    {
        super.onStop();
        stopTimer();
        myToast.cancel();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        mapview.onResume();
        permissionsGranted();
        mLocationOverlay.enableMyLocation();
        checkForPermissions();
    }

    /**
     * Checks whether location permissions is granted or not, shows a Toast depending on the given permissions
     */
    public void permissionsGranted()

    {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "Please enable LOCATION ACCESS in settings.", Toast.LENGTH_LONG).show();
        }
        else
        {
            locationPermissionGranted = true;
            Toast.makeText(getApplicationContext(), "Location permissions granted", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Checks whether the user is near a certain wall
     */
    public void addProximityAlert()
    {
        myTimer = new Timer();
        myTimerTask = new TimerTask() {
            @Override
            public void run() {
                if( myTimer != null && mLocationOverlay.getMyLocation() != null) {
                    if (10 > (int) (mLocationOverlay.getMyLocation().distanceToAsDouble( new GeoPoint(locations.returnPoint(0).getLatitude(),locations.returnPoint(0).getLongitude())))) {

                        runOnUiThread(new Runnable() {
                            public void run() {
                                camera.setEnabled(true);
                                myToast.makeText(getApplicationContext(), "You are near a E-Wall", Toast.LENGTH_LONG).show();

                            }
                        });

                    } else if (10 > (int) (mLocationOverlay.getMyLocation().distanceToAsDouble(
                            new GeoPoint( locations.returnPoint(1).getLongitude(),
                            locations.returnPoint(1).getLatitude())))) {
                        camera.setEnabled(true);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                camera.setEnabled(true);
                                myToast.makeText(getApplicationContext(), "You are near a E-Wall", Toast.LENGTH_LONG).show();
                            }
                        });

                    } else if (10 > (int) (mLocationOverlay.getMyLocation().distanceToAsDouble(
                            new GeoPoint( locations.returnPoint(2).getLongitude(),
                            locations.returnPoint(2).getLatitude())))) {
                        camera.setEnabled(true);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                camera.setEnabled(true);
                                myToast.makeText(getApplicationContext(), "You are near a E-Wall", Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                    else
                    {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                camera.setEnabled(false);
                            }
                        });
                    }
                }
            }
        };
        myTimer.schedule(myTimerTask,4000,2800);
    }

    /**
     * adds proximity alerts if location permission is granted
     */
    public void checkForPermissions()
    {
        if(locationPermissionGranted) {
            addProximityAlert();
        }
    }

    /**
     * Stops and deletes all the events connected to the myTimer object
     */
    public void stopTimer()
    {
        if( myTimer != null)
        {
            myTimer.cancel();
            myTimer.purge();
            myTimer = null;
        }
    }
    public void openCameraPage(){
        Intent cameraPage = new Intent(Map.this, ArActivity.class);
        startActivity(cameraPage);
    }
}

