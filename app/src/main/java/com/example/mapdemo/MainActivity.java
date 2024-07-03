package com.example.mapdemo;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private final int FINE_PERMISSION_CODE = 1;
    private GoogleMap gMap;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;

    private LatLng firstMarkerLocation = null;
    private LatLng secondMarkerLocation = null;
    private Button clearButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        System.loadLibrary("mapdemo");

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();

        clearButton = findViewById(R.id.clear_button);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearMarkers();
            }
        });
    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;

                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.id_map);
                    mapFragment.getMapAsync(MainActivity.this);
                }
            }
        });
    }

    private void showMarkerLocations() {
        if (firstMarkerLocation != null && secondMarkerLocation != null) {
            String message = "First Marker: Lat: " + firstMarkerLocation.latitude + ", Lng: " + firstMarkerLocation.longitude + "\n" +
                    "Second Marker: Lat: " + secondMarkerLocation.latitude + ", Lng: " + secondMarkerLocation.longitude;
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
    }

    private void calculateDistance() {
        if (firstMarkerLocation != null && secondMarkerLocation != null) {
            double distance = getDistanceFromNative(firstMarkerLocation.latitude, firstMarkerLocation.longitude, secondMarkerLocation.latitude, secondMarkerLocation.longitude);
            Toast.makeText(this, "CPP Function Result Distance: " + distance + " km", Toast.LENGTH_LONG).show();
        }
    }

    private void clearMarkers() {
        if (gMap != null) {
            gMap.clear();
            firstMarkerLocation = null;
            secondMarkerLocation = null;
            Toast.makeText(this, "Markers cleared", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FINE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                Toast.makeText(this, "Location permission is denied, please allow the permission", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * A native method that is implemented by the 'mapdemojni' native library,
     * which is packaged with this application.
     */
    /// Calculate air distance
    public native double getDistanceFromNative(double lat1, double lon1, double lat2, double lon2);

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap = googleMap;

        gMap.getUiSettings().setZoomControlsEnabled(true);
        gMap.getUiSettings().setCompassEnabled(true);
        gMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (firstMarkerLocation == null) {
                    firstMarkerLocation = latLng;
                    gMap.addMarker(new MarkerOptions().position(latLng).title("First Marker"));
                    Toast.makeText(MainActivity.this, "First marker set", Toast.LENGTH_SHORT).show();
                } else if (secondMarkerLocation == null) {
                    secondMarkerLocation = latLng;
                    gMap.addMarker(new MarkerOptions().position(latLng).title("Second Marker"));
                    Toast.makeText(MainActivity.this, "Second marker set", Toast.LENGTH_SHORT).show();
                    showMarkerLocations();
                    calculateDistance();
                }
            }
        });
    }
}
