package com.common.wxalipaydemo;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import common.pay.sdk.BaseActivity;
import common.pay.sdk.CommonPayConfig;
import common.pay.sdk.ComplexPayResp;
import common.pay.sdk.utils.CommonPaySdk;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener,Observer<ComplexPayResp> {

    TextView tvPayResult;
    @Override
    protected int getProvideContentViewResID() {
        return R.layout.activity_main;
    }

    @Override
    protected void initViews() {
        LIFE_CIRCLE_DEBUG = true;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        tvPayResult = findViewById(R.id.tv_show_pay_result);
        CommonPaySdk.getMe().observePayResp(this, this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private static final int TEST_REQUEST_PAY_CODE = 100;
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_wx_pay) {
            TestPay.testWxPay(this,TEST_REQUEST_PAY_CODE);
        }
        else if (id == R.id.nav_alipay) {
            TestPay.testZfbPay(this, TEST_REQUEST_PAY_CODE);
        }
        else if (id == R.id.nav_slideshow) {
            jumpToActivity(WithFragmentActivity.class);
        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 当使用 LiveData的方案来观测支付结果后，旧的使用 onActivityResult()来观测支付结果的方案可以废弃了
        switch (requestCode) {
            case TEST_REQUEST_PAY_CODE:
                String toastHint = "支付模式:%s,响应码:%s,结果描述:%s";
                String payModeDesc = "未知";
                String payRespCode = "unKnow";
                if (data != null) {
                    int payMode = data.getIntExtra(CommonPayConfig.INTENT_KEY_CUR_PAY_MODE, CommonPayConfig.PAY_MODE_WX);
                    payModeDesc = payMode == CommonPayConfig.PAY_MODE_ALIPAY ? "[支付宝]" : "[微信]";
                     payRespCode = data.getStringExtra(CommonPayConfig.INTENT_KEY_REAL_PAY_RESULT_STATUS_CODE);
                }
                String resultDesc = "支付失败";
                switch (resultCode) {
                    case CommonPayConfig.REQ_PAY_RESULT_CODE_OK:
                        resultDesc = "支付成功";
                        break;
                    case CommonPayConfig.REQ_PAY_RESULT_CODE_CANCEL:
                        resultDesc = "支付被取消了";
                        break;
                    case CommonPayConfig.REQ_PAY_RESULT_CODE_NO_WX:
                        resultDesc = "支付失败,未安装微信APP";
                        break;
                    case CommonPayConfig.REQ_PAY_RESULT_CODE_ERROR:
                        resultDesc = "支付失败";
                        break;
                }
                String payResultInfo = "支付模式:" + payModeDesc + "\n" +
                        "支付SDK的实际响应码：" + payRespCode + "\n" +
                        "结果描述：" + resultDesc;
                tvPayResult.setText(payResultInfo);
//                toastShow(String.format(toastHint, payModeDesc, payRespCode, resultDesc));
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CommonPaySdk.getMe().unObservePayResp(this);
    }

    @Override
    public void onChanged(ComplexPayResp complexPayResp) {
        e(null,"--> 支付结果：complexPayResp = ",complexPayResp);
    }
}
