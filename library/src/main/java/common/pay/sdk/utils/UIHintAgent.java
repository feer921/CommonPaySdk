package common.pay.sdk.utils;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Handler;

import cn.pedant.SweetAlert.SweetAlertDialog;
import common.pay.sdk.R;


/**
 * UI交互过程一些提醒类型的交互代理者
 * 里面的一些通用交互控件都可以自定义再对应的替换
 * 2015年9月7日-下午7:49:21
 * @author lifei
 * 注：本类有删减，完整版本
 * 参考：https://github.com/feer921/BaseProject/blob/master/src/main/java/common/base/activitys/UIHintAgent.java
 */
public class UIHintAgent {
    private Context mContext;
    /**
     * 对话框Dialog中的("确定“、”取消“按钮)的点击事件的监听者,供回调给外部
     */
    private DialogInterface.OnClickListener mClickListenerForDialog;
    private IProxyCallback  mProxyCallback;
    /**
     * 当前宿主(Activity)是否可见，一般不可见的情况(比如执行了onStop())不应该弹出Dialog
     */
    private boolean isOwnerVisible = true;
    /**
     * 提示性对象框Dialog显示show时是否按back键可取消
     * 默认为可取消
     */
    private boolean isHintDialogCancelable = true;
    /**
     * 提示性对象框Dialog显示show时是否可点击外部取消
     * 默认为点击外部不可取消
     */
    private boolean isHintDialogCancelableOutSide = false;
    /**
     * 是否需要监听提示性Dialog对话框的被取消显示(dismiss)事件
     */
    private boolean isNeedListenHintDialogCancel = false;
    private Handler mHandler;
    /**
     * 提示加载对话框是否可按back键取消 默认为不可取消
     */
    private boolean isLoadingDialogCancelable = false;
    //added by fee 2016-07-28
    private SweetAlertDialog sweetAlertDialog;
    private SweetAlertDialog sweetLoadingDialog;
    public void setProxyCallback(IProxyCallback curProxyOwner){
        mProxyCallback = curProxyOwner;
    }

    public UIHintAgent(Context curContext) {
        this.mContext = curContext;
    }
    public void setHintDialogOnClickListener(DialogInterface.OnClickListener l) {
        mClickListenerForDialog = l;
    }

    /**
     * 是否需要监听本类中hintDialog被用户取消展示的动作，一般来讲是不需要监听提示性的Dialog的被取消的动作的。
     *
     * 但由用户选定一个数据体并弹出需要对数据体进行某种操作而弹出提示性Dialog时，一般程序会使用临时变量存放该数据体，eg.:Object temp2OptObj;
     * 而当用户在所弹出的提示性Dialog中取消了对所选定的数据体的操作时，应该将临时存放的数据体给置空(因为用户取消了该数据体的操作，所以程序应该也取消对该数据体的引用，不然
     * 可能会造成一些bug，当然如果其他程序块中不会对上次所选定的数据体进行操作，也不会出什么问题)，上面这种场景下，则有一定必要来监听用户的取消操作
     * so:此处提供该功能，设置了监听后，请在当前界面中的实现{@linkplain IProxyCallback#ownerToCancelHintDialog()}中处理.
     * @param isNeed true:需要; false:不需要
     */
    public void needListenHintDialogCancelCase(boolean isNeed) {
        isNeedListenHintDialogCancel = isNeed;
        setUpHintDialogCancelListenerInfo();
    }

    /**
     * 开关 : 提示用Dialog 是否可按back键取消
     * @param cancelable
     */
    public void toggleHintDialogCancelable(boolean cancelable){
        isHintDialogCancelable = cancelable;
        if (sweetAlertDialog != null) {
            sweetAlertDialog.setCancelable(cancelable);
        }
        setUpHintDialogCancelListenerInfo();
    }

