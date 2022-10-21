package com.metoo.nspm.core.mapper;

import com.metoo.nspm.dto.NetworkElementDto;
import com.metoo.nspm.entity.NetworkElement;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface NetworkElementMapper {

    NetworkElement selectObjById(Long id);

    List<NetworkElement> selectConditionQuery(NetworkElementDto instance);

    List<NetworkElement> selectObjByMap(Map params);

    int save(NetworkElement instance);

    int update(NetworkElement instance);

    int del(Long id);
}
