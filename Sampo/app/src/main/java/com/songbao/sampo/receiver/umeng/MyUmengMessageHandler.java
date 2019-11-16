package com.songbao.sampo.receiver.umeng;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.songbao.sampo.AppConfig;
import com.songbao.sampo.utils.LogUtil;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.entity.UMessage;

public class MyUmengMessageHandler extends UmengMessageHandler {

    @Override
    public void dealWithCustomMessage(Context context, UMessage uMessage) {
        super.dealWithCustomMessage(context, uMessage);
        LogUtil.i("PushManager", "自定义消息 dealWithCustomMessage " + uMessage.custom);
        int msgType = Integer.valueOf(uMessage.custom);
        Intent intent = new Intent();
        switch (msgType) {
            case AppConfig.PUSH_MSG_TYPE_001: //刷新预约核销码
                intent.setAction(AppConfig.RA_PAGE_RESERVE);
                intent.putExtra(AppConfig.RA_PAGE_RESERVE_KEY, AppConfig.PUSH_MSG_TYPE_001);
                context.sendBroadcast(intent);
                break;
        }
        LogUtil.i("PushManager", "PushManager sendBroadcast msgType = " + msgType);
    }

    @Override
    public Notification getNotification(Context context, UMessage msg) {
        LogUtil.i("PushManager", "自定义通知 getNotification " + msg.custom);
        switch (msg.builder_id) {
            case 1:
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
                /*RemoteViews myNotificationView = new RemoteViews(context.getPackageName(), R.layout.notification_view);
                myNotificationView.setTextViewText(R.id.notification_title, msg.title);
                myNotificationView.setTextViewText(R.id.notification_text, msg.text);
                myNotificationView.setImageViewBitmap(R.id.notification_large_icon, getLargeIcon(context, msg));
                myNotificationView.setImageViewResource(R.id.notification_small_icon, getSmallIconId(context, msg));
                builder.setContent(myNotificationView)
                        .setSmallIcon(getSmallIconId(context, msg))
                        .setTicker(msg.ticker)
                        .setAutoCancel(true);*/
                return builder.build();
            default:
                //默认为0，若填写的builder_id并不存在，也使用默认。
                return super.getNotification(context, msg);
        }
    }

}
