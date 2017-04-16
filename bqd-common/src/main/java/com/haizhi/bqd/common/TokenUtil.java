package com.haizhi.bqd.common;

import org.jose4j.jwe.ContentEncryptionAlgorithmIdentifiers;
import org.jose4j.jwe.JsonWebEncryption;
import org.jose4j.jwe.KeyManagementAlgorithmIdentifiers;
import org.jose4j.keys.AesKey;
import org.jose4j.lang.JoseException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by chenbo on 17/4/6.
 */
public class TokenUtil {

    public static final String AUTH_TOKEN_KEY = "Authorization";

    private static final String keyString = "d4897b480d6cedb5";


    public static String generateToken(String username, String password, Date createTime, String sessionId, String userId) throws JoseException {
        Map<String, Object> params = new HashMap<String, Object>();
        if (username != null) {
            params.put("username", username);
        }
        if (password != null) {
            params.put("password", password);
        }
        if (createTime != null) {
            params.put("createTime", createTime);
        }
        if (sessionId != null) {
            params.put("sessionId", sessionId);
        }

        if(userId != null && !"null".equals(userId)){
            params.put("userId",userId);
        }
        return encodeToken(params);
    }


    public static String encodeToken(Map<String, Object> params) throws JoseException {
        Key key = new AesKey(keyString.getBytes());
        JsonWebEncryption jwe = new JsonWebEncryption();
        jwe.setPayload(JsonUtil.formatJSON(params));
        jwe.setAlgorithmHeaderValue(KeyManagementAlgorithmIdentifiers.A128KW);
        jwe.setEncryptionMethodHeaderParameter(ContentEncryptionAlgorithmIdentifiers.AES_128_CBC_HMAC_SHA_256);
        jwe.setKey(key);
        return jwe.getCompactSerialization();
    }

    public static Map<String, Object> decodeToken(String source) throws JoseException {
        Key key = new AesKey(keyString.getBytes());
        JsonWebEncryption jwe = new JsonWebEncryption();
        jwe.setAlgorithmHeaderValue(KeyManagementAlgorithmIdentifiers.A128KW);
        jwe.setEncryptionMethodHeaderParameter(ContentEncryptionAlgorithmIdentifiers.AES_128_CBC_HMAC_SHA_256);
        jwe.setKey(key);
        jwe = new JsonWebEncryption();
        jwe.setKey(key);
        jwe.setCompactSerialization(source);
        return (Map<String, Object>) JsonUtil.unformatJSON(jwe.getPayload(), HashMap.class);
    }

    public static String getToken(HttpServletRequest request) {

        String token = null;

        if (request.getSession().getAttribute(TokenUtil.AUTH_TOKEN_KEY) != null) {
            token = (String) request.getSession().getAttribute(TokenUtil.AUTH_TOKEN_KEY);
        }

        if (token == null) {
            token = request.getHeader(TokenUtil.AUTH_TOKEN_KEY);
        }

        if (token == null) {
            Cookie[] cookie = request.getCookies();
            if (cookie != null) {
                for (int i = 0; i < cookie.length; i++) {
                    if (cookie[i].getName().equals(TokenUtil.AUTH_TOKEN_KEY)) {
                        token = cookie[i].getValue();
                    }
                }
            }
        }

        return token;

    }
}
