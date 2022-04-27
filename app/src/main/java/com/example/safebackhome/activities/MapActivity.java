package com.example.safebackhome.activities;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

public class MapActivity extends AppCompatActivity {

    private MapView map;
    IMapController mapController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        startLocationService();

        try {
            Configuration.getInstance().load(getApplicationContext(), PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
            map = findViewById(R.id.map);
            map.setTileSource(TileSourceFactory.MAPNIK);
            map.setBuiltInZoomControls(true); //zoomable
            GeoPoint myLocation = new GeoPoint(ServiceLocation.latitude, ServiceLocation.latitude);
            mapController = map.getController();
            mapController.setCenter(myLocation);
            mapController.setZoom(18.0);
            Toast.makeText(this, "init : "+ServiceLocation.latitude+", "+ServiceLocation.longitude, Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            Log.e("Map Error", e.getMessage().toString());
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
    public void onDestroy() {
        super.onDestroy();
        stopLocationService();
    }
}