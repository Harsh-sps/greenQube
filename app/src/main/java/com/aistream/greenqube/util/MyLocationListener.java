package com.aistream.greenqube.util;

import android.location.Location;
import android.os.Bundle;

public interface MyLocationListener {
    void updateLocation(Location location);

    void updateStatus(String provider, int status, Bundle extras);
}
