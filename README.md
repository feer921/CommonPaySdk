# 再版说明
由于想把本库发布到【[jitpack](https://jitpack.io/)】 上去以方便使用者直接可以进行gradle依赖，但想要能放上【jitpack】上，需要一个完整的项目才能成功，可参考教程【[将自己写的库发布到JitPack](http://blog.csdn.net/chendong_/article/details/52196454)】,而之前并没有考虑到这一点，库【[CommonPayLib](https://github.com/feer921/CommonPayLib)】与Demo工程【[WxAlipayDemo](https://github.com/feer921/WxAlipayDemo)】是分开写的，但是不能单独将一个Android库发布到JitPack上，故打算重新开一个完整项目，并与其他开源库一样，直接在项目中通用支付库以library的形式存在，这样才能发布到【jitpack】上去。

# 依赖步骤 
因为本库已经发布到JitPack上去了，所以APP的使用可以直接进行gradle依赖

步骤：

1、在项目的根目录下的build.gradle中添加JitPack的maven 仓库

> allprojects{
> 
> repositories{
> 
>    ...
> 
> maven{url'https://jitpack.io'}
> 
> }
> 
> }

2、在项目的app目录(moudle)的build.gradle文件中添加如下代码：

> dependencies{
> 
> ...
> 
> compile'com.github.feer921:CommonPaySdk:1.1'//目录为1.1版本
> 
> }

再更改了*.gradle文件时 AndroidStudio会提示需要同步(编译)，如果顺利的话，本库则应该会依赖成功。

# 使用方法及步骤
本库的核心类为【[PayEntryActivity](https://github.com/feer921/CommonPaySdk/blob/master/library/src/main/java/common/pay/sdk/PayEntryActivity.java)】，之所以说是微信与支付宝支付的通用库，其调起微信/支付宝的以及接收支付的结果响应逻辑全在这个类里面，一个类通用两种支付方式，看过别人写支付功能时，微信支付的功能就按照官方的Demo写一个WxPayEntryActivity，支付宝的就另写一个逻辑，这样会造成需要分开来处理，不太方便，而且如果好几个项目都要集成支付功能，每个项目里面都要写一样的代码，维护也不爽，因而很有必要写这样一个通用库工程到处可以使用。

### 1、步骤一
因为微信支付SDK需要一个appid，该appid为在微信支付开放平台为所要申请开通支付功能的app生成的

所以必要先给本库的中的【[CommonPayConfig](https://github.com/feer921/CommonPaySdk/blob/master/library/src/main/java/common/pay/sdk/CommonPayConfig.java)】的变量【WX_APP_ID】赋上你的APP在开放平台上的appid的值，一般写在你的APP里的application类下，如：

> public class MyApplication extends Application {
> 
> @Override
> 
> public void onCreate() {
> 
> super.onCreate();
> 
> ...
> 
> CommonPayConfig.WX_APP_ID="wxb4bbf0651d312ab6";
> 
> }
> 
> ..........
> 
> ｝

### 2、准备选择支付方式的界面(APP自备)，以及解析服务端返回的支付订单信息
一般来讲APP上在付款前都会提供一个供用户选择使用何种支付方式的界面，如下：

![供用户选择支付方式](http://upload-images.jianshu.io/upload_images/3531899-1b0fd78e41c9fd57.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)



用户选择相应的支付方式后，用户点击支付时，APP会提交<支付方式、支付相关的数据>给APP的后台服务器，后台服务器则依支付方式对应的去微信支付服务器或者支付宝支付服务器获取相关的预支付订单信息(该信息数据需要满足各自官网上的示例数据信息)返回给APP，示例如下，

返回的微信订单信息数据示例（参考别人公司的）：

> {
> 
> "status": "1",
> 
> "msg": "请求成功",
> 
> "data": {
> 
> "orderNo": "551515515151551",//这个字段是APP服务器自己加的,可以用来让APP再去APP服务器上查询一遍是否本次订单支付成功
> 
> "appid": "wxbee9e8888665656",
> 
> "partnerid": "01234598565",
> 
> "prepayid": "wx20160122151438e832d724940443134124",
> 
> "noncestr": "uphct75fl9qexvpeeiy8k0cdzo13h7ap",
> 
> "timestamp": "1453446877",
> 
> "package": "Sign=WXPay",
> 
> "paySign": "1C71DBC9F32B41723165554987DD0F7"
> 
> }

返回的支付宝订单信息(参考别人公司的)：

> {
> 
> "status": "1",
> 
> "msg": "请求成功"，
> 
> "data": {
> 
> "orderNo": "2016011310211365556",//这个字段是APP服务器自己加的
> 
> "payInfo": "partner="2088101568358171"&seller_id="xxx@alipay.com"&out_trade_no="0819145412-6177"&subject="测试"&body="测试测试"&total_fee="0.01"¬ify_url="http://notify.msp.hk/notify.htm"&service="mobile.securitypay.pay"&payment_type="1"&_input_charset="utf-8"&it_b_pay="30m"&sign="lBBK%2F0w5LOajrMrji7DUgEqNjIhQbidR13GovA5r3TgIbNqv231yC1NksLdw%2Ba3JnfHXoXuet6XNNHtn7VE%2BeCoRO1O%2BR1KugLrQEZMtG5jmJIe2pbjm%2F3kb%2FuGkpG%2BwYQYI51%2BhA3YBbvZHVQBYveBqK%2Bh8mUyb7GM1HxWs9k4%3D"&sign_type="RSA"}
> }

以上参考的从服务端返回的订单数据信息为Json格式的（有可能一些公司返回的不是Json数据格式），但不管返回什么格式，一般APP在接收到网络请求的响应时，会使用一些框架如Gson，FastJson，JackSon等来把响应信息解析成Java对象，上面的这种返回数据关注的是”data"字段，将这个字段解析成Java对象，此时，关键的一环来了，要使用本库来调起微信、支付宝支付，则所解析成的Java对象，需要实现本库的一个接口【ICanPayOrderInfo】该接口的目的即为统一以及通用各APP从服务端解析的支付订单数据对象，该接口【[ICanPayOrderInfo](https://github.com/feer921/CommonPaySdk/blob/master/library/src/main/java/common/pay/sdk/ICanPayOrderInfo.java)】代码为：

> package common.pay.sdk;
> 
> import com.tencent.mm.sdk.modelpay.PayReq;
> 
> import java.io.Serializable;
> 
> /**
> 
> *User: fee(1176610771@qq.com)
> 
> *Date: 2016-11-01
> 
> *Time: 18:51
> 
> *DESC: 能发起微信/支付宝 支付的订单信息接口,各APP在使用本库时，因为各自的服务端返回的微信、支付宝的支付订单信息数据
> 
> *本库不能统一，所以只要各APP的某个订单信息实体对象实现该接口并正确实现接口中的方法
> 
> */
> 
> public interface ICanPayOrderInfo extends Serializable {
> 
> /**
> 
> *将服务端的返回支付订单信息转换成微信支付订单请求对象，如果当前是微信支付的话，
> 
> *参考：
> 
> *
> 
> PayReq request = new PayReq();
> 
> request.appId = "wxd930ea5d5a258f4f";
> 
> request.partnerId = "1900000109";
> 
> request.prepayId= "1101000000140415649af9fc314aa427",;
> 
> request.packageValue = "Sign=WXPay";
> 
> request.nonceStr= "1101000000140429eb40476f8896f4c9";
> 
> request.timeStamp= "1398746574";
> 
> request.sign= "7FFECB600D7157C5AA49810D2D8F28BC2811827B";
> 
> *@return 微信订单支付请求对象
> 
> */
> 
> PayReq convert2WxPayReq();
> 
> /**
> 
> *获取阿里--支付宝的订单信息
> 
> *该支付宝的订单信息规则需要参见
> 
> *https://doc.open.alipay.com/doc2/detail?treeId=59&articleId=103662&docType=1
> 
> *上面链接中请求参数示例对应PayTask payTask = new PayTask(activity); payTask.pay(orderInfo,true);
> 
> *目前有新版本
> 
> *https://doc.open.alipay.com/docs/doc.htm?spm=a219a.7629140.0.0.vvNnw2&treeId=204&articleId=105300&docType=1
> 
> *新版本的支付SDK对应PayTask payTask = new PayTask(activity); payTask.payV2(orderInfo,true);
> 
> *@return
> 
> */
> 
> String getAlipayInfo();
> 
> /**
> 
> *本次订单信息是否可以支付
> 
> *@return
> 
> */
> 
> boolean canPayThisOrder();
> 
> /**
> 
> *是否为支付宝的订单
> 
> *@return 依据是否当前有支付宝的支付订单请求信息来判断
> 
> */
> 
> boolean isAliPayOrder();
> 
> /**
> 
> *是否为微信支付的订单
> 
> *@return 依据是否当前有微信支付订单请求的关键字段来判断
> 
> */
> 
> boolean isWxPayOrder();
> 
> }

你自己解析服务端返回的支付订单信息成Java对象(通用微信/支付宝两支付订单信息)并实现本库接口【ICanPayOrderInfo】并正确实现该接口中的方法后，可参考本库自带的一个示例类【[PrePayOrderInfo](https://github.com/feer921/CommonPaySdk/blob/master/library/src/main/java/common/pay/sdk/PrePayOrderInfo.java)】则可以使用本库发起支付了。
### 3、调用本库的API发起微信、支付宝支付
API方法在【PayEntryActivity】中，为全局静态方法，有三种方式，见下，

#### 方式一：

> /**注：该启动支付的方法目前只支持(阿里支付宝支付)
> 
> *微信支付时也可以调用，但由于微信支付SDK对响应回调的WxPayEntryActivity的包路径限制很死，所以本库的PayEntryActivity接收不到
> 
> *微信支付SDK的响应回调，所以为了通用请直接调用{@linkplain #startPayActivity(Activity, ICanPayOrderInfo, int, Class)}
> 
> *@param activity 发起支付的当前Activity
> 
> *@param curPrePayOrderInfo  当前服务器返回的支付请求信息数据对象
> 
> *@param requestCode 区分请求的请求码
> 
> *@deprecated
> 
> */
> 
> public static void startPayActivity(Activity activity, ICanPayOrderInfo curPrePayOrderInfo, int requestCode) {
> 
> startPayActivity(activity, curPrePayOrderInfo, requestCode, PayEntryActivity.class);
> 
> }

注意该方法已被标注为过期了，因为该方法在发起支付宝支付时没问题，但如果发起微信支付时，会收不到微信支付SDK的支付结果响应，所以最好不要使用这个方式，原因是微信支付SDK对接收它的支付结果响应的Activity一定要在指定的包路径下才能接收到结果响应，因而下面的方式二，方式三正是解决这个问题。

#### 方式二：

首先说一下，微信支付SDK对接收支付结果响应的Activity的限制情况，按照微信支付官方以及其Demo的做法来看，要想接收到微信支付SDK的支付结果，必须一定要满足以下条件，

其一：一定要有这样一个名字的Activity类，即【WxPayEntryActivity】；

其二：类【WxPayEntryActivity】一定要在【wxapi】包下；

其三：【wxapi】这个目录一定要在当前项目的包名的直接目录下，并且该项目包名即为在微信开放平台上申请时填写的包名。

所有以上满足条件后的目录结构示例(也即本项目的目录结构)为：
![满足微信支付SDK要求的目录结构](http://upload-images.jianshu.io/upload_images/3531899-08fbc89a09b12b84.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)





（吐槽一下，其实微信支付SDK这样把目录限制死，很不利于有些项目有多渠道打包(并且连包名都不同)的需求，应该像支付宝支付SDK一样，只要调用SDK里的方法就行，管它在哪调用的在哪接收。），顺带提一下，怎么解决多渠道打包的需求，比如说你的APP根据不同的应用市场(如360手机应用，应用宝等)，并且在不同的渠道应用市场上时包名也不一样这种场景，那么解决办法类似，即如果有两个包名(决定该包名是什么，是在app目录下build.gradle文件中的applicationId=''的赋值来决定)需要打包发布，则在你的APP项目代码下，也要建立相对应的包路径，然后，再在包路径下，复制粘贴放进【wxapi】以及【WxPayEntryActivity】放进【wxapi】目录下，即目录是这样的：com.myapp.360ver-->wxapi-->WxPayEntryActivity。

并且所写的【WxPayEntryActivity】类需要继承本库的【PayEntryActivity】类，可以是一个空类，示例-->
![需要在当前所打包的包名目录的wxapi目录下写一个WxPayEntryActivity类](http://upload-images.jianshu.io/upload_images/3531899-2fd017a252c6fba7.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


**并且别忘记在AndroidMenifest中注册该【WxPayEntryActivity】如：**

> <activity 
> 
> android:name=".wxapi.WXPayEntryActivity"
> 
> android:exported="true"
> 
> android:launchMode="singleTop"
> 
> android:screenOrientation="portrait"
> 
> />

则可以使用以下方式发起支付了，见代码：

> /**
> 
> 为了解除微信支付SDK限制死集成微信支付的APP内一定要在包名内下建立一个wxapi包再在该包下建立WxPayEntryActivity类才能正常回调出响应
> 
> *所以本库改为此方法来调起支付
> 
> *@param startActivity 发起支付的当前Activity
> 
> *@param curPrePayOrderInfo 当前服务器返回的支付请求信息数据对象
> 
> *@param requestCode 区分请求的请求码
> 
> *@param localWxPayEntryActivityClass 即你的APP内的wxapi包下建立的WxPayEntryActivity(该类你什么也不用写就继承PayEntryActivity就行)
> 
> */
> 
> public static void **startPayActivity(Activity startActivity, ICanPayOrderInfo curPrePayOrderInfo, int requestCode, Class<? extends PayEntryActivitylocalWxPayEntryActivityClass)** {
> 
> Intent startIntent = new Intent(startActivity, localWxPayEntryActivityClass);
> 
> startIntent.putExtra(CommonPayConfig.INTENT_KEY_CUR_PAY_ORDER_INFO, curPrePayOrderInfo);
> 
> startActivity.startActivityForResult(startIntent, requestCode);
> 
> }


#### 方式三（为了解决一些APP中存在在Fragment中调起支付的场景）：

> /**
> 
> * 该方法供在Fragment界面里跳转支付的情况，这样就能直接在Fragment的onActivityResult()方法中直接拿到支付结果并处理了
> 
> *@param fragment 当前碎片界面
> 
> *@param curPrePayOrderInfo 当前服务器返回的支付请求信息数据对象
> 
> *@param requestCode 区分请求的请求码
> 
> *@param localWxPayEntryActivityClass 即你的APP内的wxapi包下建立的WxPayEntryActivity
> 
> */
> 
> public static void ***startPayActivity(Fragment fragment, ICanPayOrderInfo curPrePayOrderInfo, int requestCode, Class<? extends PayEntryActivitylocalWxPayEntryActivityClass)***{
> 
> if (fragment == null) {
> 
> return;
> 
> }
> 
> Intent startIntent = new Intent(fragment.getContext(), localWxPayEntryActivityClass);
> 
> startIntent.putExtra(CommonPayConfig.INTENT_KEY_CUR_PAY_ORDER_INFO, curPrePayOrderInfo);
> 
> fragment.startActivityForResult(startIntent, requestCode);
> 
> }

可以看到，方式二与方式三的**第四个**参数即需要传入你APP内自己写的那个【WxPayEntryActivity】的Class，并且该【WxPayEntryActivity】类需要继承【PayEntryActivity】，这样就从本库的角度出发解决微信支付SDK对接收支付结果响应的Activity的包路径问题并通用。

### 4、可自定义调起支付时的过度界面
虽然使用本库时，你需要写一个【WxPayEntryActivity】并且继承本库的【PayEntryActivity】，就能发起支付了，但本库的【PayEntryActivity】里面的一些等待支付结果时过度界面使用了默认的一套UI，可能对于一些APP来说，不太美观，所以其实在你写的【WxPayEntryActivity】中重写父类的一些方法来自定义你喜欢的过度界面，比如可重写父类【PayEntryActivity】中的

/**

*子类可重写此方法用来提供自己喜欢的过度界面

*@return

*/

@Override

protected int getProvideContentViewResID() {

return R.layout.def_pay_activity_layout;

}
等其他你认为需要优化UI的细节。

### 5、接收微信/支付宝的支付结果
以上步骤完成后，就只剩下等待支付响应结果了，本库的支付响应结果，是通过【PayEntryActivity】中来setResult()方法设置的，所以不管你是在【Activity】中还是在【Fragment】中使用本库来调起支付，都是在"protected voidonActivityResult(intrequestCode, intresultCode,Intent data){}" 方法中来接收支付结果，可参考本项目的【MainActivity】中的

> @Override
> 
> protected void onActivityResult(int requestCode, int resultCode, Intent data) {
> 
> super.onActivityResult(requestCode, resultCode, data);
> 
> switch (requestCode) {
> 
> case TEST_REQUEST_PAY_CODE:
> 
> String toastHint = "支付模式:%s,响应码:%s,结果描述:%s";
> 
> String payModeDesc = "未知";
> 
> String payRespCode = "unKnow";
> 
> if (data != null) {
> 
> int payMode = data.getIntExtra(CommonPayConfig.INTENT_KEY_CUR_PAY_MODE, CommonPayConfig.PAY_MODE_WX);
> 
> payModeDesc = payMode == CommonPayConfig.PAY_MODE_ALIPAY ? "[支付宝]" : "[微信]";
> 
> payRespCode = data.getStringExtra(CommonPayConfig.INTENT_KEY_REAL_PAY_RESULT_STATUS_CODE);
> 
> }
> 
> String resultDesc = "支付失败";
> 
> switch (resultCode) {
> 
> case CommonPayConfig.REQ_PAY_RESULT_CODE_OK:
> 
> resultDesc = "支付成功";
> 
> break;
> 
> case CommonPayConfig.REQ_PAY_RESULT_CODE_CANCEL:
> 
> resultDesc = "支付被取消了";
> 
> break;
> 
> case CommonPayConfig.REQ_PAY_RESULT_CODE_NO_WX:
> 
> resultDesc = "支付失败,未安装微信APP";
> 
> break;
> 
> case CommonPayConfig.REQ_PAY_RESULT_CODE_ERROR:
> 
> resultDesc = "支付失败";
> 
> break;
> 
> }
> 
> String payResultInfo = "支付模式:" + payModeDesc + "\n" +
> 
> "支付SDK的实际响应码：" + payRespCode + "\n" +
> 
> "结果描述：" + resultDesc;
> 
> toastShow(String.format(toastHint, payModeDesc, payRespCode, resultDesc));
> 
> break;
> 
> }
> 
> }

至此，整个支付使用流程就结束了。

### 6、打包测试
由于微信支付SDK要能真正输入密码并付款，需要你的APP用正式签名文件进行打包才能使用，而打正式包并且安装到手机上这挺费事的，那么可以直接通过AndroidStudio运行(debug模式)到手机上来作正式测试，就是让Android Studio跑debug模式时也指定签名配置为使用正式的签名文件来打包，教程可网上搜索或者参考:

Android Studio配置正式签名和debug签名



# 番外
如果本支付通用库有幸被你使用了，顺便说一句，那么微信SDK中的微信分享等功能也可以使用，同理，你有APP的包名路径下的【wxapi】目录下写一个【WXEntryActivity】即可，具体逻辑就自己写吧。
感谢各位的使用，欢迎提交【issue】【建议】【指正】【批评】，谢谢！












































