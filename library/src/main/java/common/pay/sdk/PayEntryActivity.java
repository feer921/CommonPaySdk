package common.pay.sdk;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;

import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

import common.pay.sdk.utils.AlipayTaskRunner;
import common.pay.sdk.utils.CommonPaySdk;

import static common.pay.sdk.CommonPayConfig.REQ_PAY_RESULT_CODE_ERROR;

/**
 * User: fee(1176610771@qq.com)
 * Date: 2016-10-31
 * Time: 19:41
 * DESC: 支付入口(界面)Activity,兼容微信以及支付宝支付
 */
public class PayEntryActivity extends BaseActivity implements IWXAPIEventHandler {
    /**
     * 当前能被支付的预付订单信息
     */
    private ICanPayOrderInfo curPayOrderInfo;

    private boolean isAliPayOrder,isWxPayOrder;
    protected CommonPaySdk paySdk;

    /**
     * 注：该启动支付的方法目前只支持(阿里支付宝支付)
     * 微信支付时也可以调用，但由于微信支付SDK对响应回调的WxPayEntryActivity的包路径限制很死，所以本库的PayEntryActivity接收不到
     * 微信支付SDK的响应回调，所以为了通用请直接调用{@linkplain #startPayActivity(Activity, ICanPayOrderInfo, int, Class)}
     * @param activity 发起支付的当前Activity
     * @param curPrePayOrderInfo  当前服务器返回的支付请求信息数据对象
     * @param requestCode 区分请求的请求码
     * @deprecated
     */
    public static void startPayActivity(Activity activity, ICanPayOrderInfo curPrePayOrderInfo, int requestCode) {
        startPayActivity(activity, curPrePayOrderInfo, requestCode, PayEntryActivity.class);
    }

    /**
     * 为了解除微信支付SDK限制死集成微信支付的APP内一定要在包名内下建立一个wxapi包再在该包下建立WxPayEntryActivity类才能正常回调出响应
     * 所以本库改为此方法来调起支付
     * @param startActivity 发起支付的当前Activity
     * @param curPrePayOrderInfo 当前服务器返回的支付请求信息数据对象
     * @param requestCode 区分请求的请求码
     * @param localWxPayEntryActivityClass 即你的APP内的wxapi包下建立的WxPayEntryActivity(该类你什么也不用写就继承PayEntryActivity就行)
     */
    public static void startPayActivity(Activity startActivity, ICanPayOrderInfo curPrePayOrderInfo, int requestCode, Class<? extends PayEntryActivity> localWxPayEntryActivityClass) {
        Intent startIntent = new Intent(startActivity, localWxPayEntryActivityClass);
        startIntent.putExtra(CommonPayConfig.INTENT_KEY_CUR_PAY_ORDER_INFO, curPrePayOrderInfo);
        startActivity.startActivityForResult(startIntent, requestCode);
    }
    /**
     * 该方法供在Fragment界面里跳转支付的情况，这样就能直接在Fragment的onActivityResult()方法中直接拿到支付结果并处理了
     * @param fragment 当前碎片界面
     * @param curPrePayOrderInfo 当前服务器返回的支付请求信息数据对象
     * @param requestCode 区分请求的请求码
     * @param localWxPayEntryActivityClass 即你的APP内的wxapi包下建立的WxPayEntryActivity
     */
    public static void startPayActivity(Fragment fragment, ICanPayOrderInfo curPrePayOrderInfo, int requestCode, Class<? extends PayEntryActivity> localWxPayEntryActivityClass) {
        if (fragment == null) {
            return;
        }
        Intent startIntent = new Intent(fragment.getContext(), localWxPayEntryActivityClass);
        startIntent.putExtra(CommonPayConfig.INTENT_KEY_CUR_PAY_ORDER_INFO, curPrePayOrderInfo);
        fragment.startActivityForResult(startIntent, requestCode);
    }
    /**
     * 为了解除微信支付SDK限制死集成微信支付的APP内一定要在包名内下建立一个wxapi包再在该包下建立WxPayEntryActivity类才能正常回调出响应
     * 所以本库改为此方法来调起支付
     * 该方法不需要传入自己写的WxPayEntryActivity.class参数，方法内会去当前所打包的包下寻找WxPayEntryActivity类,所以更简洁
     * 但是可能兼容性有问题，有些系统可能会报找不到WxPayEntryActivity类的错误，如果发生这样的错误，请换上面两个调用方式。
     * @param startActivity 发起支付的当前Activity
     * @param curPrePayOrderInfo 当前服务器返回的支付请求信息数据对象
     * @param requestCode 区分请求的请求码
     */
    public static void startPayActivity2(Activity startActivity, ICanPayOrderInfo curPrePayOrderInfo, int requestCode) {
        Intent startIntent = getCanHoldWxPayActivityClassIntent(startActivity.getApplicationContext(), curPrePayOrderInfo);
        startActivity.startActivityForResult(startIntent, requestCode);
    }

