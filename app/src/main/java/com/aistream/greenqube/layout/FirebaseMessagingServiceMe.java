//package com.flixsys.soflix.layout;
//
//import android.app.Notification;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.BitmapFactory;
//import android.graphics.Color;
//import android.media.RingtoneManager;
//import android.net.Uri;
//import android.support.v4.app.NotificationCompat;
//import android.util.Log;
//
//import com.flixsys.soflix.LoginActivity;
//import com.flixsys.soflix.LoginActivity;
//import com.flixsys.soflix.R;
//import com.flixsys.soflix.activity.MainActivity;
//import com.google.firebase.messaging.FirebaseMessagingService;
//import com.google.firebase.messaging.RemoteMessage;
//
//import java.util.Map;
//
//public class FirebaseMessagingServiceMe extends FirebaseMessagingService {
//    private static final String TAG = "FCMService";
//
//    @Override
//    public void onMessageReceived(RemoteMessage remoteMessage) {
////        super.onMessageReceived(remoteMessage);
//        Log.d(TAG, "From: " + remoteMessage.getFrom());
//        Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());
//        // Check if message contains a data payload.
//        if (remoteMessage.getData().size() > 0) {
//            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
//        }
//// Check if message contains a notification payload.
//        if (remoteMessage.getNotification() != null) {
//            String title = remoteMessage.getNotification().getTitle(); //get title
//            String message = remoteMessage.getNotification().getBody(); //get message
//            Log.d(TAG, "Message Notification Title: " + title);
//            Log.d(TAG, "Message Notification Body: " + message);
////            sendNotification(title, message);
////            sendNotification(remoteMessage);
//        }
//
////        RemoteMessage.Notification notification = remoteMessage.getNotification();
////        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
////        notificationManager.notify(0, notification);
//////        notification.
////        Map<String, String> data = remoteMessage.getData();
////        ShowNotification(notification, data);
//    }
//
//    @Override
//    public void onNewToken(String s) {
//        super.onNewToken(s);
//        Log.i(TAG, "onNewToken - " + s);
//        sendRegistrationToServer(s);
//    }
//
//    private void sendRegistrationToServer(String token) {
////        final FirebaseDatabase database = FirebaseDatabase.getInstance();
////        DatabaseReference ref = database.getReference("server/saving-data/IDs");
////        // then store your token ID
////        ref.push().setvalue(token);
//    }
//
//    private void ShowNotification(RemoteMessage.Notification notification, Map<String, String> data) {
//
////        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round);
//
//        Intent intent = new Intent(this, MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
//        Uri sound = Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/raw/notification");
//
//
//       NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(),"6")
//                .setContentTitle(data.get("title"))
//                .setContentText(data.get("text"))
//                .setAutoCancel(true)
//                .setSound(sound)
//                .setContentIntent(pendingIntent)
//                .setContentInfo("ANY")
////                .setLargeIcon(R.drawable.ic_launcher_app)
//                .setColor(Color.RED)
//                .setSmallIcon(R.mipmap.ic_launcher);
//
//
//        notificationBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
//        notificationBuilder.setLights(Color.YELLOW, 1000, 300);
//
//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.notify(0, notificationBuilder.build());
//    }
//
//    private void sendNotification(RemoteMessage remoteMessage) {
//        RemoteMessage.Notification notification = remoteMessage.getNotification();
//        Intent intent = new Intent(this, LoginActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
//                PendingIntent.FLAG_ONE_SHOT);
//
//        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
//                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.ic_launcher_app))
//                .setSmallIcon(R.drawable.ic_launcher_app)
//                .setContentTitle(notification.getTitle())
//                .setContentText(notification.getBody())
//                .setAutoCancel(true)
//                .setSound(defaultSoundUri)
//                .setContentIntent(pendingIntent);
//
//        NotificationManager notificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        notificationManager.notify(0, notificationBuilder.build());
//    }
//
//}
