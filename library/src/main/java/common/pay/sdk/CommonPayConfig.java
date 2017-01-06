package common.pay.sdk;

import android.app.Activity;

/**
 * User: fee(1176610771@qq.com)
 * Date: 2016-10-31
 * Time: 19:43
 * DESC:
 */
public class CommonPayConfig {
    /**
     * 使用本通用支付库时，各APP需要赋值该变量为在微信公众平台所分配给APP的APP ID
     */
    public static String WX_APP_ID = "";

    //Intent意图中的Keys

    /**
     * 当前所携带的支付订单信息Key
     */
    public static final String INTENT_KEY_CUR_PAY_ORDER_INFO = "cur_pay_order_info";

    /**
     * Intent返回结果Key：当前的支付方式
     */
    public static final String INTENT_KEY_CUR_PAY_MODE = "cur_pay_mode";

    /**
     * Intent返回结果Key：真实的支付结果码，依据微信支付、支付宝支付源支付响应码
     */
    public static final String INTENT_KEY_REAL_PAY_RESULT_STATUS_CODE = "pay_real_result_code";

    /**
     * 微信支付模式
     */
    public static final int PAY_MODE_WX  = 1;
    /**
     * 阿里的支付宝支付模式
     */
    public static final int PAY_MODE_ALIPAY = 2;
    //支付结果响应码
    /**
     * 支付成功
     */
    public static final int REQ_PAY_RESULT_CODE_OK = Activity.RESULT_OK;

    /**
     * 支付被取消
     */
    public static final int REQ_PAY_RESULT_CODE_CANCEL = Activity.RESULT_CANCELED;
    /**
     * 支付失败，没有安装微信
     */
    public static final int REQ_PAY_RESULT_CODE_NO_WX = REQ_PAY_RESULT_CODE_OK - 1;
    /**
     * 支付失败：一般错误
     */
    public static final int REQ_PAY_RESULT_CODE_ERROR = REQ_PAY_RESULT_CODE_NO_WX - 1;



}
