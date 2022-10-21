package com.metoo.nspm.core.topology.mapper;

import com.metoo.nspm.entity.TopologyToken;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface TopologyTokenMapper {

    List<TopologyToken> query(Map map);
}
