package com.vivid.biz.flow.utils;


import org.apache.commons.lang3.CharSequenceUtils;
import org.apache.commons.lang3.CharSetUtils;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;

import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DbUtil {
    // camel string, AbCd to ab_cd
    public static String go2DBName(String name) {
        int num = name.length();
        StringBuilder data = new StringBuilder();
        char d = name.charAt(0);
        if(d >= 'A' && d <= 'Z') {
            data.append(d+32);
        }
        for( int i= 1; i < num; i++) {
            d = name.charAt(i);
            if(d >= 'A' && d <= 'Z'){
                data.append('_');
                data.append(d+32);
            } else {
                data.append(d);
            }
        }
        return data.toString();
    }
}
