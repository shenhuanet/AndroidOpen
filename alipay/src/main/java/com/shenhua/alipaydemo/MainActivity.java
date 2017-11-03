package com.shenhua.alipaydemo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.shenhua.alipaydemo.utils.HttpUtils;
import com.shenhua.alipaydemo.utils.PayResult;
import com.shenhua.alipaydemo.utils.QueryString;
import com.shenhua.alipaydemo.utils.SignUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by shenhua on 4/21/2016.
 */
public class MainActivity extends AppCompatActivity {

    // 商户PID
    public static final String PARTNER = "2088221988963446";//2088221988963446
    // 商户收款账号
    public static final String SELLER = "1829212192@qq.com";//1829212192@qq.com
    // 商户私钥，pkcs8格式
    public static final String RSA_PRIVATE = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBANyeT8yRCrWOxnbvu9R8OvlTnzBsdTuRBhnFVkzkl81HjuPhT4fPc1uN+SnX0+BCx9KqBMRfm/c6bb9BfOO63SAbbmQeGwoiN0TWJWwTJhG85PyR0eeeGLjEYd+8Uco+D7uhdGb/XFFPDxFDg/+BdPOhnvgcB9pw4pQKdxeWPOg9AgMBAAECgYAcPJbyE85PFosIRf7AX3Yc2zsQs5D9or4pJjy5criLKZF8USYgt8iH+0/crycLGQECUYhyqdoIdo39YKBfdVc/u8UIrTF92DECeSIU3Njvy2THcetcG2v/Ynot3ebgirPYEoCQ+rWfiujHP9WgPNKo2cZfIIspQQV8QpLPETH3AQJBAP9nCS2YdUOYtA4u+VdK9qXQiXV7V9HkhJskrboPQ/W48RHTHRY+O/lUeK3MghCm640pVR8QU6rm4mzZrWNkZS0CQQDdInF0xxjoa7MFG3/UN4AKHzpiNDIT+7ev2DmyhPusrSN8MzBrDWluYQvPETdK3zGS1c65wOb3V3VOiTjBpZlRAkEAi7/BUeWeJIKl4yzo2k72bvyQemwnPX4g4RNMzAYWVZoArpQp0kXzYxZJgi/o61uqf+8h2IGChn0XYCArs7r3cQJBAKTLrlGgrmdHG7qKq9CzK7BfZVsaGtfZlyJsuVLY19XvkapVW5fr/s5LdEl0QwiBHKiYvd6ga3+YpctJaAqoJoECQQCusHUCq/3D5gQ6WIRr98rPY2KZmhwDwifEfnpC62a6hr0xyVj2VZ1jl6O7ofVNMwTFz4/LJ23Ofrrhv62Mqe80";
    // 支付宝公钥
    public static final String RSA_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDcnk/MkQq1jsZ277vUfDr5U58wbHU7kQYZxVZM5JfNR47j4U+Hz3Nbjfkp19PgQsfSqgTEX5v3Om2/QXzjut0gG25kHhsKIjdE1iVsEyYRvOT8kdHnnhi4xGHfvFHKPg+7oXRm/1xRTw8RQ4P/gXTzoZ74HAfacOKUCncXljzoPQIDAQAB";
    // 服务器异步通知页面路径
    public static final String NOTIFY_URL = "http://121.41.18.117:8084/RedPackage/app/ordernotifyurl";
    // 服务接口名称， 固定值
    public static final String SERVICE_NAME = "mobile.securitypay.pay";

    public static final String SAVE_URL = "http://121.41.18.117:8084/RedPackage/app/rechargerecord/saveRecharge";
    public static final String APP_TAKEN = "vers125730383f573189bf1SL0JOTZ3X7205B9469080027386A99B94E8DB4D93E53B66BB6C020FEC8BD3EC79385297B0";
    private String tradeNo;
    private String money;
    private static final int SDK_PAY_FLAG = 1;
    private Button btn_pay;
    private EditText et_money;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_pay = (Button) findViewById(R.id.btn_pay);
        et_money = (EditText) findViewById(R.id.product_price);

