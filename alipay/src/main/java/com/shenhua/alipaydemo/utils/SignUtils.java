package com.shenhua.alipaydemo.utils;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;

/**
 * 签名订单类
 * Created by shenhua on 4/21/2016.
 */
public class SignUtils {

    //算法
    private static final String ALGORITHM = "RSA";
    //签名算法
    private static final String SIGN_ALGORITHMS = "SHA1WithRSA";
    //默认编码
    private static final String DEFAULT_CHARSET = "UTF-8";

    /**
     * 开始签名订单
     *
     * @param content    待签名内容
     * @param privateKey 私钥
     * @return 签名后的内容
     */
    public static String sign(String content, String privateKey) {
        try {
            PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64.decode(privateKey));
            KeyFactory keyf = KeyFactory.getInstance(ALGORITHM);
            PrivateKey priKey = keyf.generatePrivate(priPKCS8);
            java.security.Signature signature = java.security.Signature
                    .getInstance(SIGN_ALGORITHMS);
            signature.initSign(priKey);
            signature.update(content.getBytes(DEFAULT_CHARSET));
            byte[] signed = signature.sign();
            return Base64.encode(signed);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
