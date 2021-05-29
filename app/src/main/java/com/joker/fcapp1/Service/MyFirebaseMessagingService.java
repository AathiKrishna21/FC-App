package com.joker.fcapp1.Service;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.navigation.NavDeepLinkBuilder;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdReceiver;
import com.google.firebase.iid.internal.FirebaseInstanceIdInternal;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.joker.fcapp1.Helper.NotificationHelper;
import com.joker.fcapp1.Main2Activity;
import com.joker.fcapp1.Menu_trial;
import com.joker.fcapp1.Model.Token;
import com.joker.fcapp1.SignInActivity;
import com.joker.fcapp1.ui.home.HomeFragment;
import com.joker.fcapp1.ui.orders.OrdersFragment;

import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        FirebaseMessaging.getInstance().getToken()
               .addOnCompleteListener(new OnCompleteListener<String>() {
                   @Override
                   public void onComplete(@NonNull Task<String> task) {
                       String token=task.getResult();
                       if(FirebaseAuth.getInstance().getCurrentUser()!=null)
                            updateTokenToFirebase(token);
                   }
               });

    }

    private void updateTokenToFirebase(String token) {
        FirebaseDatabase db=FirebaseDatabase.getInstance();
        DatabaseReference tokenref=db.getReference("Tokens");
        Token tokenobj= new Token(token,"false");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userKey = user.getUid();
        tokenref.child(userKey).setValue(tokenobj);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
            sendNotificationAPI26(remoteMessage);
    }

    private void sendNotificationAPI26(RemoteMessage remoteMessage) {
        RemoteMessage.Notification notification=remoteMessage.getNotification();
        String title=notification.getTitle();
        String content=notification.getBody();
        Intent intent=new Intent(this, Main2Activity.class);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userKey = user.getUid();
        intent.putExtra("uid",userKey);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationHelper helper =new NotificationHelper(this);
        Notification.Builder builder=helper.getFCAppChannelNotification(title,content,pendingIntent,defaultSoundUri);

        helper.getManager().notify(new Random().nextInt(),builder.build());
    }

}
