package com.haizhi.bqd.common.security;

import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.springframework.security.authentication.encoding.PasswordEncoder;

import java.util.Random;

/**
 * Created by chenbo on 17/4/7.
 */
public class SimpleHashPasswordEncoder implements PasswordEncoder {

    private static final int HASH_ITERATIONS = 2;
    private static Random random = new Random();

    @Override
    public String encodePassword(String rawPass, Object salt) {
        return new SimpleHash(Md5Hash.ALGORITHM_NAME, rawPass, salt,
                HASH_ITERATIONS).toHex();
    }

    @Override
    public boolean isPasswordValid(String encPass, String rawPass, Object salt) {
        return encPass.equals(encodePassword(rawPass, salt));
    }

    public static String genSalt(Integer size) {
        String[] strings = new String[]{"0", "1", "2", "3", "4", "5", "6", "7",
                "8", "9", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
                "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v",
                "w", "x", "y", "z", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
                "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
                "W", "X", "Y", "Z"};

        StringBuffer buffer = new StringBuffer("");
        for (int i = 0; i < size; i++) {
            buffer.append(strings[random.nextInt(strings.length)]);
        }
        return buffer.toString();
    }

    public static void main(String[] args) {

        SimpleHashPasswordEncoder encoder = new SimpleHashPasswordEncoder();

        System.out.println(encoder.encodePassword("123", "3c7b"));

        System.out.printf(encoder.genSalt(4));
    }

}
