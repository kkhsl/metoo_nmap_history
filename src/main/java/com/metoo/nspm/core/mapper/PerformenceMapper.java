package com.metoo.nspm.core.mapper;

import com.metoo.nspm.entity.Performance;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface PerformenceMapper {

    Performance getObjBy(Map params);
}
