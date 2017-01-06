package common.pay.sdk;

import com.tencent.mm.sdk.modelpay.PayReq;

import java.io.Serializable;

/**
 * User: fee(1176610771@qq.com)
 * Date: 2016-11-01
 * Time: 18:51
 * DESC: 能发起微信/支付宝 支付的订单信息接口,各APP在使用本库时，因为各自的服务端返回的微信、支付宝的支付订单信息数据
 * 本库不能统一，所以只要各APP的某个订单信息实体对象实现该接口并正确实现接口中的方法
 */
public interface ICanPayOrderInfo extends Serializable {
    /**
     * 将服务端的返回支付订单信息转换成微信支付订单请求对象，如果当前是微信支付的话，
     * 参考：
     *
     PayReq request = new PayReq();
     request.appId = "wxd930ea5d5a258f4f";
     request.partnerId = "1900000109";
     request.prepayId= "1101000000140415649af9fc314aa427",;
     request.packageValue = "Sign=WXPay";
     request.nonceStr= "1101000000140429eb40476f8896f4c9";
     request.timeStamp= "1398746574";
     request.sign= "7FFECB600D7157C5AA49810D2D8F28BC2811827B";
     * @return 微信订单支付请求对象
     */
    PayReq convert2WxPayReq();

    /**
     * 获取阿里--支付宝的订单信息
     * 该支付宝的订单信息规则需要参见
     * https://doc.open.alipay.com/doc2/detail?treeId=59&articleId=103662&docType=1
     * 上面链接中请求参数示例对应PayTask payTask = new PayTask(activity); payTask.pay(orderInfo,true);
     * 目前有新版本
     * https://doc.open.alipay.com/docs/doc.htm?spm=a219a.7629140.0.0.vvNnw2&treeId=204&articleId=105300&docType=1
     * 新版本的支付SDK对应PayTask payTask = new PayTask(activity); payTask.payV2(orderInfo,true);
     * @return
     */
    String getAlipayInfo();

    /**
     * 本次订单信息是否可以支付
     * @return
     */
    boolean canPayThisOrder();

    /**
     * 是否为支付宝的订单
     * @return 依据是否当前有支付宝的支付订单请求信息来判断
     */
    boolean isAliPayOrder();

    /**
     * 是否为微信支付的订单
     * @return 依据是否当前有微信支付订单请求的关键字段来判断
     */
    boolean isWxPayOrder();
}
