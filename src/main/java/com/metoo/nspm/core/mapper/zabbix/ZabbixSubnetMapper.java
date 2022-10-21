package com.metoo.nspm.core.mapper.zabbix;

import com.metoo.nspm.entity.zabbix.Subnet;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ZabbixSubnetMapper {


    Subnet selectObjById(Long id);

    Subnet selectObjByIp(@Param("ip") String ip, @Param("mask") Integer mask);

    List<Subnet> selectSubnetByParentId(Long id);

    int save(Subnet instance);

    int delete(Long id);

}
