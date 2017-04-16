package com.haizhi.bqd.common.security;

import com.haizhi.bqd.common.MD5Util;
import org.springframework.security.authentication.encoding.PasswordEncoder;

/**
 * Created by chenbo on 17/4/7.
 */
public class MD5Encoder implements PasswordEncoder {

    @Override
    public String encodePassword(String rawPass, Object salt) {
        if(rawPass == null){
            return null;
        }
        return MD5Util.getMD5Code(MD5Util.getMD5Code(rawPass.toString()));
    }

    @Override
    public boolean isPasswordValid(String encPass, String rawPass, Object salt) {
        if(rawPass == null){
            return false;
        }
        return MD5Util.getMD5Code(MD5Util.getMD5Code(rawPass.toString())).equals(encPass);
    }
}
