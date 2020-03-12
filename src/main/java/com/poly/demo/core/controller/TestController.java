package com.poly.demo.core.controller;

import org.springframework.web.bind.annotation.*;

/**
 * @author wangyg
 * @time 10:28
 * @note
 **/
@RestController
@RequestMapping("/test")
public class TestController {


    @GetMapping("/gettest")
    public String testGet(@RequestParam String id,@RequestParam String name){
        System.out.println("###############id:"+id+" ######name:"+name);
        return "test afjafa aifjasfjaan oiafjapo";
    }

    @PostMapping("/posttest")
    public String testPost(@RequestBody PostParms postParms){
        System.out.println(postParms.toString());
        return "test afjafa aifjasfjaan oiafjapo";
    }

}
