package com.haizhi.bqd.web.rest.util;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by chenbo on 17/4/7.
 */
@Slf4j
public class ConfUtil {
    public static Map<String, String> getConfMap(String filePrefix) {
        if (Strings.isNullOrEmpty(filePrefix)) {
            return null;
        }
        Map<String, String> result = Maps.newHashMap();
        InputStream inputStream = ConfUtil.class.getClassLoader().getResourceAsStream("/conf/" + filePrefix + ".conf");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] keyValue = line.split(",");
                if (keyValue.length == 2) {
                    result.put(keyValue[0].trim(), keyValue[1].trim());
                }
            }
        } catch (IOException e) {
            log.error("{}", e);
        }
        return result;
    }

    public static List<String> getConfList(String filePrefix) {
        if (Strings.isNullOrEmpty(filePrefix)) {
            return Collections.emptyList();
        }
        List<String> result = Lists.newArrayList();
        InputStream inputStream = ConfUtil.class.getClassLoader().getResourceAsStream("/conf/" + filePrefix + ".conf");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                result.add(line.trim());
            }
        } catch (IOException e) {
            log.error("{}", e);
        }
        return result;
    }
}
