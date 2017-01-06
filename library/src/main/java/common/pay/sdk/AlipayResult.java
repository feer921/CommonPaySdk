package common.pay.sdk;

import android.text.TextUtils;

import java.util.HashMap;

/**
 * User: fee(1176610771@qq.com)
 * Date: 2016-07-21
 * Time: 11:56
 * DESC: 支付宝支付SDK旧版本的支付结果实体
 * 参考支付宝支付官方给出的解析规则解析:https://doc.open.alipay.com/doc2/detail?treeId=59&articleId=103662&docType=1
 */
public class AlipayResult extends CommonAlipayResult {
    private HashMap<String,String> paramsValues;
    public AlipayResult(String rawResult) {
        if (TextUtils.isEmpty(rawResult))
            return;

        String[] resultParams = rawResult.split(";");
        for (String resultParam : resultParams) {
            if (resultParam.startsWith(KEY_RESULT_STATUS)) {
                resultStatus = gatValue(resultParam, KEY_RESULT_STATUS);
            }
            if (resultParam.startsWith(KEY_RESULT)) {
                result = gatValue(resultParam, KEY_RESULT);
                if (!TextUtils.isEmpty(result)) {
//                    String[] paramsInResultStr = resultStr.split("&");
                    //[partner="2088101568358171"] [success="true"]
//                    for (String curParam : paramsInResultStr) {
//
//                    }
                }
            }
            if (resultParam.startsWith(KEY_MEMO)) {
                memo = gatValue(resultParam, KEY_MEMO);
            }
        }
    }
    private String gatValue(String content, String key) {
        String prefix = key + "={";
        return content.substring(content.indexOf(prefix) + prefix.length(),
                content.lastIndexOf("}"));
    }
}
