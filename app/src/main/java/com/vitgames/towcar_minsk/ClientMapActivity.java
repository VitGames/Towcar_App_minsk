package com.vitgames.towcar_minsk;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ClientMapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private GoogleMap mMap;
    GoogleApiClient googleApiClient;
    Location lastLocation;
    LocationRequest locationRequest;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private Button logoutClientButton, SettingsClientButton, call_orderButton;
    private String clientId;
    private DatabaseReference clientdatabaseRef;
    private DatabaseReference driverslocationRef;
    private LatLng ClientPosition;
    private int radius = 1;
    private boolean driverfound = false;
    private String driver_foundID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_map);
        logoutClientButton = (Button) findViewById(R.id.client_logout_button);
        SettingsClientButton = (Button) findViewById(R.id.client_settings_button);
        call_orderButton = (Button) findViewById(R.id.client_order_button);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        clientId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        clientdatabaseRef = FirebaseDatabase.getInstance().getReference().child("Clients Request");
        driverslocationRef = FirebaseDatabase.getInstance().getReference().child("Driver Available");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        logoutClientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                LogoutClient();
                
            }
        });
        call_orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GeoFire geoFire = new GeoFire(clientdatabaseRef);
                geoFire.setLocation(clientId, new GeoLocation(lastLocation.getLatitude(),lastLocation.getLongitude()));
                ClientPosition = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                mMap.addMarker(new MarkerOptions().position(ClientPosition).title("Точка вызова"));
                call_orderButton.setText("Поиск эвакуатора...");
                getNearlyDrivers();
            }
        });
    }

    private void getNearlyDrivers() {
               GeoFire geoFire = new GeoFire(driverslocationRef);
               GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(ClientPosition.latitude,ClientPosition.longitude),radius);
               geoQuery.removeAllListeners();

               geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                   @Override
                   public void onKeyEntered(String key, GeoLocation location) {
                       if(!driverfound){
                           driverfound = true;
                           driver_foundID = key;
                       }
                   }

                   @Override
                   public void onKeyExited(String key) {

                   }

                   @Override
                   public void onKeyMoved(String key, GeoLocation location) {

                   }

                   @Override
                   public void onGeoQueryReady() {
                       if(!driverfound){
                           radius = radius + 1;
                           getNearlyDrivers();
                       }
                   }

                   @Override
                   public void onGeoQueryError(DatabaseError error) {

                   }
               });

    }

    private void LogoutClient() {
        Intent welcomeintent = new Intent(ClientMapActivity.this, WelcomeActivity.class);
        startActivity(welcomeintent);
        finish();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        buildGoogleApiClient();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(locationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        lastLocation = location;
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
    }
    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


}