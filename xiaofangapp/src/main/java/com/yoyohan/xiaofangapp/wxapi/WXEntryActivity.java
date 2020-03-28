//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.yoyohan.xiaofangapp.wxapi;

import android.content.Intent;
import android.widget.Toast;

import cn.sharesdk.wechat.utils.WXAppExtendObject;
import cn.sharesdk.wechat.utils.WXMediaMessage;
import cn.sharesdk.wechat.utils.WechatHandlerActivity;

public class WXEntryActivity extends WechatHandlerActivity {
    public WXEntryActivity() {
    }

    public void onGetMessageFromWXReq(WXMediaMessage var1) {
        Intent var2 = this.getPackageManager().getLaunchIntentForPackage(this.getPackageName());
        this.startActivity(var2);
    }

    public void onShowMessageFromWXReq(WXMediaMessage var1) {
        if (var1 != null && var1.mediaObject != null && var1.mediaObject instanceof WXAppExtendObject) {
            WXAppExtendObject var2 = (WXAppExtendObject)var1.mediaObject;
            Toast.makeText(this, var2.extInfo, 0).show();
        }

    }
}
