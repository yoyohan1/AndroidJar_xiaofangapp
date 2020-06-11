package com.youlu.xiaofangapp;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Region;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.view.DisplayCutout;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;

import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.unity3d.player.UnityPlayer;
import com.unity3d.player.UnityPlayerActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public static void SendMessageToUnity(int requestId, String msg, int code) {
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

        final UnityPlayer mUnityPlayer_final = this.mUnityPlayer;
        final Activity this_final = this;
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                this_final.getWindow().clearFlags(1024);
                mUnityPlayer_final.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);

                //this_final.getWindow().clearFlags(2048);
//                if (Build.VERSION.SDK_INT >= 29) {
//                } else {
//                    this_final.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, -1);
//                }

            }
        });

    }

    public void HideStatusBar() {
        Log.i("Unity", "HideStatusBar!   当前NotchState：" + NotchState);

        final UnityPlayer mUnityPlayer_final = this.mUnityPlayer;
        final Activity this_final = this;
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                this_final.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                mUnityPlayer_final.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

//                if (Build.VERSION.SDK_INT >= 29) {
//                } else {
//                    this_final.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN, -1);
//                }
                //this_final.getWindow().addFlags(2048);

            }
        });

    }


    public long GetFreeDiskSpace() {
        //8.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.i("Unity", "使用安卓8.0以上的处理方法------------------------------");
            long size = StorageQueryUtil.queryWithStorageManager(mUnityPlayer.getContext());
            Log.i("Unity", "获取到容量StorageQueryUtil.queryWithStorageManager:" + size);
            if (size == -1) {
                return size;
            }
            long size2 = size / (1000 * 1000);
            long size3 = (long) (size * 1.0 / (1024 * 1024));
            Log.i("Unity", "获取到容量StorageQueryUtil.queryWithStorageManager 除以1000*1000:" + size2);
            Log.i("Unity", "获取到容量StorageQueryUtil.queryWithStorageManager 除以1024*1024:" + size3);
            return size2;
        } else {
            try {
                File file = Environment.getDataDirectory();
                StatFs sf = new StatFs(file.getPath());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    Log.i("Unity", "GetFreeDiskSpace !   设备大于Android4.3 获取结果：" + (long) (sf.getAvailableBytes() * 1.0 / (1024 * 1024)) + "  Total:" + sf.getTotalBytes() / (1024 * 1024) + "  Free:" + (long) (sf.getFreeBytes() * 1.0 / (1024 * 1024)));
                    return (long) (sf.getAvailableBytes() * 1.0 / (1024 * 1024));
                } else {
                    StatFs statFs = new StatFs(Environment.getDataDirectory().getPath());
                    //存储块
                    long blockCount = statFs.getBlockCount();
                    //块大小
                    long blockSize = statFs.getBlockSize();
                    //可用块数量
                    long availableCount = statFs.getAvailableBlocks();
                    //剩余块数量，注：这个包含保留块（including reserved blocks）即应用无法使用的空间
                    long freeBlocks = statFs.getFreeBlocks();

                    long size = (long) (blockSize * availableCount * 1.0 / (1024 * 1024));
                    Log.i("Unity", "GetFreeDiskSpace !   设备小于Android4.3 获取结果：" + size);
                    return size;
                }
            } catch (Throwable e) {
                Log.i("Unity", "GetFreeDiskSpace Error!   " + e.getLocalizedMessage());
            }
            return -1;
        }
    }

    public static List<Map<String, Object>> lisNoSendMsg = new ArrayList<>();
    public static String noSendNotification = "";

    public String getLisNoSendMsg() {
        Log.i("Unity", "开始getLisNoSendMsg");
        JSONArray jsonArray = new JSONArray(lisNoSendMsg);
        lisNoSendMsg.clear();
        Log.i("Unity", "getLisNoSendMsg结果为：" + jsonArray.toString());
        return jsonArray.toString();
    }

    public String getNoSendNotification() {
        Log.i("Unity", "开始getNoSendNotification");
        String returnStr = noSendNotification;
        noSendNotification = "";
        Log.i("Unity", "getNoSendNotification结果为：" + returnStr);
        return returnStr;
    }


    public static void SendPushToUnity(int requestId, JSONObject jsonObject, int code) {
        if (UnityPlayer.currentActivity == null) {
            Log.i("Unity", "无法SendPushToUnity！Activity没启动！把数据存储一下！");

            //未发送给Unity的通知
            if (requestId == 3) {
                noSendNotification = jsonObject.toString();
            }
            //未发送给Unity的消息
            else if (requestId == 4) {
                Map<String, Object> map = new HashMap<>();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                map.put("date", formatter.format(new Date()));
                map.put("requestId", requestId);
                map.put("msg", jsonObject);
                map.put("code", code);
                lisNoSendMsg.add(map);
            }

        } else {
            SendMessageToUnity(requestId, jsonObject.toString(), code);
        }
    }


}