    /**
     * 开关/触发：提示用Dialog是否可点击自身的外围空间而取消显示(dismiss)
     * @param hintDialogCancelable
     */
    public void toggleHintDialogCancelableOutSide(boolean hintDialogCancelable) {
        isHintDialogCancelableOutSide = hintDialogCancelable;

        //added 2016-10-31
        if (sweetAlertDialog != null) {
            sweetAlertDialog.setCanceledOnTouchOutside(hintDialogCancelable);
        }
    }
    /**
     * 开关 : 加载对话框 是否可按back键取消
     * @param cancelable
     */
    public void toggleLoadingDialogCancelable(boolean cancelable){
        isLoadingDialogCancelable = cancelable;

        //added 2016-10-31
        if (null != sweetLoadingDialog) {//之所以需要主动再调用一次，是因为，如果使用者先调用#showLoading()时loadDialog已经设置了是否可取消显示为loadingDialogCancelable的默认值
            //而中间想改变是否可取消的值时，如果不主动调用一次，则会无效,故凡是临时改变Dialog的是否可取消的状态值时都需要主动再调用一次
            sweetLoadingDialog.setCancelable(cancelable);
        }
        setUpLoadingDialogCancelListenerInfo();
    }
    public void onClickInDialog(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
           //本来一些通用的点击提示对话框的肯定性按钮时 的通用处理，比如：对话框提示了未登陆，点击确定，本意可以统一在这处理，但由于框架不知道对话框所处的显示情景，所以不能在此处理了
            //可以交给各APP的统一基类来处理
        }
        dialog.dismiss();
    }

    /**
     * Activity界面之间的切换效果
     * @param finishSelf
     */
    protected void switchActivity(boolean finishSelf) {
        if(mContext instanceof Activity){
            if (finishSelf) {
                ((Activity) mContext).overridePendingTransition(R.anim.common_part_left_in, R.anim.common_whole_right_out);
            } else {
                ((Activity) mContext).overridePendingTransition(R.anim.common_whole_right_in, R.anim.common_part_right_out);
            }
        }
    }


    /**
     * 配置Loading类Dialog的被取消显示时的监听者信息
     */
    private void setUpLoadingDialogCancelListenerInfo() {
        if (isLoadingDialogCancelable) {//只有Loading类的Dialog在可取消(按back键)时才有意义去设置取消的监听事件
            if (loadingDialogCancelListener == null) {
                loadingDialogCancelListener = new LoadingDialogCancelListener();
            }
        }

        if (sweetLoadingDialog != null) {
            sweetLoadingDialog.setOnCancelListener(isLoadingDialogCancelable ? loadingDialogCancelListener : null);
        }
    }

    /**
     * 配置提示类Dialog的被取消显示时的监听者信息
     */
    private void setUpHintDialogCancelListenerInfo() {
        boolean isNeedToConfigCancelListener = isNeedListenHintDialogCancel && isHintDialogCancelable;
        if (isNeedToConfigCancelListener) {
            if (hintDialogCancelListener == null) {
                hintDialogCancelListener = new HintDialogCancelListener();
            }
        }

        if (sweetAlertDialog != null) {
            sweetAlertDialog.setOnCancelListener(isNeedToConfigCancelListener ? hintDialogCancelListener : null);
        }
    }

    public void loadDialogDismiss() {

        //added by fee 2016-07-28
        if (sweetLoadingDialog != null) {
            sweetLoadingDialog.dismissWithAnimation();
        }
    }

    public void dismissAlertDialog() {
        if (sweetAlertDialog != null) {
            sweetAlertDialog.dismissWithAnimation();
        }
    }
    public boolean isLoadingDialogShowing(){
        return sweetLoadingDialog != null && sweetLoadingDialog.isShowing();
    }


    private String getString(int errorInfoResIdBaseCode) {
        return mContext.getResources().getString(errorInfoResIdBaseCode);
    }


    public void setOwnerVisibility(boolean isVisible){
        this.isOwnerVisible = isVisible;
    }
    /**
     * 结束代理UI交互
     */
    public void finishAgentFollowUi() {

        if(mHandler != null){
            mHandler.removeCallbacksAndMessages(null);
        }
        mHandler = null;

        if (sweetAlertDialog != null) {
            sweetAlertDialog.dismiss();
        }
        if (sweetLoadingDialog != null) {
            sweetLoadingDialog.dismiss();
        }
    }

    public int getHintDialogInCase() {
        //hintDialogInWhichCase 该变量会在sweetAlertDialog show时赋值当前的Case值
        return hintDialogInWhichCase;
    }


    /**
     * @deprecated
     */
    public void cancelLoadingCaseTimer(){
        if(mHandler != null){
            mHandler.removeCallbacksAndMessages(null);
        }
    }
    //added sweetAlertDialog codes by fee 2016-07-28
    /**
     * 提示性Dialog当前显示show时所处的哪种(提示性)情况
     */
    private int hintDialogInWhichCase = 0;
    public void sweetLoading(String loadhintMsg) {
        if (sweetLoadingDialog == null) {
            sweetLoadingDialog = new SweetAlertDialog(mContext);
            sweetLoadingDialog.setCancelable(isLoadingDialogCancelable);
            sweetLoadingDialog.changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
            setUpLoadingDialogCancelListenerInfo();
        }
        sweetLoadingDialog.setTitleText(loadhintMsg);
        if (!sweetLoadingDialog.isShowing()) {
            sweetLoadingDialog.show();
        }
    }

    public void sweetHintSuc(String successInfo,String confimInfo,int sweetDialogInCase) {
        sweetDialogHint(successInfo, null, null, confimInfo, sweetDialogInCase, SweetAlertDialog.SUCCESS_TYPE);
    }

    public void sweetHintFail(String failHintInfo, String confimInfo, int sweetDialogInCase) {
        sweetDialogHint(failHintInfo, null, null, confimInfo, sweetDialogInCase, SweetAlertDialog.ERROR_TYPE);
    }

    public void sweetNormalHint(String titleInfo, String hintInfo, String cancelBtnInfo, String confimBtnInfo, int curDialogInCase) {
        sweetDialogHint(titleInfo, hintInfo, cancelBtnInfo, confimBtnInfo, curDialogInCase, SweetAlertDialog.NORMAL_TYPE);
    }
    public void sweetDialogHint(String titleInfo, String hintInfo, String cancelInfo, String confimInfo, int curDialogInCase,int sweetDialogContentCase) {
        if (!isOwnerVisible) {
            return;
        }
        hintDialogInWhichCase = curDialogInCase;
        initSweetAlertDialog();
//        sweetAlertDialog.setOnCancelListener(null);
        sweetAlertDialog.setTitleText(titleInfo)
                .setContentText(hintInfo)
                .setCancelText(cancelInfo)
                .setConfirmText(confimInfo)
                .setConfirmClickListener(comfimBtnClickListener)
                .changeAlertType(sweetDialogContentCase);
        boolean needShowCancelBtn = !Util.isEmpty(cancelInfo);
        sweetAlertDialog.showCancelButton(needShowCancelBtn);
        if (needShowCancelBtn) {
            sweetAlertDialog.setCancelClickListener(cancelBtnClickListener);
        }
        sweetAlertDialog.showContentText(!Util.isEmpty(hintInfo));
        sweetAlertDialog.show();
    }
    private void initSweetAlertDialog() {
        if (sweetAlertDialog == null) {
            sweetAlertDialog = new SweetAlertDialog(mContext);
            sweetAlertDialog.setCancelable(isHintDialogCancelable);
            sweetAlertDialog.setCanceledOnTouchOutside(isHintDialogCancelableOutSide);
            setUpHintDialogCancelListenerInfo();
        }
    }

    /**
     * Loading类的Dialog被取消显示(在设置了可被取消,一般按back键)时的回调监听
     * 注：主动调用dialog.dismiss()是不会触发的(不信可验证)
     */
    private LoadingDialogCancelListener loadingDialogCancelListener;
    private class LoadingDialogCancelListener implements OnCancelListener{
        @Override
        public void onCancel(DialogInterface dialog) {
            if (mProxyCallback != null) {//当Loading类Dialog被取消(在可取消状态下，按back键)显示时回调给外部
                mProxyCallback.ownerToCancelLoadingRequest();
            }
        }
    }

    /**
     * 提示性Dialog被取消显示(在设置了可被取消,一般按back键)时的回调监听
     * 注：主动调用dialog.dismiss()是不会触发的(不信可验证)
     */
    private HintDialogCancelListener hintDialogCancelListener;
    private class HintDialogCancelListener implements OnCancelListener{
        @Override
        public void onCancel(DialogInterface dialog) {
            if (mProxyCallback != null) {//当提示类Dialog被取消(在需要监听该类Dialog取消状态且可取消状态下，按back键)显示时回调给外部
                mProxyCallback.ownerToCancelHintDialog();
            }
        }
    }
    private SweetAlertDialog.OnSweetClickListener comfimBtnClickListener = new SweetAlertDialog.OnSweetClickListener() {
        @Override
        public void onClick(SweetAlertDialog sweetAlertDialog) {
            if (mClickListenerForDialog != null) {
                mClickListenerForDialog.onClick(sweetAlertDialog,DialogInterface.BUTTON_POSITIVE);            }
        }
    };

    private SweetAlertDialog.OnSweetClickListener cancelBtnClickListener = new SweetAlertDialog.OnSweetClickListener() {
        @Override
        public void onClick(SweetAlertDialog sweetAlertDialog) {
            if (mClickListenerForDialog != null) {
                mClickListenerForDialog.onClick(sweetAlertDialog, DialogInterface.BUTTON_NEGATIVE);
            }
        }
    };
}