    /**
     * 但是可能兼容性有问题，有些系统可能会报找不到WxPayEntryActivity类的错误，如果发生这样的错误，请换上面两个调用方式。
     * @param curFragment
     * @param curPrePayOrderInfo
     * @param requestCode
     */
    public static void startPayActivity(Fragment curFragment, ICanPayOrderInfo curPrePayOrderInfo, int requestCode) {
        Intent startIntent = getCanHoldWxPayActivityClassIntent(curFragment.getContext(), curPrePayOrderInfo);
        curFragment.startActivityForResult(startIntent, requestCode);
    }

    private static Intent getCanHoldWxPayActivityClassIntent(Context packageContext,ICanPayOrderInfo curPrePayOrderInfo) {
        Intent startIntent = new Intent();
        String curPackageName = packageContext.getPackageName();
        //采用这种方式的话，就不需要使用者直接传入自己写的WxPayEntryActivity.class参数了，只要在对应的打包包名下的【wxapi】目录下有写这样一个
        //类并且该WxPayEntryActivity继承PayEntryActivity，并且在AndroidMenifest中有注册就行了
        startIntent.setClassName(packageContext, curPackageName + ".wxapi.WXPayEntryActivity");
        startIntent.putExtra(CommonPayConfig.INTENT_KEY_CUR_PAY_ORDER_INFO, curPrePayOrderInfo);
        return startIntent;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LIFE_CIRCLE_DEBUG = true;
        Intent startIntent = getIntent();
        paySdk = new CommonPaySdk(this);
        if (startIntent.hasExtra(CommonPayConfig.INTENT_KEY_CUR_PAY_ORDER_INFO)) {
            curPayOrderInfo = (ICanPayOrderInfo) startIntent.getSerializableExtra(CommonPayConfig.INTENT_KEY_CUR_PAY_ORDER_INFO);
            if (curPayOrderInfo != null) {
                isAliPayOrder = curPayOrderInfo.isAliPayOrder();
                i(null, "-->onCreate() isAliPayOrder = " + isAliPayOrder);
                isWxPayOrder = curPayOrderInfo.isWxPayOrder();
                if (isWxPayOrder) {
                    paySdk.initWxPayModes();
                }
            }
            else{
                toastShow("支付信息错误,请重试");
                finishSelf(true);
                return;
            }
        }
        else{//这种情况为其他地方直接使用微信支付SDK发起了支付请求，而由微信APP间接启动WXPayEntryActivity界面
            isWxPayOrder = true;
            paySdk.initWxPayModes();
            paySdk.handlerWxEvent(startIntent, this);
        }
        super.onCreate(savedInstanceState);
    }

    /**
     * 子类可重写此方法用来提供自己喜欢的过度界面
     * @return
     */
    @Override
    protected int getProvideContentViewResID() {
        return R.layout.def_pay_activity_layout;
    }

    /**
     * 子类可重写此方法用来初始化你自己提供的布局中的视图Views
     */
    @Override
    protected void initViews() {
        //init the views
    }

