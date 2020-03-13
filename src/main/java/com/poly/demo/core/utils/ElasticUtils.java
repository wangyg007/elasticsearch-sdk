package com.poly.demo.core.utils;

import org.elasticsearch.common.xcontent.XContent;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;

import java.io.IOException;

/**
 * @author wangyg
 * @time 14:03
 * @note
 **/
public class ElasticUtils {

    /**
     *
     * 官网:https://www.elastic.co/guide/en/elasticsearch/reference/current/mapping.html
     * https://www.cnblogs.com/LQBlog/p/10648496.html
     *
     * mapping是类似于数据库中的表结构定义，主要作用如下：
     * 定义index下的字段名
     * 定义字段类型，比如数值型、浮点型、布尔型等
     * 定义倒排索引相关的设置，比如是否索引、记录position等
     *
     * text 用于全文索引，该类型的字段将通过分词器进行分词，最终用于构建索引
     * keyword	不分词
     * long	有符号64-bit integer：-2^63 ~ 2^63 - 1
     * integer	有符号32-bit integer，-2^31 ~ 2^31 - 1
     * short	有符号16-bit integer，-32768 ~ 32767
     * byte	 有符号8-bit integer，-128 ~ 127
     * double	64-bit IEEE 754 浮点数
     * float	32-bit IEEE 754 浮点数
     * half_float	16-bit IEEE 754 浮点数
     * boolean	true,false
     * date	https://www.elastic.co/guide/en/elasticsearch/reference/current/mapping-date-format.html
     * binary
     * 该类型的字段把值当做经过 base64 编码的字符串，默认不存储，且不可搜索
     *
     * @return 创建仅包含一个message且全文索引的字段
     */
    public static XContentBuilder getIndexMapping() throws IOException {
        XContentBuilder builder = XContentFactory.jsonBuilder();
        builder.startObject();
        {
            builder.startObject("properties");
            {
                builder.startObject("id");
                {
                    builder.field("type","long");
                }
                builder.endObject();

                builder.startObject("orderNo");
                {
                    builder.field("type","keyword");
                }
                builder.endObject();

                builder.startObject("userId");
                {
                    builder.field("type","keyword");
                }
                builder.endObject();

                builder.startObject("userName");
                {
                    builder.field("type","text");
                }
                builder.endObject();

                builder.startObject("createTime");
                {
                    builder.field("type","keyword");
                }
                builder.endObject();

            }
            builder.endObject();
        }
        builder.endObject();

        return builder;
    }

    public static void main(String[] args) throws IOException {
        //System.out.println(ElasticUtils.getIndexMapping());;
        //String pattern="yyyy-MM-dd HH:mm:ss";
        System.out.println(getIndexMapping().toString());
    }

}
