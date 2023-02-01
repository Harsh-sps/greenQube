package com.aistream.greenqube.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.List;

public class LocationHelper implements LocationListener{
    private static String TAG = LocationHelper.class.getSimpleName();


    private static final int REQUEST_PRESSMION_CODE = 10000;
    public static final int REQUEST_LOCATION_CODE = 10001;
    private final static String[] MULTI_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 1000;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    private LocationManager locationManager;
    /**
     * Provides access to the Fused Location Provider API.
     */
    private FusedLocationProviderClient mFusedLocationClient;
    /**
     * Provides access to the Location Settings API.
     */
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    /**
     * Stores the types of location services the client is interested in using. Used for checking
     * settings to determine if the device has optimal location settings.
     */
    private LocationSettingsRequest mLocationSettingsRequest;

    private MyLocationListener mListener;

    private Context mContext;
    private Activity mActivity;
    public LocationHelper(Activity activity) {
        this.mActivity = activity;
        this.mContext = activity.getApplicationContext();
        locationManager = (LocationManager) this.mContext.getSystemService(Context.LOCATION_SERVICE);
//        if (isGooglePlayServicesAvailable()) {
//            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this.mContext);
//            mSettingsClient = LocationServices.getSettingsClient(this.mContext);
//
//            createLocationRequest();
//            buildLocationSettingsRequest();
//        }
        Log.d(TAG, "mFusedLocationClient: "+mFusedLocationClient+", locationManager: "+locationManager);
    }

    private boolean isGooglePlayServicesAvailable(){
        int googlePlayServicesAvailable = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(mContext);
        Log.d(TAG, "googlePlayServicesAvailable: "+googlePlayServicesAvailable);
        switch (googlePlayServicesAvailable){
            case ConnectionResult.SUCCESS:
                Log.d(TAG, "support google play service platform, use google service to get location");
                return true;
            default:
                Log.d(TAG, "not support google play service platform, use android native api to get location");
                return false;
        }
    }

    private LocationCallback mLocationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            Location location = locationResult.getLastLocation();
            Log.d(TAG, "mLocationCallback location: "+location);
            if (location != null) {
                mListener.updateLocation(location);
            }
        }
    };

    /**
     * Sets up the location request. Android has two location request settings:
     * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
     * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     * <p/>
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     * <p/>
     * These settings are appropriate for mapping applications that show real-time location
     * updates.
     */
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Uses a {@link com.google.android.gms.location.LocationSettingsRequest.Builder} to build
     * a {@link com.google.android.gms.location.LocationSettingsRequest} that is used for checking
     * if a device has the needed location settings.
     */
    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    /**
     * Requests location updates from the FusedLocationApi. Note: we don't call this unless location
     * runtime permission has been granted.
     */
    private void startLocationUpdates() {
        final Context ctx = mContext;
        // Begin by checking if the device has the necessary location settings.
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(mActivity, new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i(TAG, "All location settings are satisfied.");
                        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                                ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        Log.i(TAG, "mFusedLocationClient requestLocationUpdates.");
                        //noinspection MissingPermission
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());
                    }
                })
                .addOnFailureListener(mActivity, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);
                        }
                    }
                });
    }



    /**
     * initial location listener
     * @param mListener
     */
    public void setLocationListener(final MyLocationListener mListener) {
        if (mListener == null) {
            return;
        }
        this.mListener = mListener;
        if (ActivityCompat.checkSelfPermission(this.mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this.mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.mActivity, MULTI_PERMISSIONS, REQUEST_PRESSMION_CODE);
            return;
        }

        if (mFusedLocationClient != null) {
            mFusedLocationClient.getLastLocation()
                    .addOnCompleteListener(mActivity, new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if (task.isSuccessful()) {
                                Location location = task.getResult();
                                Log.d(TAG, "setUpLicationListener location: "+location);
                                if (location != null) {
                                    Log.d(TAG, "current location logitude: "+location.getLongitude()+", latitude: "+location.getLatitude());
                                    mListener.updateLocation(task.getResult());
                                } else {
                                    startLocationUpdates();
                                }
                            } else {
                                Log.w(TAG, "getLastLocation:exception", task.getException());
                            }
                        }
                    });
        } else {
            Location location = null;
            List<String> providers = locationManager.getProviders(true);
            String best_provider = LocationManager.NETWORK_PROVIDER;
            for (String provider : providers) {
                Location l = locationManager.getLastKnownLocation(provider);
                Log.d(TAG, "check provider: "+provider+", location: "+l);
                if (l == null) {
                    continue;
                }
                if(location == null || location.getAccuracy() < l.getAccuracy()) {
                    location = l;
                    best_provider = provider;
                }
            }
            Log.d(TAG, "setUpLicationListener best provider: "+best_provider+", location: "+location);
            if (location != null) {
                Log.d(TAG, "current location logitude: "+location.getLongitude()+", latitude: "+location.getLatitude());
                mListener.updateLocation(location);
            }
            //add location listener
            locationManager.requestLocationUpdates(best_provider, 100, 10, this);
        }
    }

    /**
     * remove location update listener
     */
    public void removeLocationUpdatesListener() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }

        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (mListener != null) {
            mListener.updateLocation(location);
            Log.i(TAG, "Time：" + location.getTime());
            Log.i(TAG, "Longitude：" + location.getLongitude());
            Log.i(TAG, "Latitude：" + location.getLatitude());
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        if (mListener != null) {
            mListener.updateStatus(provider, status, extras);
        }
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d(TAG, "onProviderEnabled: " + provider);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d(TAG, "onProviderDisabled: " + provider);
    }

    /**
     * check location whether enabled
     * @return
     */
    public boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                    || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    /**
     * open gps setting
     * @param activity
     */
    public void openLocationSetting(Activity activity) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            Intent intent = new Intent(
                    Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            activity.startActivityForResult(intent, REQUEST_LOCATION_CODE);
        }
    }
}
