package com.youlu.xiaofangapp;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.sdk.android.ams.common.logger.AmsLogger;
import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.VivoBadgeReceiver;
import com.alibaba.sdk.android.push.common.util.AppInfoUtil;
import com.alibaba.sdk.android.push.huawei.HuaWeiRegister;
import com.alibaba.sdk.android.push.impl.VivoMsgParseImpl;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.alibaba.sdk.android.push.register.GcmRegister;
import com.alibaba.sdk.android.push.register.MeizuRegister;
import com.alibaba.sdk.android.push.register.MiPushRegister;
import com.alibaba.sdk.android.push.register.OppoRegister;
import com.alibaba.sdk.android.push.register.VivoRegister;
import com.taobao.agoo.BaseNotifyClickActivity;
import com.vivo.push.IPushActionListener;
import com.vivo.push.PushClient;

import org.android.agoo.control.NotifManager;

//因为 接入了ShareSdk 里边已经声明有Application  所以引入ShareSdk的jar包并继承 然后更改AndroidMainfest里的ApplicationName为 com.youlu.xiaofangapp.MainApplication
public class MainApplication extends com.mob.MobApplication {
    @Override
    public void onCreate() {
        super.onCreate();

        Log.i("Unity", "MainApplication启动！");

        initCloudChannel(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            // 通知渠道的id
            String id = "1";
            // 用户可以看到的通知渠道的名字.
            CharSequence name = "消防3D课堂";
            // 用户可以看到的通知渠道的描述
            String description = "消防3D课堂的主要通知";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(id, name, importance);
            // 配置通知渠道的属性
            mChannel.setDescription(description);
            // 设置通知出现时的闪灯（如果 android 设备支持的话）
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            // 设置通知出现时的震动（如果 android 设备支持的话）
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            //最后在notificationmanager中创建该通知渠道
            mNotificationManager.createNotificationChannel(mChannel);
        }

        //注意：辅助通道注册务必在Application中执行且放在推送SDK初始化代码之后，否则可能导致辅助通道注册失败
        // 注册方法会自动判断是否支持小米系统推送，如不支持会跳过注册。
        MiPushRegister.register(this, "2882303761518400394", "5911840035394");
        // 注册方法会自动判断是否支持华为系统推送，如不支持会跳过注册。
        HuaWeiRegister.register(this);
        // OPPO通道注册
        OppoRegister.register(this, "dfa57e9e64ec485aa0b5e0d84cf96258", "c3151be5733442e69c2f6964579165b9"); // appKey/appSecret在OPPO开发者平台获取
        // 魅族通道注册
        MeizuRegister.register(this, "132636", "66510ffbe9e04fe7b161beb7c1a3b569"); // appId/appkey在魅族开发者平台获取

        // VIVO通道注册 奇葩的规则 在vivo手机上MPS:vivo不会打印日志 只能自己把注册方法拿过来 问客服说之后的版本修复
        VivoRegister.register(this);
        
    }

    /**
     * 初始化云推送通道
     *
     * @param applicationContext
     */
    private void initCloudChannel(Context applicationContext) {
        PushServiceFactory.init(applicationContext);
        CloudPushService pushService = PushServiceFactory.getCloudPushService();

        Log.i("Unity", "阿里云推送  deviceID:" + pushService.getDeviceId());

        pushService.register(applicationContext, new CommonCallback() {
            @Override
            public void onSuccess(String response) {
                Log.i("Unity", "阿里云推送 init cloudchannel success");
            }

            @Override
            public void onFailed(String errorCode, String errorMessage) {
                Log.i("Unity", "阿里云推送 init cloudchannel failed -- errorcode:" + errorCode + " -- errorMessage:" + errorMessage);
            }
        });
    }


    private static AmsLogger logger = AmsLogger.getLogger("Unity");
    private static Context mContext;
    private static VivoBadgeReceiver vivoBadgeReceiver;

    public static void register(final Context context) {
        try {
            if (context == null) {
                return;
            }

            mContext = context.getApplicationContext();
            if (!AppInfoUtil.isMainProcess(context)) {
                logger.i("not in main process, return");
                return;
            }

            if (PushClient.getInstance(context).isSupport()) {
                logger.d("register start");
                BaseNotifyClickActivity.addNotifyListener(new VivoMsgParseImpl());
                PushClient.getInstance(context).initialize();
                PushClient.getInstance(context).turnOnPush(new IPushActionListener() {
                    public void onStateChanged(int state) {
                        MainApplication.logger.d("turnOnPush state:" + state);
                        if (state == 0) {
                            String regId = PushClient.getInstance(context).getRegId();
                            MainApplication.logger.d("getRegId  regId:" + regId);
                            if (!TextUtils.isEmpty(regId)) {
                                NotifManager notifyManager = new NotifManager();
                                notifyManager.init(context.getApplicationContext());
                                notifyManager.reportThirdPushToken(regId, "VIVO_TOKEN", "1.0.4", true);
                            }
                        }

                    }
                });
                vivoBadgeReceiver = new VivoBadgeReceiver();
                IntentFilter filter = new IntentFilter();
                filter.addAction("msg.action.ACTION_MPM_MESSAGE_BOX_UNREAD");
                LocalBroadcastManager.getInstance(context).registerReceiver(vivoBadgeReceiver, filter);
            } else {
                logger.e("this device is not support vivo push");
            }
        } catch (Throwable var2) {
            logger.e("register", var2);
        }

    }


}