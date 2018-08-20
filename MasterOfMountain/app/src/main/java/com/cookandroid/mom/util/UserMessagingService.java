package com.cookandroid.mom.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import com.cookandroid.mom.MainActivity;
import com.cookandroid.mom.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by Shining on 2017-08-01.
 */

public class UserMessagingService extends FirebaseMessagingService {
    private final String tag = "UserMessagingService";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //FCM 서버에서 메세지를 보내면 클라이언트에서 수신했을때 동작하는 메소드.
        /*
        RemoteMessage 객체는 수신한 메세지를 나타내는 객체이며, XMPP 사용시 송신할 메세지의 형식객체(RemoteMessage.Builder클래스로 인스턴스를 생성함)이기도 하다.
        앱서버에서 FCM으로 보내는 JSON메세지의 key에 대응하여 해당값을 리턴하는 메소드를 제공한다.
        특별히 메세지 내용에 해당하는 notification의 값과 data의 값은 JSON객체를 리턴하는게 아니고, 각각 특별한 형식의 객체를 반환한다.
        getData() : Map<String,String>
        getNotification() : RemoteMessage.Notification
        Data는 어플리케이션 개발자가 커스터마이징 할 수 있는 메세지 데이터이므로(key형식과 data형식의 제한이 없다), 어플리케이션에 따라 수신받은 값들을 key,value를 명시적으로 사용할 수 있도록 Map객체로 반환한다.
        Notification은 정해진 알람메세지 및 알람메세지 설정값에 key형식이 있으므로, 내부클래스형식을 제공하여 key에 대응하여 값을 반환하는 메소드를 제공한다.
        Notification.getTitle() : 제목값을 반환한다.
        Notification.getBody() : 알람본문값을 반환한다.
        ...
        등등 메소드들을 제공한다.
        * */
        super.onMessageReceived(remoteMessage);

        /* 일반적인 메세지 처리형식 */
        Log.d(tag, "From : "+remoteMessage.getFrom());

        /* Data 처리 코드*/
        Map<String,String> msgData = remoteMessage.getData();
        //null처리를 안해도 되는걸보니, 없으면 빈 Map객체를 반환하는듯함.
        if(msgData.size()>0){
            Set<String> keySet = msgData.keySet();
            Iterator<String> i = keySet.iterator();

            String key = null;

            while(i.hasNext()){
                key = i.next();
                Log.d(key, msgData.get(key));
            }
        }

        /* Notification 처리코드 */
        RemoteMessage.Notification msgNoti = remoteMessage.getNotification();
        if(msgNoti != null){
            Log.d("title", msgNoti.getTitle());
            Log.d("body", msgNoti.getBody());

            /*
            디바이스 상단 작업표시줄에 뜨는 알람을 구현하기위해선 아래 두 클래스를 생성해야한다.
            Notification : 실제 알람메세지의 제목 및 내용, 기타 설정등의 데이터를 갖는 클래스.
            NotificationManager : 알람을 실제로 디바이스에 출력하고 관리하는 클래스(시스템 서비스를 이용)
            * */

            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),0,intent, PendingIntent.FLAG_UPDATE_CURRENT);
            Resources res = getResources();

            Notification notify =
                    new Notification.Builder(getApplicationContext())
                    .setContentTitle(msgNoti.getTitle())
                    .setContentText(msgNoti.getBody())
                    .setSmallIcon(R.drawable.mom_notify_icon)
                    .setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.ic_launcher))
                    .setContentIntent(pendingIntent)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setVisibility(Notification.VISIBILITY_PUBLIC)
                    .setCategory(Notification.CATEGORY_MESSAGE)
                    .setFullScreenIntent(pendingIntent, true)
                    .build();
            //notify.notify() 로 보이는 메소드는 쓰레드 syncronize에 쓰이는 그 notify()이며, 알람을 보내는 notify가 아니다.
            //contentIntent는 알람을 눌렀을때 실행되는 Activity를 말하는것인가가가가?
            NotificationManager notificationManager;
            notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0,notify);
            //Toast.makeText(UserMessagingService.this,"alarm~",Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }
}