        et_money.addTextChangedListener(new TextWatcher() {
            private boolean isChanged = false;

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                if (isChanged) {// ----->如果字符未改变则返回
                    return;
                }
                String str = s.toString();

                isChanged = true;
                String cuttedStr = str;
                /* 删除字符串中的dot */
                for (int i = str.length() - 1; i >= 0; i--) {
                    char c = str.charAt(i);
                    if ('.' == c) {
                        cuttedStr = str.substring(0, i) + str.substring(i + 1);
                        break;
                    }
                }
                /* 删除前面多余的0 */
                int NUM = cuttedStr.length();
                int zeroIndex = -1;
                for (int i = 0; i < NUM - 2; i++) {
                    char c = cuttedStr.charAt(i);
                    if (c != '0') {
                        zeroIndex = i;
                        break;
                    } else if (i == NUM - 3) {
                        zeroIndex = i;
                        break;
                    }
                }
                if (zeroIndex != -1) {
                    cuttedStr = cuttedStr.substring(zeroIndex);
                }
                /* 不足3位补0 */
                if (cuttedStr.length() < 3) {
                    cuttedStr = "0" + cuttedStr;
                }
                /* 加上dot，以显示小数点后两位 */
                cuttedStr = cuttedStr.substring(0, cuttedStr.length() - 2)
                        + "." + cuttedStr.substring(cuttedStr.length() - 2);

                et_money.setText(cuttedStr);
                et_money.setSelection(et_money.length());
                isChanged = false;
            }
        });

        //开始支付
        btn_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                money = et_money.getText().toString();
                if (money.length() == 0 || money == null) {
                    Toast.makeText(getApplicationContext(), "金额不能为空！", Toast.LENGTH_LONG).show();
                    return;
                }
                final QueryString qs = new QueryString();
                qs.add("appToken", APP_TAKEN);
                qs.add("payMoney", money);
                System.out.println(qs.toString());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        HttpUtils manager = new HttpUtils();
                        String result = manager.doPost(SAVE_URL, qs.toString());
                        System.out.println("保存充值记录接口返回：" + result);
                        try {
                            JSONObject o = new JSONObject(new JSONObject(new String(result)).getString("result"));
                            JSONObject object = new JSONObject(o.getString("record"));
                            tradeNo = object.getString("tradeNo");
                            System.out.println(tradeNo);
                            handler.obtainMessage(1).sendToTarget();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
    }

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    String name = "测试名称";
                    String body = "测试描述...";
                    String orderInfo = getOrderInfo(name, body, money);
                    System.out.println("------->" + orderInfo);
                    //模拟服务器签名订单
                    //特别注意，这里的签名逻辑需要放在服务端，切勿将私钥泄露在代码中！
                    String sign = sign(orderInfo);
                    try {
                        //对sign 做URL编码
                        sign = URLEncoder.encode(sign, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    // 生成完整的符合支付宝参数规范的订单信息
                    final String payInfo = orderInfo + "&sign=\"" + sign + "\"&" + getSignType();
                    System.out.println("最终签名后的内容：" + sign);

                    Runnable payRunnable = new Runnable() {

                        @Override
                        public void run() {
                            // 构造PayTask 对象
                            PayTask alipay = new PayTask(MainActivity.this);
                            // 调用支付接口，获取支付结果
                            String result = alipay.pay(payInfo, true);
                            Message msg = new Message();
                            msg.what = SDK_PAY_FLAG;
                            msg.obj = result;
                            mHandler.sendMessage(msg);
                        }
                    };
                    // 必须异步调用
                    Thread payThread = new Thread(payRunnable);
                    payThread.start();
                    break;
            }
        }
    };

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    System.out.println("支付结果:" + (String) msg.obj);
                    PayResult payResult = new PayResult((String) msg.obj);
                    /**
                     * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&docType=1) 建议商户依赖异步通知
                     */
                    final String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    System.out.println("resultInfo:" + resultInfo);


