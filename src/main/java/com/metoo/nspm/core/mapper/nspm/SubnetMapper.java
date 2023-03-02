package com.metoo.nspm.core.mapper.nspm;

import com.metoo.nspm.entity.nspm.Subnet;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface SubnetMapper {

    Subnet selectObjById(Long id);

    Subnet selectObjByIp(String ip);

    Subnet selectObjByIpAndMask(@Param("ip") String ip, @Param("mask") Integer mask);

    List<Subnet> selectSubnetByParentId(Long id);

    List<Subnet> selectSubnetByParentIp(Long ip);

    List<Subnet> selectObjByMap(Map params);

    int save(Subnet instance);

    int update(Subnet instance);

    int delete(Long id);

}
