package common.pay.sdk;

/**
 * User: fee(1176610771@qq.com)
 * Date: 2016-11-02
 * Time: 17:14
 * DESC: 针对阿里支付宝支付
 */
public class CommonAlipayResult {
//    public static final String RESULT_VER_V2_FLAG = "RESULT-V2";
    //    public CommonAlipayResult(String alipayRawResult) {
//        //implements by subClass
//    }
//    public static CommonAlipayResult buildAlipayResult(String alipayRawResult) {
//        CommonAlipayResult result = null;
//        if (!Util.isEmpty(alipayRawResult)) {
//            if (alipayRawResult.startsWith(RESULT_VER_V2_FLAG)) {
//                result = new AlipayResultV2(alipayRawResult);
//            }
//            else{
//                result = new AlipayResult(alipayRawResult);
//            }
//        }
//        return result;
//    }
    /**
     * {
     * "memo" : "xxxxx",
     * "result" : "{
     * \"alipay_trade_app_pay_response\":{
     * \"code\":\"10000\",
     * \"msg\":\"Success\",
     * \"app_id\":\"2014072300007148\",
     * \"out_trade_no\":\"081622560194853\",
     * \"trade_no\":\"2016081621001004400236957647\",
     * \"total_amount\":\"0.01\",
     * \"seller_id\":\"2088702849871851\",
     * \"charset\":\"utf-8\"
     * },
     * \"sign\":\"NGfStJf3i3ooWBuCDIQSumOpaGBcQz+aoAqyGh3W6EqA/gmyPYwLJ2REFijY9XPTApI9YglZyMw+ZMhd3kb0mh4RAXMrb6mekX4Zu8Nf6geOwIa9kLOnw0IMCjxi4abDIfXhxrXyj********\",
     * \"sign_type\":\"RSA\"
     * }",
     * "resultStatus" : "9000"
     * }
     */
    protected static final String KEY_MEMO = "memo";
    protected static final String KEY_RESULT = "result";
    protected static final String KEY_RESULT_STATUS = "resultStatus";
    private static final String STATUS_OK = "9000";
    /**
     * 支付结果的说明/备忘
     */
    protected String memo;



    protected String result;
    /**
     * 支付结果体,对于支付宝支付SDK的V2版本为一Json数据体
     * 参见：
     * 9000	订单支付成功
     8000	正在处理中，支付结果未知（有可能已经支付成功），请查询商户订单列表中订单的支付状态
     4000	订单支付失败
     5000	重复请求
     6001	用户中途取消
     6002	网络连接出错
     6004	支付结果未知（有可能已经支付成功），请查询商户订单列表中订单的支付状态
     其它	其它支付错误
     */
    protected String resultStatus;

    /**
     * 将支付宝SDK返回的响应状态转换成数值型
     * @return
     */
    public int getResultStatusCode() {
        int result = 0;
        try {
            result = Integer.valueOf(resultStatus);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 是否支付成功
     * @return
     */
    public boolean isPayOk() {
        return STATUS_OK.equals(resultStatus);
    }
    public String getMemo() {
        return memo;
    }

    public String getResult() {
        return result;
    }

    /**
     * 获取支付结果的状态
     * @return
     */
    public String getResultStatus() {
        return resultStatus;
    }

    @Override
    public String toString() {
        return "resultStatus = " + resultStatus + " \n"
                + "result = " + result + "\n"
                + "memo = " + memo;
    }
}
