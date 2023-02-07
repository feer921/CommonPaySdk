package common.pay.sdk;

/**
 * 复合的 支付结果响应数据
 */
public class ComplexPayResp {

    /**
     * 当前支付模式类型(微信、支付宝)
     * <ul>
     *     <li>{@link CommonPayConfig#PAY_MODE_WX}</li>
     *     <li>{@link CommonPayConfig#PAY_MODE_ALIPAY}</li>
     * </ul>
     */
    public int payMode;

    /**
     * 本次支付咱就消息
     */
    public String msg;

    /**
     * 本次支付响应码
     * 取值：
     * <ul>
     *     <li>{@link CommonPayConfig#REQ_PAY_RESULT_CODE_OK}</li>
     *     <li>{@link CommonPayConfig#REQ_PAY_RESULT_CODE_CANCEL}</li>
     *     <li>{@link CommonPayConfig#REQ_PAY_RESULT_CODE_ERROR}</li>
     *     <li>{@link CommonPayConfig#REQ_PAY_RESULT_CODE_NO_WX}</li>
     * </ul>
     */
    public int respCode;


    /**
     * 支付SDK的响应码,具体参见微信、支付宝SDK所定义的支付响应码
     */
    public String paySdkRespCode;

    @Override
    public String toString() {
        return "ComplexPayResp{" +
                "payMode=" + payMode +
                ", msg='" + msg + '\'' +
                ", respCode=" + respCode +
                ", paySdkRespCode='" + paySdkRespCode + '\'' +
                '}';
    }
}
