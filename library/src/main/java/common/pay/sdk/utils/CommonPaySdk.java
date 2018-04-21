package common.pay.sdk.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
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

    private volatile static CommonPaySdk commonPaySdk;

    public static CommonPaySdk getMe(){
        if (commonPaySdk == null) {
            synchronized (CommonPaySdk.class) {
                if (commonPaySdk == null) {
                    commonPaySdk = new CommonPaySdk();
                }
            }
        }
        return commonPaySdk;
    }

    private CommonPaySdk() {

    }

    public CommonPaySdk withContext(Context context) {
        if (context != null) {
            this.mContext = context.getApplicationContext();
        }
        return this;
    }
//    public CommonPaySdk(Context context) {
//        this.mContext = context;
//    }

    /**
     * 初始化微信API接口模块
     * @return true:通过Wx_APP_ID注册到微信SDK成功; false: other case.
     */
    public boolean initWxPayModes() {
        return initWxPayModes(null);
    }

    public boolean initWxPayModes(Context context) {
        if (mContext == null) {
            mContext = context;
        }
        if (iwxapi == null) {
            iwxapi = WXAPIFactory.createWXAPI(mContext, CommonPayConfig.WX_APP_ID, false);
        }
        return iwxapi.registerApp(WX_APP_ID);
    }
    public boolean handlerWxEvent(Intent intent, IWXAPIEventHandler eventHandler) {
        checkWxSdkApiOk();
        return iwxapi.handleIntent(intent, eventHandler);
    }

    /**
     * 调起微信进行支付
     * @param curPayReq
     * @return
     */
    public boolean callWxPay(PayReq curPayReq) {
        return sendWxReq(curPayReq);
    }

    /**
     * 通过微信SDK发送针对微信的请求信息
     * @param theReq 对于微信的请求
     * @return true:成功：fasle:other case
     */
    public boolean sendWxReq(BaseReq theReq) {
        checkWxSdkApiOk();
        return iwxapi.sendReq(theReq);
    }

    /**
     * 微信向第三方(我们)app请求数据，第三方(我们)app回应数据之后会切回到微信界面
     * @param resp 要响应给微信的数据
     * @return true:成功; false: other case.
     */
    public boolean sendResp2Wx(BaseResp resp) {
        checkWxSdkApiOk();
        return iwxapi.sendResp(resp);
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
