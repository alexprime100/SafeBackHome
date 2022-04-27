package com.example.safebackhome.activities;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.safebackhome.R;

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
        try {
            Configuration.getInstance().load(getApplicationContext(), PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
            map = findViewById(R.id.map);
            map.setTileSource(TileSourceFactory.MAPNIK);
            map.setBuiltInZoomControls(true); //zoomable
            GeoPoint starPoint = new GeoPoint(48.390394, -4.486076);
            mapController = map.getController();
            mapController.setCenter(starPoint);
            mapController.setZoom(18.0);
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
}