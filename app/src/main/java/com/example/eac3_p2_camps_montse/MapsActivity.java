package com.example.eac3_p2_camps_montse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Locale;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private GoogleMap mMap;
    private Location mCurrentLocation;
    private Button btInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //- Botó per mostrar el següent missatge: La meva localització és: (coordenades del lloc).
        //- Les coordenades han d'estar en format (latitud, longitud). (veure res/values/strings)
        btInfo = findViewById(R.id.btInfo);
        btInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayCurrentLocation();
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container,mapFragment).commit();
        mapFragment.getMapAsync(this);
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
        float zoom = 13;
        //- El mapa ha de mostrar una zona determinada com el de l'exemple.
        //- Pinteu un globus vermell que marqui una ubicació d'un altre lloc identificat amb un nom
        LatLng ioc = new LatLng(41.374744, 2.168329);
        mMap.addMarker(new MarkerOptions().position(ioc).title("IOC"));
        //- El nivell de zoom ha de ser 13.
        //- El mapa s'ha de centrar automàticament amb la ubicació del usuari en el centre.
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ioc, zoom));
        //- El mapa ha de ser del tipus Terreny.
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        //- L'aplicació haurà d'obtenir la posició actual del dispositiu a través del GPS del dispositiu.
        //- Ha de sortir la posició del usuari amb el cercle blau.
        //- Botó per centrar el mapa a la localització actual del dispositiu.
        enableMyLocation(mMap);
        //- Sobre el mapa hi ha d'haver els controls de zoom.
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }

    /**
     * Checks for location permissions, and requests them if they are missing.
     * Otherwise, enables the location layer.
     */
    private void enableMyLocation(GoogleMap map) {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // Check if location permissions are granted and if so enable the
        // location data layer.
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                if (grantResults.length > 0
                        && grantResults[0]
                        == PackageManager.PERMISSION_GRANTED) {
                    enableMyLocation(mMap);
                    break;
                }
        }
    }

    /**
     * Gets the current location and shows it using a toast.
     */
    private void displayCurrentLocation() {
        getCurrentLocation();

        String snippet = getString(R.string.my_localization) + " " + String.format(Locale.getDefault(),
                getString(R.string.current_latlng_format),
                mCurrentLocation.getLatitude(),
                mCurrentLocation.getLongitude());
        Toast.makeText(getApplicationContext(), snippet, Toast.LENGTH_LONG).show();
    }

    /**
     * Gets the current location and handles the needed permissions.
     */
    private void getCurrentLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]
                                {Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_LOCATION_PERMISSION);
                return;
            }
        }
        mCurrentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }

}
