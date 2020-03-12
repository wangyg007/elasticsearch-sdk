package com.poly.demo.module.elastic.repository;

import com.alibaba.fastjson.JSON;
import com.poly.demo.core.utils.ElasticDoc;
import com.poly.demo.core.utils.ElasticUtils;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author wangyg
 * @time 11:23
 * @note
 **/
@Repository
public class BaseElasticDao {

    @Autowired
    RestHighLevelClient restHighLevelClient;

    /**
     * 创建索引
     * @param name 名称
     * @param shards 分区数
     * @param replicas 副本数
     * @return 1-成功
     * @throws Exception
     */
    public int createIndex(String name, int shards, int replicas) {

        try {
            if (exsistIndex(name)){
                throw new RuntimeException("index:"+name+" exsisted!!!");
            }
            CreateIndexRequest request = new CreateIndexRequest(name);
            request.settings(Settings.builder()
                    .put("index.number_of_shards",shards)
                    .put("index.number_of_replicas",replicas));
            request.mapping(ElasticUtils.getIndexMapping());
            CreateIndexResponse response = restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);
            if (response.isAcknowledged()){
                return 1;
            }
            return 0;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    /**
     * 删除索引
     * @param name 索引名
     * @return 1-成功
     */
    public int delIndex(String name) {

        try {
            DeleteIndexRequest request = new DeleteIndexRequest(name);

            AcknowledgedResponse response = restHighLevelClient.indices().delete(request, RequestOptions.DEFAULT);
            if (response.isAcknowledged()){
                return 1;
            }
            return 0;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    /**
     * 判断索引是否存在
     * @param name
     * @return
     */
    public boolean exsistIndex(String name){
        try {
            GetIndexRequest request = new GetIndexRequest(name);
            boolean exists = restHighLevelClient.indices().exists(request, RequestOptions.DEFAULT);
            return exists;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    /**
     * 保存或更新文档,id相同会覆盖
     * @param index
     * @param doc
     * @return
     */
    public int insertOrUpdate(String index, ElasticDoc doc){
        int res=0;
        try {
            IndexRequest request = new IndexRequest(index);
            request.id(doc.getId());
            request.source(JSON.toJSONString(doc.getData()),XContentType.JSON);
            IndexResponse response = restHighLevelClient.index(request, RequestOptions.DEFAULT);
            if (response.getResult()== DocWriteResponse.Result.CREATED ||
            response.getResult() == DocWriteResponse.Result.UPDATED){
                res=1;
            }
            return res;
        }catch (Exception e){
            throw new RuntimeException(e);
        }

    }

    /**
     * 批量保存文档,id相同会覆盖
     * @param index
     * @param docs
     */
    public void insertBatch(String index, List<ElasticDoc> docs){
        try {
            BulkRequest request = new BulkRequest();
            for (ElasticDoc doc:docs){
                IndexRequest indexRequest = new IndexRequest(index).id(doc.getId()).source(JSON.toJSONString(doc.getData()), XContentType.JSON);
                request.add(indexRequest);
            }
            restHighLevelClient.bulk(request, RequestOptions.DEFAULT);

        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }




}
