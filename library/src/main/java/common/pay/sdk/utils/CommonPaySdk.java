package common.pay.sdk.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import common.pay.sdk.CommonPayConfig;
import static common.pay.sdk.CommonPayConfig.WX_APP_ID;

/**
 * User: fee(1176610771@qq.com)
 * Date: 2016-07-05
 * Time: 13:31
 * DESC: 支付、付款调用模块
 */
public class CommonPaySdk {
    private IWXAPI iwxapi;
    private Context mContext;
    public CommonPaySdk(Context context) {
        this.mContext = context;
    }
    public void initWxPayModes() {
        iwxapi = WXAPIFactory.createWXAPI(mContext, CommonPayConfig.WX_APP_ID, false);
        iwxapi.registerApp(WX_APP_ID);
    }

    public void handlerWxEvent(Intent intent, IWXAPIEventHandler eventHandler) {
        checkWxSdkApiOk();
        iwxapi.handleIntent(intent, eventHandler);
    }

    /**
     * 调起微信进行支付
     * @param curPayReq
     * @return
     */
    public boolean callWxPay(PayReq curPayReq) {
        checkWxSdkApiOk();
        return iwxapi.sendReq(curPayReq);
    }

    public IWXAPI getIwxapi() {
        if (iwxapi == null) {
            initWxPayModes();
        }
        return iwxapi;
    }

    /**
     * 当前系统内是否安装了微信APP
     * @return
     */
    public boolean isWxAppInstalled() {
        checkWxSdkApiOk();
        return iwxapi.isWXAppInstalled();
    }

    /**
     * 当前微信支付SDK的版本信息
     * @return
     */
    public int wxPaySdkVersion() {
        checkWxSdkApiOk();
        return iwxapi.getWXAppSupportAPI();
    }
    private void checkWxSdkApiOk() {
        if (null == iwxapi) {
            throw new IllegalArgumentException("should invoke initWxPayModes() method first");
        }
    }
    //---------------------------------  down down 支付宝支付业务 down down -----------------------------
    /**
     * 在工作线程中调起阿里支付
     * @param curPayTask
     */
    public void aliPay(AlipayTaskRunner curPayTask) {
        if (curPayTask == null || Util.isEmpty(curPayTask.getAlipayOrderInfo())) {
            return;
        }
        excuteTask(curPayTask);
    }

    public void excuteTask(Runnable anyTask) {
        if (anyTask == null) {
            return;
        }
        if (alipayHandlerThread == null) {
            alipayHandlerThread = new HandlerThread("alipay_task");
            alipayHandlerThread.start();
        }
        Handler handlerInThread = new Handler(alipayHandlerThread.getLooper());
        handlerInThread.post(anyTask);
    }
    private HandlerThread alipayHandlerThread;
    public void endPayModes() {
        if (alipayHandlerThread != null) {
            alipayHandlerThread.quit();
        }
        //is really need??
        if (iwxapi != null) {
//            iwxapi.unregisterApp();
        }
    }
}
