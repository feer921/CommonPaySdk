package common.pay.sdk.utils;



/**
 * 代理者处理事件回调接口
 * <br/>
 * 2015年9月8日-下午9:28:16
 * @author lifei
 */
public interface IProxyCallback {
    /**
     * 被代理者(宿主)主动取消了Loading Dialog的方法回调
     * (eg.:此Loading Dialog正在提示网络数据的请求,用户主动取消后,该方法回调
     * ,则需要在各自实现中实现各网络请求的取消并标志好该请求已取消)
     */
    void ownerToCancelLoadingRequest();

    /**
     * 被代理者(宿主)主动取消了提示性Dialog的回调，各实现类可在此处理当前具体的提示性Dialog被取消了后的逻辑
     */
    void ownerToCancelHintDialog();
}
