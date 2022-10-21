package com.hj.se.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.hj.se.mapper.BldMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

/**
 * @see 건물 컨트롤러
 * @author hojin cho
 */
@RestController
@RequestMapping("api/bld")
public class BldController {

    private static final Logger log = LoggerFactory.getLogger(BldController.class);
    private final BldMapper bldMapper;

    public BldController(BldMapper bldMapper) {
        this.bldMapper = bldMapper;
    }

    @PostMapping("/getBldListLike")
    public List<LinkedHashMap<String, Object>> getBldListLike(@RequestBody Map<String, Object> param) throws Exception {

        log.info(param.get("bldName").toString());

        return bldMapper.getBldListLike(param.get("bldName").toString());
    }

    

}