    @Override
    protected final void initData() {
        super.initData();
        uiHintAgent.toggleHintDialogCancelable(false);
        if (curPayOrderInfo != null) {
            if (isAliPayOrder) {//是支付宝的支付订单
                initHandler();
                AlipayTaskRunner alipayTaskRunner = new AlipayTaskRunner(this, mHandler, curPayOrderInfo.getAlipayInfo());
                alipayTaskRunner.applyPaySdkVersion(AlipayTaskRunner.AlipayVersion.V2);//申请支付宝最新版本V2版本的支付
                paySdk.aliPay(alipayTaskRunner);
                startToPay("");
            }
            else{//微信支付的支付订单
                PayReq wxPayReq = curPayOrderInfo.convert2WxPayReq();
                wxPayReq.appId = CommonPayConfig.WX_APP_ID;//再确认赋值一遍
                if (paySdk.callWxPay(wxPayReq)) {
                    startToPay("");
                }
                else{//不能进行支付
                    int wxPayFailureCode = REQ_PAY_RESULT_CODE_ERROR;
                    String failureHintInfo = "订单支付失败";
                    if (!paySdk.isWxAppInstalled()) {
                        wxPayFailureCode = CommonPayConfig.REQ_PAY_RESULT_CODE_NO_WX;
                        failureHintInfo += ",未安装微信APP.";
                    }
                    setWxPayResult(wxPayFailureCode, wxPayFailureCode + "");
                    payFailure(failureHintInfo);
                }
            }
        }
    }

