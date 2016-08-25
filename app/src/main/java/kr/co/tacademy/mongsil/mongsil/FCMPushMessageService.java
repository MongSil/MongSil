package kr.co.tacademy.mongsil.mongsil;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.net.URLDecoder;
import java.util.Map;

/*
  실제 FCM을 통한 푸쉬메세지를 받는 곳
 */
public class FCMPushMessageService extends FirebaseMessagingService {

    private static final String TAG = "FCMPushMessageService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (!PropertyManager.getInstance().getAlarm()) {
            return;
        }

        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        if (remoteMessage.getData() != null) {
            //실제 푸쉬로 넘어온 데이터
            Map<String, String> receiveData = remoteMessage.getData();
            try {
                //한글은 반드시 디코딩 해준다.
                sendPushNotification(
                        URLDecoder.decode(receiveData.get("fcmMessage"), "UTF-8"),
                        URLDecoder.decode(receiveData.get("postId"), "UTF-8"),
                        URLDecoder.decode(receiveData.get("replyWriterId"), "UTF-8"),
                        URLDecoder.decode(receiveData.get("replyContent"), "UTF-8")
                        );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     */
    private void sendPushNotification(String message, String postId, String replyWriterId, String replyContent) {
        Intent intent = new Intent(this, SplashActivity.class);
        intent.putExtra("postId", postId);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }
}