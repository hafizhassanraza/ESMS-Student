package com.enfotrix.cgscstudent.fcm;

import android.util.Log;

import androidx.annotation.NonNull;


import com.enfotrix.cgscstudent.Common;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
//        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
//            UserUtils.updateToken(this, s);
//        }
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d("onMessageReceived",  "called");
        Map<String, String> dataRec = remoteMessage.getData();
        if (dataRec != null) {


            Common.showNotification(this, new Random().nextInt(),
                    dataRec.get(Common.NOTI_TITLE),
                    dataRec.get(Common.NOTI_CONTENT),
                    null);


        }
    }
}
