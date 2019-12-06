package com.example.ulsanathelticmatching.alarm;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.ulsanathelticmatching.R;
import com.example.ulsanathelticmatching.main.MainActivity;

public class NotificationHelper extends ContextWrapper {
    // 알람 알리는데 도와주는 클라스
    public static final String channelID = "channelID";
    public static final String channelName = "Channel Name";

    private NotificationManager mManager; //알람창을 띄워주는 객체

    public NotificationHelper(Context base) {
        //안드로이드 버전이 안맞을 경우 처리해주는 메써드
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }
    }

    @TargetApi(Build.VERSION_CODES.O) //안드로이드 버전에 따라 다르게 처리를 함
    private void createChannel() {
        NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH);

        getManager().createNotificationChannel(channel);
    }

    public NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mManager;
    }

    Uri defaultSoundUri= RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_NOTIFICATION);

    public NotificationCompat.Builder getChannelNotification() {
        //알람 객체의 화면과 설정을 하는 메써드
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        return new NotificationCompat.Builder(getApplicationContext(), channelID)
                .setSound(defaultSoundUri)
                .setContentTitle("알람 시간입니다!")
                .setContentText("시간 약속을 지키지 않는다면 다른 사용자에게 피해를 끼칠 수 있어요ㅠㅠ")
                .setSmallIcon(R.drawable.logo)//알람에 아이콘을 넣음
                .setContentIntent(pendingIntent);//알람의 인텐트를 달아 알람을 누르면 해당 엑티비티로 넘어가게 된다
    }
}