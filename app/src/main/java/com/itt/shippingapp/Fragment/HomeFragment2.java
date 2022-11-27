package com.itt.shippingapp.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.itt.shippingapp.R;

import java.io.IOException;
import java.util.List;

public class HomeFragment2 extends Fragment implements OnMapReadyCallback {

    SearchView searchView;
    double lat = -0.1, lon = -0.1;
    String locName = "";
    Button submit;
    SupportMapFragment supportMapFragment;
    FusedLocationProviderClient fusedLocationProviderClient;
    LatLng src;
    ImageButton gps;
    GoogleMap gmap;
    public static LatLngBounds BOUNDS_INDIA;

    HomeFragment2(LatLng lat1) {
        src = lat1;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map2);
        searchView = view.findViewById(R.id.sv_view_2);
        submit = view.findViewById(R.id.submit_home2);
        gps = view.findViewById(R.id.gps2);
        submit.setEnabled(false);
        submit.setClickable(false);
        BOUNDS_INDIA = new LatLngBounds(new LatLng(7.798000, 68.14712), new LatLng(37.090000, 97.34466));
        if(gmap==null){
            supportMapFragment.getMapAsync(this);
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                String loc = searchView.getQuery().toString();
                List<Address> addresses;

                Geocoder geocoder = new Geocoder(getActivity());
                try {
                    addresses = geocoder.getFromLocationName(loc, 1);
                    if (!addresses.isEmpty()) {
                        lat = addresses.get(0).getLatitude();
                        lon = addresses.get(0).getLongitude();
                        locName = loc;
                        submit.setEnabled(true);
                        submit.setClickable(true);
                        supportMapFragment.getMapAsync(callback);
                    } else {
                        Toast.makeText(getActivity(), "No location found", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (searchView.getQuery().toString().isEmpty() && (lat == -0.1)) {
                    Toast.makeText(getActivity(), "Enter destination city", Toast.LENGTH_SHORT).show();
                } else {
                    String loc = searchView.getQuery().toString();
                    List<Address> addresses;

                    Geocoder geocoder = new Geocoder(getActivity());
                    try {
                        addresses = geocoder.getFromLocationName(loc, 1);
                        if (!addresses.isEmpty()) {
                            lat = addresses.get(0).getLatitude();
                            lon = addresses.get(0).getLongitude();
                            locName = loc;

                            submit.setEnabled(true);
                            submit.setClickable(true);
                            supportMapFragment.getMapAsync(callback);
                        } else {
                            Toast.makeText(getActivity(), "No location found", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (src.latitude == lat && src.longitude == lon) {
                        Toast.makeText(getActivity(), "Source and destination locations cannot be the same", Toast.LENGTH_SHORT).show();
                    } else if (!(lat >= BOUNDS_INDIA.southwest.latitude && lat <= BOUNDS_INDIA.northeast.latitude) || !(lon >= BOUNDS_INDIA.southwest.longitude && lon <= BOUNDS_INDIA.northeast.longitude)) {
                        Toast.makeText(getActivity(), "Oops! We don't provide our services at the selected location :(", Toast.LENGTH_SHORT).show();
                    } else {
                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
                        transaction.replace(R.id.container, new HomeFragment3(src, new LatLng(lat, lon)));
                        transaction.commit();
                    }
                }
            }
        });

        gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLastLocation();
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if (checkPermission()) {
            if (isLocationEnabled()) {
                fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            lat = location.getLatitude();
                            lon = location.getLongitude();
                            locName = "Your location";
                            supportMapFragment.getMapAsync(callback);
                        }
                    }
                });
            } else {
                Intent intent1 = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent1);
            }
        } else {
            requestPermissions();
        }
    }

    private boolean checkPermission() {
        return ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, 44);
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            lat = mLastLocation.getLatitude();
            lon = mLastLocation.getLongitude();
            locName = "Your location";
            supportMapFragment.getMapAsync(callback);
        }
    };

    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            googleMap.clear();
            if (lat != -0.1 && lon != -0.1) {
                LatLng sydney = new LatLng(lat, lon);
                googleMap.addMarker(new MarkerOptions().position(sydney).title(locName));
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15));
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 44) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gmap=googleMap;

        gmap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                lat=latLng.latitude;
                lon=latLng.longitude;
                locName="";
                supportMapFragment.getMapAsync(callback);
            }
        });
    }
}
