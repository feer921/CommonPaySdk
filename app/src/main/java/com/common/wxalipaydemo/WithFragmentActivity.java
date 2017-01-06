package com.common.wxalipaydemo;

import android.content.Intent;
import android.util.Log;

import common.pay.sdk.BaseActivity;

/**
 * User: fee(1176610771@qq.com)
 * Date: 2017-01-06
 * Time: 18:59
 * DESC:
 */
public class WithFragmentActivity extends BaseActivity {

    /**
     * 获取当前Activity需要填充、展示的内容视图，如果各子类提供，则由基类来填充，如果不提供，各子类也可自行处理
     *
     * @return 当前Activity需要展示的内容视图资源ID
     */
    @Override
    protected int getProvideContentViewResID() {
        return R.layout.test_fragment_activity_layout;
    }

    /**
     * 如果子类在getProvideContentViewResID()方法提供了视图资源，那么子类的初始化视图可在此方法中完成
     */
    @Override
    protected void initViews() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new WithPayFragment()).commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("info","WithFragmentActivity----> onActivityResult() requestCode = " + requestCode + " resultCode = " + resultCode
        );
    }
}
