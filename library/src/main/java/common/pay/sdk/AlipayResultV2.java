package common.pay.sdk;

import java.util.Map;

/**
 * User: fee(1176610771@qq.com)
 * Date: 2016-11-02
 * Time: 18:18
 * DESC: 支付宝支付SDK V2版本支付时返回的结果实体
 */
public class AlipayResultV2 extends CommonAlipayResult {
    public AlipayResultV2(Map<String,String> alipayResultMap) {
        if (alipayResultMap != null && !alipayResultMap.isEmpty()) {
            resultStatus = alipayResultMap.get(KEY_RESULT_STATUS);
            result = alipayResultMap.get(KEY_RESULT);
            memo = alipayResultMap.get(KEY_MEMO);
        }
    }
}