//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            MyHttp http = new MyHttp();
//                            System.out.println("异步：" + http.doPost2(NOTIFY_URL, resultInfo));
//                            System.out.println("异步结束");
//                        }
//                    }).start();


                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档:https://doc.open.alipay.com/doc2/detail.htm?spm=a219a.7629140.0.0.PjIpFm&treeId=59&articleId=103671&docType=1
                    if (TextUtils.equals(resultStatus, "9000")) {
                        /**
                         * 这里需要对success 的值综合判断
                         * partner="2088221988963446"&seller_id="1829212192@qq.com"&out_trade_no="MORD20160623000003"&subject="测试名称"&body="测试描述..."
                         * &total_fee="0.01"&notify_url="http://121.41.18.117:8084/RedPackage/app/ordernotifyurl"
                         * &service="mobile.securitypay.pay"&payment_type="1"&_input_charset="utf-8"&it_b_pay="10m"
                         * &success="true"
                         * &sign_type="RSA"&sign="e3Xpk43vOqnh7qb/Xnykldq7ZtGH5YdFUHmNzHd7r32WZiB3YXFJg7T+pLMOcy4ZkofQocOfLBEvz3bzw9iTw3lGENgQ9q4SQkLqajP3nKjLvjisqIF1hV1wL7PABHsVbKG+9v9HoHIdItjsEoo3nFFAxMSmmQP83KCfyHhQ6AM="
                         */

                        Toast.makeText(MainActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                    } else {
                        // 判断resultStatus 为非"9000"则代表可能支付失败
                        // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Toast.makeText(MainActivity.this, "支付结果确认中", Toast.LENGTH_SHORT).show();
                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            Toast.makeText(MainActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                }
                default:
                    break;
            }
        }
    };

    /**
     * 创建订单信息，未注明的所有类型均不可为空
     *
     * @param name     订单名称
     * @param describe 订单描述
     * @param price    支付价格
     * @return
     */
    private String getOrderInfo(String name, String describe, String price) {
        // 签约合作者身份ID
        String orderInfo = "partner=" + "\"" + PARTNER + "\"";
        // 签约收款方支付宝账号
        orderInfo += "&seller_id=" + "\"" + SELLER + "\"";
        // 商户网站唯一订单号
        orderInfo += "&out_trade_no=" + "\"" + getOutTradeNo() + "\"";
        // 订单名称
        orderInfo += "&subject=" + "\"" + name + "\"";
        // 订单描述
        orderInfo += "&body=" + "\"" + describe + "\"";
        // 支付价格
        orderInfo += "&total_fee=" + "\"" + price + "\"";
        // 服务器异步通知页面路径
        orderInfo += "&notify_url=" + "\"" + NOTIFY_URL + "\"";
        // 服务接口名称， 固定值
        orderInfo += "&service=" + "\"" + SERVICE_NAME + "\"";
        // 支付类型， 固定值 1为支付
        orderInfo += "&payment_type=\"1\"";
        // 参数编码， 固定值
        orderInfo += "&_input_charset=\"utf-8\"";
        // 设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m～15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
        orderInfo += "&it_b_pay=\"10m\"";
        // extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
        // orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
//        orderInfo += "&return_url=\"m.alipay.com\"";

        // 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
        // orderInfo += "&paymethod=\"expressGateway\"";
        return orderInfo;
    }

    /**
     * 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范）
     *
     * @return 订单号
     */
    private String getOutTradeNo() {
        return tradeNo;
    }

    /**
     * 模拟服务器签名订单
     *
     * @param content
     * @return
     */
    private String sign(String content) {
        return SignUtils.sign(content, RSA_PRIVATE);
    }

    /**
     * 获取签名方式
     *
     * @return
     */
    private String getSignType() {
        return "sign_type=\"RSA\"";
    }
}
