package com.test;

import cn.itrip.common.PropertiesUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.junit.Test;
import sun.nio.cs.UnicodeEncoder;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class Test1 {
    @Test
    public void test01(){
        try {
            String s = URLEncoder.encode("张三", "utf-8");
            System.out.println(s);
            String s2 = URLDecoder.decode(s, "utf-8");
            System.out.println(s2);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
