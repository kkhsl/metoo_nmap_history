package com.metoo.nspm.core.mapper;

import com.metoo.nspm.entity.VirtualType;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface VirtualTypeMapper {

    VirtualType getObjById(Long id);

    List<VirtualType> selectByMap(Map map);
}
