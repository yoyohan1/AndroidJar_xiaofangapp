package com.youlu.xiaofangapp;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.unity3d.player.UnityPlayer;

public class TuXiaoCaoActivity extends Activity {

    private WebView webView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayShowHomeEnabled(false);
        int color = Color.parseColor("#000000");
        ColorDrawable drawable = new ColorDrawable(color);
        actionBar.setBackgroundDrawable(drawable);

        String phone = this.getIntent().getStringExtra("phone");
        String nickname = this.getIntent().getStringExtra("nickname");
        String avatar = this.getIntent().getStringExtra("avatar");
        String openid = this.getIntent().getStringExtra("openid");

        setContentView(R.layout.activity_tuxiaocao);
        webView = (WebView) findViewById(R.id.tuxiaocao);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);       // 这个要加上

        String url = "https://support.qq.com/product/176457?d-wx-push=1";

        WebViewClient webViewClient = new WebViewClient() {
            /**
             * 拦截 url 跳转,在里边添加点击链接跳转或者操作
             */
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                super.shouldOverrideUrlLoading(view, url);

                if (url == null) {
                    return false;
                }
                try {
                    if (url.startsWith("weixin://")) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        view.getContext().startActivity(intent);
                        return true;//代表告诉系统已经拦截 不需要再做加载了
                    }
                } catch (Exception e) {
                    return false;
                }
                view.loadUrl(url);
                return true;
            }
        };
        webView.setWebViewClient(webViewClient);

        String jixing = android.os.Build.BRAND + "  " + android.os.Build.MODEL;
        String customInfo = "账号:" + phone + " 机型:" + jixing + " 手机版本:Android" + android.os.Build.VERSION.RELEASE;
        String postData = "nickname=" + nickname + "&avatar=" + avatar + "&openid=" + openid + "&customInfo=" + customInfo;

        Log.i("Unity", "打开兔小巢页面 postData:" + postData);
        webView.postUrl(url, postData.getBytes());
    }

    @SuppressLint("ResourceType")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 为ActionBar扩展菜单项
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.layout.tuxiaocao_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.action_exit) {
            this.finish();
            return true;
        } else if (i == R.id.action_back) {
            if (webView.canGoBack()){
                webView.goBack();
            }
            else{
                this.finish();
            }
            return true;
        }else if (i == R.id.action_refersh){
            webView.reload();
            return true;
        }
        else {
            return super.onOptionsItemSelected(item);
        }
    }

}