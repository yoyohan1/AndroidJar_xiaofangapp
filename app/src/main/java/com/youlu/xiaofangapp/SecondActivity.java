package com.youlu.xiaofangapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.alibaba.sdk.android.push.AndroidPopupActivity;

import org.json.JSONObject;

import java.util.Map;

public class SecondActivity extends AndroidPopupActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * 阿里云推送 辅助弹窗指定打开Activity回调
     *
     * @param title    标题
     * @param content  内容
     * @param extraMap 额外参数
     */
    @Override
    protected void onSysNoticeOpened(String title, String content, Map<String, String> extraMap) {
        Log.i("Unity", "阿里云推送  onSysNoticeOpened, title: " + title + ", content: " + content + ", extraMap: " + extraMap);

        Intent intent = new Intent(this, MainActivity.class);
        this.startActivity(intent);
        MainActivity.SendPushToUnity(3, new JSONObject(extraMap), 0);
    }
}

