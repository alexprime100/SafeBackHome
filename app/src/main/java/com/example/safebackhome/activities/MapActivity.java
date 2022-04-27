package com.example.safebackhome.activities;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.safebackhome.Data;
import com.example.safebackhome.R;
import com.example.safebackhome.service.ServiceLocation;
import com.google.android.gms.location.LocationServices;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class MapActivity extends AppCompatActivity {

    private MapView map;
    IMapController mapController;
    private Handler m_timerHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        /*
        if(!isLocationServiceRunning()){
            startLocationService();
        }
         */

        try {
            Configuration.getInstance().load(getApplicationContext(), PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
            map = findViewById(R.id.map);
            map.setTileSource(TileSourceFactory.MAPNIK);
            map.setBuiltInZoomControls(true); //zoomable
            GeoPoint startPoint = new GeoPoint(ServiceLocation.latitude, ServiceLocation.longitude);
            mapController = map.getController();
            mapController.setCenter(startPoint);
            mapController.setZoom(19.0);

            Marker myPosition = new Marker(map);
            myPosition.setPosition(startPoint);
            myPosition.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            myPosition.setTitle("Vous êtes ici");
            map.getOverlays().add(myPosition);

            m_timerHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (map != null) {
                        map.getOverlayManager().clear();
                        GeoPoint newLocation = new GeoPoint(ServiceLocation.latitude, ServiceLocation.longitude);
                        myPosition.setPosition(newLocation);
                        map.getOverlays().add(myPosition);
                        mapController.setCenter(newLocation);
                        map.onResume();
                        m_timerHandler.postDelayed(this, 10*1000);
                        //Log.d("LOCATION_UPDATE", "New geopoint : "+ServiceLocation.latitude + ", " + ServiceLocation.longitude);
                    }
                }
            }, 10*1000);

        }
        catch (Exception e){
            Log.e("Map Error", e.getMessage().toString());
        }
    }

    private boolean isLocationServiceRunning() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if(activityManager != null){
            for(ActivityManager.RunningServiceInfo service: activityManager.getRunningServices(Integer.MAX_VALUE)){
                if (LocationServices.class.getName().equals(service.service.getClassName())) {
                    if (service.foreground) {
                        return true;
                    }
                }
            }
            return false;
        }
        return false;
    }

    private void startLocationService(){
        if(!isLocationServiceRunning()){
            Intent intent = new Intent(getApplicationContext(), ServiceLocation.class);
            intent.setAction(Data.ACTION_START_LOCATION_SERVICE);
            startService(intent);
            Toast.makeText(this, "Location service started", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopLocationService(){
        if(isLocationServiceRunning()){
            Intent intent = new Intent(getApplicationContext(), ServiceLocation.class);
            intent.setAction(Data.ACTION_STOP_LOCATION_SERVICE);
            startService(intent);
            Toast.makeText(this, "Location service stopped", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        map.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        map.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(isLocationServiceRunning()){
            stopLocationService();
        }
    }
}