    /**
     * 之所以需要此方法，是因为本支付界面是主动调起的，所以微信支付APP在本地响应它的支付结果时会调起本界面，而因为本界面已经启动了，所以生命周期会走该方法
     * @param intent
     */
    @Override
    protected final void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        uiHintAgent.setOwnerVisibility(true);
        setIntent(intent);
        if (isWxPayOrder) {
            paySdk.handlerWxEvent(intent,this);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (isWxPayOrder) {
            e(null,"-->onRestart() wx sdk version: " + paySdk.wxPaySdkVersion());
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        switchActivity(true);
    }
    @Override
    public void onReq(BaseReq baseReq) {
        i(null, "--> onReq() baseReq =" + baseReq);
    }

    /**
     * 微信SDK的响应
     * @param resp
     */
    @Override
    public final void onResp(BaseResp resp) {
//        isWxPayEventResponced = true;
        i(null, "-->onResp() errCode = " + resp.errCode + " error info: " + resp.errStr);
        int respType = resp.getType();
        switch (respType){
            case ConstantsAPI.COMMAND_PAY_BY_WX://微信支付的响应类型
                int respCode = resp.errCode;
                uiHintAgent.loadDialogDismiss();
                String payResultHintInfo = "";
                int payResultCode = REQ_PAY_RESULT_CODE_ERROR;
                if (respCode == BaseResp.ErrCode.ERR_OK) {
                    //微信支付成功
                    payResultCode = RESULT_OK;
                }
                else{
                    payResultHintInfo = "订单支付失败";
                    if (respCode == BaseResp.ErrCode.ERR_USER_CANCEL) {//被用户取消了
                        payResultCode = CommonPayConfig.REQ_PAY_RESULT_CODE_CANCEL;
                        payResultHintInfo += ",用户取消支付.";
                    }
                }
//                setResult(payResultCode);
                setWxPayResult(payResultCode, respCode + "");//把微信SDK源生的响应码返回
                if (payResultCode == RESULT_OK) {
                    paySuc();
                }
                else{
                    payFailure(payResultHintInfo);
                }
                break;
        }
    }

    /**
     * 开始去发起支付，显示Loading,
     * 子类可重写此方法，用来根据你自己的布局视图提供你喜欢的开始支付的UI效果，比如开始有一个什么动画之类的
     * @param hintMsg
     */
    protected void startToPay(String hintMsg) {
//        if (Util.isEmpty(hintMsg)) {
//            hintMsg = "正在支付,请稍候...";
//        }
//        sweetLoading(hintMsg);//删除这个loading类，以避免微信支付SDK不响应的坑导致一直loading的问题
        uiHintAgent.sweetNormalHint("提示","是否支付完成？","支付取消","支付完成",DIALOG_IN_CASE_WITHOUT_PAY);
    }


    /**
     * 支付宝的支付结果将在此回调
     * @param msg
     */
    @Override
    protected void handlerMessage(Message msg) {
        super.handlerMessage(msg);
        int msgWhat = msg.what;
        switch (msgWhat) {
            case AlipayTaskRunner.MSG_ALIPAY_RESULT:
//                String alipayResult = (String) msg.obj;
//                e(null, "--> handlerMessage() 支付宝响应结果: alipayResult = " + alipayResult);
                CommonAlipayResult payResult = (CommonAlipayResult) msg.obj;
                e(null, "--> handlerMessage() 支付宝响应结果: alipayResult = " + payResult);
                boolean isPaySuc = payResult.isPayOk();
//                uiHintAgent.loadDialogDismiss();
                uiHintAgent.setOwnerVisibility(true);
                int alipayResultCode = payResult.getResultStatusCode();
                setAliPayResult(isPaySuc ? RESULT_OK : alipayResultCode,payResult.getResultStatus());
                if (isPaySuc) {
                    paySuc();
                }
                else{
                    payFailure();
                }
                break;
        }
    }

    private void paySuc() {
        paySuc("订单支付成功");
    }

    /**
     * 提示支付成功
     * 如果您不喜欢这风格，重写此方法
     * @param successHintInfo
     */
    protected void paySuc(String successHintInfo) {
        sweetDialogHintSuc(successHintInfo, "确定", DIALOG_IN_CASE_PAY_RESULT);
    }

    /**
     * 提示支付失败
     * 如果您不喜欢这风格，重写此方法
     * @param failureHintMsg
     */
    protected void payFailure(String failureHintMsg) {
        sweetDialogHintError(failureHintMsg, "确定", DIALOG_IN_CASE_PAY_RESULT);
    }

    private void payFailure() {
        payFailure("订单支付失败");
    }
    protected static final int DIALOG_IN_CASE_PAY_RESULT = 0x11;
    protected static final int DIALOG_IN_CASE_WITHOUT_PAY = DIALOG_IN_CASE_PAY_RESULT + 1;

    @Override
    protected void onClickInDialog(DialogInterface dialog, int which) {
        int dialogInCase = uiHintAgent.getHintDialogInCase();
        switch (dialogInCase) {
            case DIALOG_IN_CASE_PAY_RESULT:
                finishSelf(true);
                break;
            case DIALOG_IN_CASE_WITHOUT_PAY:
                setPayResult(CommonPayConfig.REQ_PAY_RESULT_CODE_ERROR, isWxPayOrder ? CommonPayConfig.PAY_MODE_WX : CommonPayConfig.PAY_MODE_ALIPAY, "");
                finishSelf(true);
                break;
            default:
                super.onClickInDialog(dialog, which);
                break;
        }
    }

    /**
     * 给Activity的返回设置结果信息
     * @param payReqResultCode 自定义的支付结果码 参见{@linkplain CommonPayConfig#REQ_PAY_RESULT_CODE_CANCEL} so on...
     * @param payMode 当前的支付模式 参见{@linkplain CommonPayConfig#PAY_MODE_WX}
     * @param realPayResultCode 源生的支付SDk响应的支付结果码
     */
    private void setPayResult(int payReqResultCode, int payMode, String realPayResultCode) {
        Intent activeResultIntent = new Intent();
        activeResultIntent.putExtra(CommonPayConfig.INTENT_KEY_CUR_PAY_MODE, payMode);
        activeResultIntent.putExtra(CommonPayConfig.INTENT_KEY_REAL_PAY_RESULT_STATUS_CODE, realPayResultCode);
        setResult(payReqResultCode, activeResultIntent);
    }

    private void setWxPayResult(int payReqResultCode, String wxSdkPayResultCode) {
        setPayResult(payReqResultCode, CommonPayConfig.PAY_MODE_WX, wxSdkPayResultCode);
    }

    private void setAliPayResult(int payReqResultCode, String alipaySdkResultCode) {
        setPayResult(payReqResultCode, CommonPayConfig.PAY_MODE_ALIPAY, alipaySdkResultCode);
    }

    @Override
    public void finish() {
        super.finish();
        paySdk.endPayModes();
    }
}
