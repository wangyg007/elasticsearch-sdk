package com.poly.demo.module.elastic.config;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author wangyg
 * @time 10:11
 * @note
 **/
@Configuration
public class ElasticsearchRestClient {

    @Value("${elasticsearch.time_out}")
    int time_out;

    @Value("${elasticsearch.ip}")
    String ip;

    @Value("${elasticsearch.http_schema}")
    String http_schema;

    @Value("${elasticsearch.address_length}")
    int address_length;

    @Bean
    public RestClientBuilder restClientBuilder(){
        String[] ipPorts= ip.split(":");
        return RestClient.builder(new HttpHost(ipPorts[0],Integer.parseInt(ipPorts[1]),http_schema));
    }

    @Bean
    public RestHighLevelClient highLevelClient(@Autowired final RestClientBuilder restClientBuilder){
        restClientBuilder.setRequestConfigCallback(
                new RestClientBuilder.RequestConfigCallback() {
                    @Override
                    public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder builder) {
                        return builder.setSocketTimeout(time_out);
                    }
                }
        );
        return new RestHighLevelClient(restClientBuilder);
    }


}
