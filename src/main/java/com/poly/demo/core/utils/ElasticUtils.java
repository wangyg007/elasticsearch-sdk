package com.poly.demo.core.utils;

import org.apache.commons.lang3.time.DateUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wangyg
 * @time 14:03
 * @note
 **/
public class ElasticUtils {

    /**
     * type等属性,5.x以上已经没有string类型。如果需要分词的话使用text，不需要分词使用keyword
     * @return
     */
    public static Map<String, Object> getIndexMapping(){

        Map<String, Object> message = new HashMap<>();
        message.put("type","text");
        Map<String,Object> properties=new HashMap<>();
        properties.put("message",message);
        Map<String,Object> mapping=new HashMap<>();
        mapping.put("properties",properties);
        return mapping;
    }

    public static void main(String[] args) {
        //System.out.println(ElasticUtils.getIndexMapping());;
        String pattern="yyyy-MM-dd HH:mm:ss";
        System.out.println(DateUtil.datetime2Str(DateUtil.now()));
    }

}
