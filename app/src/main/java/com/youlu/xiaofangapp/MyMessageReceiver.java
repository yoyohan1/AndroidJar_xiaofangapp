package com.youlu.xiaofangapp;

import android.content.Context;
import android.util.Log;

import com.alibaba.sdk.android.push.MessageReceiver;
import com.alibaba.sdk.android.push.notification.CPushMessage;
import com.unity3d.player.UnityPlayer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MyMessageReceiver extends MessageReceiver {

    /*
     * 关于通知和消息的区别：
     * 通知：发送后会在系统通知栏收到展现，同时响铃或振动提醒用户。
     * 消息：以透传的形式传递给客户端，无显示，发送后不会在系统通知栏展现，第三方应用后需要开发者写代码才能看到。
     * */

    @Override
    public void onNotification(Context context, String title, String summary, Map<String, String> extraMap) {
        //接收到推送的通知
        Log.i("Unity", "阿里云推送 Receive notification, title: " + title + ", summary: " + summary + ", extraMap: " + extraMap);
    }

    @Override
    public void onMessage(Context context, CPushMessage cPushMessage) {
        //接收到推送的消息 应用启动和未启动都能接收到 但是未启动不能做逻辑处理 可以保存信息等打开客户端再做处理
        Log.i("Unity", "阿里云推送 onMessage, messageId: " + cPushMessage.getMessageId() + ", title: " + cPushMessage.getTitle() + ", content:" + cPushMessage.getContent());

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("title", cPushMessage.getTitle());
            jsonObject.put("content", cPushMessage.getContent());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        MainActivity.SendPushToUnity(4, jsonObject, 0);
    }

    @Override
    public void onNotificationOpened(Context context, String title, String summary, String extraMap) {
        //推送的通知被点击打开
        Log.i("Unity", "阿里云推送 onNotificationOpened, title: " + title + ", summary: " + summary + ", extraMap:" + extraMap);
        try {
            MainActivity.SendPushToUnity(3, new JSONObject(extraMap), 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onNotificationClickedWithNoAction(Context context, String title, String summary, String extraMap) {
        Log.i("Unity", "阿里云推送 onNotificationClickedWithNoAction, title: " + title + ", summary: " + summary + ", extraMap:" + extraMap);
    }

    @Override
    protected void onNotificationReceivedInApp(Context context, String title, String summary, Map<String, String> extraMap, int openType, String openActivity, String openUrl) {
        Log.i("Unity", "阿里云推送 onNotificationReceivedInApp, title: " + title + ", summary: " + summary + ", extraMap:" + extraMap + ", openType:" + openType + ", openActivity:" + openActivity + ", openUrl:" + openUrl);
    }

    @Override
    protected void onNotificationRemoved(Context context, String messageId) {
        Log.i("Unity", "阿里云推送 onNotificationRemoved");
    }

}


