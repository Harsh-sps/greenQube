//package com.flixsys.soflix.layout;
//
//
//import android.util.Log;
//
//import com.google.firebase.iid.FirebaseInstanceId;
//import com.google.firebase.iid.FirebaseInstanceIdService;
//
//public class FirebaseIDService extends FirebaseInstanceIdService {
//    private static final String TAG = "FirebaseIDService";
//
//    @Override
//    public void onTokenRefresh() {
//        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
//        Log.d(TAG, "Refreshed token: " + refreshedToken);
//
//        // TODO: Implement this method to send any registration to your app's servers.
//        sendRegistrationToServer(refreshedToken);
//    }
//
//    private void sendRegistrationToServer(String token) {
//        // Add custom implementation, as needed.
//    }
//}
