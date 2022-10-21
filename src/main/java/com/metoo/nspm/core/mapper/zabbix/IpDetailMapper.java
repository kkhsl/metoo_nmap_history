package com.metoo.nspm.core.mapper.zabbix;

import com.metoo.nspm.entity.zabbix.IpDetail;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface IpDetailMapper {

    List<IpDetail> selectObjByMap(Map map);

    IpDetail selectObjByMac(String mac);

    IpDetail selectObjByIp(String ip);

    int save(IpDetail instance);

    int update(IpDetail instance);

    void truncateTable();
}
