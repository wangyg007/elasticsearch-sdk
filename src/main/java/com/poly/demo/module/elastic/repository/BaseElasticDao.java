package com.poly.demo.module.elastic.repository;

import com.alibaba.fastjson.JSON;
import com.poly.demo.core.utils.ElasticDoc;
import com.poly.demo.core.utils.ElasticUtils;
import javafx.beans.binding.When;
import org.apache.lucene.util.QueryBuilder;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.get.*;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.FuzzyQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author wangyg
 * @time 11:23
 * @note
 * java rest-high-level-api
 * https://www.elastic.co/guide/en/elasticsearch/client/java-rest/7.6/java-rest-high-document-get.html
 *
 *
 * Elasticsearch(ES)有两种连接方式：transport、rest。
 * transport通过TCP方式访问ES(只支持java),rest方式通过http API 访问ES(没有语言限制)。
 * ES官方建议使用rest方式, transport 在7.0版本中不建议使用，在8.X的版本中废弃。
 * 7.x版本去除type,多个type反而减慢搜索的速度
 *
 *
 * 什么是 routing 参数?
 * 当索引一个文档的时候，文档会被存储在一个主分片上。在存储时一般都会有多个主分片。
 * Elasticsearch 如何知道一个文档应该放置在哪个分片呢？这个过程是根据下面的这个公式来决定的：
 * shard = hash(routing) % number_of_primary_shards
 * routing 是一个可变值，默认是文档的 _id ,也可以设置成一个自定义的值
 * number_of_primary_shards 是主分片数量
 * 所有的文档 API 都接受一个叫做 routing 的路由参数，通过这个参数我们可以自定义文档到分片的映射。
 * 一个自定义的路由参数可以用来确保所有相关的文档——例如所有属于同一个用户的文档——都被存储到同一个分片中。
 **/
@Repository
public class BaseElasticDao {

    @Autowired
    RestHighLevelClient restHighLevelClient;

    /**
     * 创建索引
     * @param name 名称
     * @param indexMapping
     * @param shards 分区数
     * @param replicas 副本数
     * @return 1-成功
     * @throws Exception
     */
    public int createIndex(String name, XContentBuilder indexMapping, int shards, int replicas) {

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
     * 保存或更新文档,id相同会覆盖,索引不存在自动创建索引,默认 1 shard,1 replication
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
                IndexRequest indexRequest = new IndexRequest(index)
                        .id(doc.getId()).source(JSON.toJSONString(doc.getData()), XContentType.JSON);
                request.add(indexRequest);
            }
            restHighLevelClient.bulk(request, RequestOptions.DEFAULT);

        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    /**
     * 根据id获取source所有字段
     * @param index
     * @param id
     * @return json
     */
    public String getAllFields(String index,String id){
        try {
            GetRequest request = new GetRequest(index, id);
            GetResponse response = restHighLevelClient.get(request, RequestOptions.DEFAULT);
            if (response.isExists()){
                return response.getSourceAsString();
            }
            return null;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    /**
     * 根据id和列过滤获取部分字段
     * @param index
     * @param id
     * @param includes
     * @return json
     */
    public String getSomeFileds(String index,String id,String[] includes){
        try {

            GetRequest request = new GetRequest(index, id);
            FetchSourceContext sourceContext = new FetchSourceContext(true, includes, Strings.EMPTY_ARRAY);
            request.fetchSourceContext(sourceContext);
            GetResponse response = restHighLevelClient.get(request, RequestOptions.DEFAULT);
            if (response.isExists()){
                return response.getSourceAsString();
            }
            return null;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    /**
     * 批量获取所有字段
     * @param index
     * @param ids
     * @return json list
     */
    public List<String> getAllFieldsBatch(String index,List<String> ids){
        List<String> list = new ArrayList<>();
        try {
            MultiGetRequest request = new MultiGetRequest();
            for (String id:ids){
                request.add(new MultiGetRequest.Item(index,id));
            }
            MultiGetResponse response = restHighLevelClient.mget(request, RequestOptions.DEFAULT);
            Iterator<MultiGetItemResponse> iterator = response.iterator();
            while (iterator.hasNext()){
                MultiGetItemResponse itemResponse = iterator.next();
                GetResponse getResponse = itemResponse.getResponse();
                if (itemResponse.getResponse().isExists()){
                    list.add(getResponse.getSourceAsString());
                }
            }
            return list;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }


    /**
     * 批量获取部分字段
     * @param index
     * @param ids
     * @param includes
     * @return json list
     */
    public List<String> getSomeFieldsBatch(String index,List<String> ids,String[] includes){
        List<String> list = new ArrayList<>();
        try {
            MultiGetRequest request = new MultiGetRequest();
            FetchSourceContext fetchSourceContext = new FetchSourceContext(true, includes, Strings.EMPTY_ARRAY);
            for (String id:ids){
                request.add(new MultiGetRequest.Item(index,id).fetchSourceContext(fetchSourceContext));
            }
            MultiGetResponse response = restHighLevelClient.mget(request, RequestOptions.DEFAULT);
            Iterator<MultiGetItemResponse> iterator = response.iterator();
            while (iterator.hasNext()){
                MultiGetItemResponse itemResponse = iterator.next();
                GetResponse getResponse = itemResponse.getResponse();
                if (itemResponse.getResponse().isExists()){
                    list.add(getResponse.getSourceAsString());
                }
            }
            return list;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    /**
     * 查询
     * @param index
     * @param field
     * @param pattern
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> List<T> search(String index,String field,String pattern,Class<T> clazz){
        List<T> list=new ArrayList<>();
        try {
            SearchRequest request = new SearchRequest(index);
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
//            FuzzyQueryBuilder queryBuilder = QueryBuilders.fuzzyQuery(field,pattern);
            sourceBuilder.query(QueryBuilders.matchQuery(field,pattern));
            sourceBuilder.size(10);
            sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
            request.source(sourceBuilder);

            SearchResponse response = restHighLevelClient.search(request,RequestOptions.DEFAULT);
            SearchHits hits = response.getHits();
            SearchHit[] searchHits = hits.getHits();
            if (null!=searchHits && searchHits.length>0){
                for(SearchHit hit : searchHits){
                    list.add(JSON.parseObject(hit.getSourceAsString(),clazz));
                }
            }
            return list;
        }catch (Exception e){
            throw new RuntimeException(e);
        }

    }



}
