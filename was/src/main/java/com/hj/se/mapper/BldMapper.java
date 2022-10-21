package com.hj.se.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;
import java.util.List;

@Mapper
public interface BldMapper {
    
    List<LinkedHashMap<String, Object>> getBldListLike(
            @Param("bldName") String bldName
    );
}
