package com.common.wxalipaydemo;

import android.app.Application;

import common.pay.sdk.CommonPayConfig;

/**
 * User: fee(1176610771@qq.com)
 * Date: 2017-01-10
 * Time: 19:40
 * DESC:
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CommonPayConfig.WX_APP_ID = "wxb4bbf0651d312ab6";
    }
}
