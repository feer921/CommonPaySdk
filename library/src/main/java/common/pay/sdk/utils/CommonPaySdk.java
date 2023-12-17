package common.pay.sdk.utils;

import static common.pay.sdk.CommonPayConfig.WX_APP_ID;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import common.pay.sdk.CommonPayConfig;
import common.pay.sdk.ComplexPayResp;

/**
 * User: fee(1176610771@qq.com)
 * Date: 2016-07-05
 * Time: 13:31
 * DESC: 支付、付款调用模块
 */
public class CommonPaySdk {
    private IWXAPI iwxapi;
    /**
     * Application 类型的  Context
     */
    private Context mAppContext;

    private volatile static CommonPaySdk commonPaySdk;

    /**
     * 支付宝支付任务的执行者
     */
    private ExecutorService mTaskExecutorService;

    /**
     * MutableLiveData类型的 支付结果响应
     */
    private MutableLiveData<ComplexPayResp> mPayResultLiveData;

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
            this.mAppContext = context.getApplicationContext();
        }
        return this;
    }

    /**
     * 初始化微信API接口模块
     * @return true:通过Wx_APP_ID注册到微信SDK成功; false: other case.
     */
    public boolean initWxPayModes() {
        return initWxPayModes(null);
    }

    public boolean initWxPayModes(Context context) {
        if (mAppContext == null && context != null) {
            mAppContext = context.getApplicationContext();
        }
        if (mAppContext == null) {
            return false;
        }
        if (iwxapi == null) {
            iwxapi = WXAPIFactory.createWXAPI(mAppContext, CommonPayConfig.WX_APP_ID, false);
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
    //--------------------------------- 支付宝支付业务 @start -----------------------------

    /**
     * 外部提供当前项目的公共的 线程池实例，以复用线程池
     * @param theExecutorService ExecutorService
     * @return CommonPaySdk self
     */
    public CommonPaySdk withTaskExecutorService(ExecutorService theExecutorService){
        if (theExecutorService != null) {
            mTaskExecutorService = theExecutorService;
        }
        return this;
    }

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
        initTaskExecutor();
        if (mTaskExecutorService != null) {
            mTaskExecutorService.execute(anyTask);
        }
    }

    private void initTaskExecutor(){
        if (mTaskExecutorService == null) {
            mTaskExecutorService = Executors.newSingleThreadExecutor();
        }
    }

    public void endPayModes() {
        if (mPayResultLiveData != null) {
            mPayResultLiveData.setValue(null);
        }
        release();
    }

    public void release(){
        if (mTaskExecutorService != null) {
            mTaskExecutorService.shutdown();
        }
        mTaskExecutorService = null;
    }

    /**
     * 观测/监听 支付响应
     * @param lifecycleOwner LifecycleOwner
     * @param observer 支付结果响应的 观察者
     */
    public void observePayResp(@NonNull LifecycleOwner lifecycleOwner, @NonNull Observer<ComplexPayResp> observer){
        if (mPayResultLiveData == null) {
            mPayResultLiveData = new MutableLiveData<>();
        }
        mPayResultLiveData.observe(lifecycleOwner,observer);
    }

    /**
     * 不再观测/监听 支付结果响应
     * @param observer Observer<ComplexPayResp>
     */
    public void unObservePayResp(@NonNull Observer<ComplexPayResp> observer){
        if (mPayResultLiveData != null && observer != null) {
            mPayResultLiveData.removeObserver(observer);
        }
    }

    /**
     * 通用的处理当前的支付响应
     * @param payMode 当前的支付模式/方式 （微信、支付宝）
     * @param respCode 当前的响应码
     * @param paySdkRespCode 当前的支付SDK的实际响应码
     * @param respMsg 当前的支付响应消息
     * @return true: 推送了支付响应结果信息；false: 未推送支付结果信息(无支付结果观测者)
     */
    public boolean handlePayResp(int payMode,int respCode,String paySdkRespCode,@Nullable String respMsg){
        if (mPayResultLiveData == null) {
            return false;
        }
        ComplexPayResp payResp = new ComplexPayResp();
        payResp.payMode = payMode;
        payResp.respCode = respCode;
        payResp.paySdkRespCode = paySdkRespCode;
        payResp.msg = respMsg;
        mPayResultLiveData.setValue(payResp);
        return true;
    }

    public void resetResultLiveData(){
        if (mPayResultLiveData != null) {
            mPayResultLiveData.setValue(null);
        }
    }
}
