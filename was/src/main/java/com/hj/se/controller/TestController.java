package com.hj.se.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @see 테스트 컨트롤러
 * @author hojin cho
 */
@RestController
@RequestMapping("api/test")
public class TestController {
    
    public TestController() {

    }

    @RequestMapping(value= "/hello", method = RequestMethod.GET, consumes = "application/json")
    public String Hello() {
        return "Hello~";
    }

    

}
