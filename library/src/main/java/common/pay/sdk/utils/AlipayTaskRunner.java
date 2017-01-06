package common.pay.sdk.utils;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import com.alipay.sdk.app.PayTask;

import java.util.Map;

import common.pay.sdk.CommonAlipayResult;
import common.pay.sdk.AlipayResult;
import common.pay.sdk.AlipayResultV2;

/**
 * User: fee(1176610771@qq.com)
 * Date: 2016-11-01
 * Time: 10:53
 * DESC: 支付宝支付任务的运行者
 */
public class AlipayTaskRunner implements Runnable{
    private Activity activity4Pay;
    private String alipayOrderInfo;
    private Handler payResultHandler;
    public enum AlipayVersion{
        V1,V2
    }
    public static final int MSG_ALIPAY_RESULT = 0x10;
//    public static final int MSG_ALIPAY_RESULT_V2 = MSG_ALIPAY_RESULT + 1;

    public AlipayTaskRunner(Activity curActivity,Handler payResultHandler, String curAlipayOrderInfo) {
        this.activity4Pay = curActivity;
        this.alipayOrderInfo = curAlipayOrderInfo;
        this.payResultHandler = payResultHandler;
    }
    public String getAlipayOrderInfo() {
        return alipayOrderInfo;
    }
    AlipayVersion defAlipaySdkVersion = AlipayVersion.V1;
    public void applyPaySdkVersion(AlipayVersion tagetVersion) {
        defAlipaySdkVersion = tagetVersion;
    }
    @Override
    public void run() {
        PayTask curPayTask = new PayTask(activity4Pay);
        String paySdkVersion = curPayTask.getVersion();
        CommonAlipayResult alipayResult = null;
        //由于阿里支付的此API耗时需要在工作线程中执行,执行结果通过Handler回调至主线程
        if (defAlipaySdkVersion == AlipayVersion.V1) {
            String payResult = curPayTask.pay(alipayOrderInfo, true);
            alipayResult = new AlipayResult(payResult);
        }
        else if(defAlipaySdkVersion == AlipayVersion.V2){
            Map<String, String> resultMap = curPayTask.payV2(alipayOrderInfo, true);
            Util.e("info", "--> Alipay ver2 paySdkVersion = " + paySdkVersion + " resultMap = " + resultMap);
            alipayResult = new AlipayResultV2(resultMap);
        }
        Message msgPayResult = Message.obtain(payResultHandler, MSG_ALIPAY_RESULT, alipayResult);
        msgPayResult.sendToTarget();
    }
}
