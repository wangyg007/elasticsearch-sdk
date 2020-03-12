package com.poly.demo.module.elastic.model;

import lombok.Data;

/**
 * @author wangyg
 * @time 16:42
 * @note
 **/
@Data
public class RequestLog {

    private Long id;

    private String orderNo;

    private String userId;

    private String userName;

    private String createTime;


}
