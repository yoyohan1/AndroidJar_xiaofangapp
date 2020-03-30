package com.youlu.xiaofangapp;

import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.DisplayCutout;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;

import com.unity3d.player.UnityPlayer;
import com.unity3d.player.UnityPlayerActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class MainActivity extends UnityPlayerActivity {
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        Log.i("Unity", "----------------------------------xiaofangapp  onCreate!");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            try {
                Window window = getWindow();
                //设置页面全屏显示 设置成这种模式才能获取AndroidP的全面屏尺寸
                WindowManager.LayoutParams lp = window.getAttributes();
                lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
                //设置页面延伸到刘海区显示
                window.setAttributes(lp);
                Log.i("Unity", "----------------------------------xiaofangapp设置页面延伸到刘海区显示!");
            } catch (Exception e) {
                Log.i("Unity", "----------------------------------xiaofangapp not support layoutInDisplayCutoutMode!");
            }
        } else {
            Log.i("Unity", "----------------------------------xiaofangapp not support layoutInDisplayCutoutMode!");
        }
    }


    /**
     * code 0成功,1失败,2其他状态
     * requestId 发送的请求ID Unity根据此值判断做出响应
     * msg 消息体 Json格式
     */
    public void SendMessageToUnity(int requestId, String msg, int code) {
        Log.i("Unity", "SendMessageToUnity调用成功！requestId:" + requestId + " msg:" + msg + " code:" + code);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("requestId", requestId);
            jsonObject.put("msg", msg);
            jsonObject.put("code", code);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        UnityPlayer.UnitySendMessage("YouDaSdk", "OnOperationResponce", jsonObject.toString());
    }


    private int NotchState = 0;// 0AndroidO  1AndroidP全面屏  2AndroidP非全面屏

    public void AutoSetStatusBar() {
        //安卓10.0以上 状态栏的黑边背景调不出来。所以适配AndroidQ的非刘海屏手机 开启全屏模式。刘海屏手机开启状态栏黑边模式
        if (Build.VERSION.SDK_INT >= 29) {
            UnityPlayer.currentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    WindowInsets rootWindowInsets = UnityPlayer.currentActivity.getWindow().getDecorView().getRootWindowInsets();
                    if (rootWindowInsets == null) {
                        Log.i("Unity", "rootWindowInsets为空了!");
                        setStatusBar(2);
                        return;
                    }

                    DisplayCutout displayCutout = rootWindowInsets.getDisplayCutout();
                    if (null == displayCutout) {
                        Log.i("Unity", "displayCutout为空了!");
                        setStatusBar(2);
                        return;
                    }

                    Log.i("Unity", "安全区域距离屏幕左边的距离 SafeInsetLeft:" + displayCutout.getSafeInsetLeft());
                    Log.i("Unity", "安全区域距离屏幕右部的距离 SafeInsetRight:" + displayCutout.getSafeInsetRight());
                    Log.i("Unity", "安全区域距离屏幕顶部的距离 SafeInsetTop:" + displayCutout.getSafeInsetTop());
                    Log.i("Unity", "安全区域距离屏幕底部的距离 SafeInsetBottom:" + displayCutout.getSafeInsetBottom());

                    List<Rect> rects = displayCutout.getBoundingRects();
                    if (rects == null || rects.size() == 0) {
                        Log.i("Unity", "不是刘海屏！");
                        setStatusBar(2);
                    } else {
                        Log.i("Unity", "刘海屏数量:" + rects.size());
                        for (Rect rect : rects) {
                            Log.i("Unity", "刘海屏区域：" + rect);
                        }
                        Log.i("Unity", "是刘海屏！");
                        setStatusBar(1);
                        //SendMessageToUnity(3, displayCutout.getSafeInsetTop() + "", 0);
                    }
                }
            });


        } else {
            setStatusBar(0);
        }
    }


    private void setStatusBar(int code) {
        NotchState = code;
        ShowStatusBar();
    }

    public void ShowStatusBar() {
        Log.i("Unity", "ShowStatusBar!   当前NotchState：" + NotchState);

        this.getWindow().clearFlags(1024);
        this.mUnityPlayer.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }
    
    public void HideStatusBar() {
        Log.i("Unity", "HideStatusBar!   当前NotchState：" + NotchState);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.mUnityPlayer.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }


}
