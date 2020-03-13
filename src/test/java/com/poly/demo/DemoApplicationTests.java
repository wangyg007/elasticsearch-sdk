package com.poly.demo;

import com.poly.demo.core.utils.DateUtil;
import com.poly.demo.core.utils.ElasticDoc;
import com.poly.demo.core.utils.ElasticUtils;
import com.poly.demo.module.elastic.config.ElasticsearchRestClient;
import com.poly.demo.module.elastic.model.RequestLog;
import com.poly.demo.module.elastic.repository.BaseElasticDao;
import io.github.swagger2markup.Language;
import io.github.swagger2markup.Swagger2MarkupConfig;
import io.github.swagger2markup.Swagger2MarkupConverter;
import io.github.swagger2markup.builder.Swagger2MarkupConfigBuilder;
import io.github.swagger2markup.markup.builder.MarkupLanguage;
import org.elasticsearch.common.Strings;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DemoApplication.class)
public class DemoApplicationTests {

    @Autowired
    ElasticsearchRestClient elasticsearchRestClient;

    @Resource
    BaseElasticDao baseElasticDao;

    @Test
    public void testEs() throws Exception {
//                System.out.println(baseElasticDao.createIndex("request_log_index",
//                        ElasticUtils.getIndexMapping(),3,2));
        //System.out.println(baseElasticDao.delIndex("request_log_oks"));
//        String[] aa=new String[]{"elastic","java","hive","hadoop","yarn","kafka","python"};
//        List<ElasticDoc> list=new ArrayList<>();
//        for (int i=0;i<100;i++){
//            ElasticDoc doc = new ElasticDoc();
//            RequestLog log = new RequestLog();
//            log.setId(Long.valueOf(i));
//            log.setOrderNo(UUID.randomUUID().toString());
//            Random random = new Random();
//            log.setUserName(aa[random.nextInt(7)]);
//            log.setUserId(UUID.randomUUID().toString());
//            log.setCreateTime(DateUtil.datetime2Str(DateUtil.now()));
//
//            doc.setId(i+"");
//            doc.setData(log);
//            list.add(doc);
//        }
//        baseElasticDao.insertBatch("request_log_index",list);
//        System.out.println(baseElasticDao.getSomeFileds("request_log_index","1",
//                new String[]{"id","userName"}));

//        List<String> ids=new ArrayList<>();
//        ids.add("1");ids.add("20");ids.add("30");ids.add("40");ids.add("50");ids.add("60");ids.add("99");
//        System.out.println(baseElasticDao.getSomeFieldsBatch("request_log_index",ids,new String[]{"id","userName"}));
        List<RequestLog> logs = baseElasticDao.search("request_log_index", "userName", "ja", RequestLog.class);
        for (RequestLog log:logs){
            System.out.println(log);
        }


    }


    @Test
    public void generateAsciiDocsToFile() throws Exception {

        // 输出Ascii到单文件
        Swagger2MarkupConfig config = new Swagger2MarkupConfigBuilder()
                .withMarkupLanguage(MarkupLanguage.ASCIIDOC)
                .withOutputLanguage(Language.ZH) //选择语言为中文
                .withGeneratedExamples() //选择需要输出入参和返回值样例
                .build();
        //localhost和8080可以改成指定的ip和端口号
        Swagger2MarkupConverter.from(new URL("http://localhost:8585/data-center/v2/api-docs"))
                .withConfig(config)
                .build()
                //这里是输出文档的位置
                .toFile(Paths.get("src/docs/asciidoc/generated/all"));
    }



}
