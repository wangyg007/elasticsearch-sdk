package com.poly.demo.core.controller;

import lombok.Data;

/**
 * @author wangyg
 * @time 11:10
 * @note
 **/
@Data
public class PostParms {

    String id;
    String name;


    @Override
    public String toString() {
        return "PostParms{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
