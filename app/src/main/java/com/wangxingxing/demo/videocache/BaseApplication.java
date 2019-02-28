package com.wangxingxing.demo.videocache;

import android.app.Application;
import android.content.Context;

import com.blankj.utilcode.util.Utils;
import com.danikula.videocache.HttpProxyCacheServer;

public class BaseApplication extends Application {

    private HttpProxyCacheServer proxy;

    @Override
    public void onCreate() {
        super.onCreate();
        Utils.init(this);
    }

    public static HttpProxyCacheServer getProxy(Context context) {
        BaseApplication app = (BaseApplication) context.getApplicationContext();
        return app.proxy == null ? (app.proxy = app.newProxy()) : app.proxy;
    }

    private HttpProxyCacheServer newProxy() {
        return new HttpProxyCacheServer(this);
    }
}
