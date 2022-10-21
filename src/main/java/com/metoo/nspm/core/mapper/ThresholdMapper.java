package com.metoo.nspm.core.mapper;

import com.metoo.nspm.entity.Threshold;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ThresholdMapper {

    Threshold query();

    int update(Threshold instance);
}
