package com.metoo.nspm.core.service.nspm;

import com.metoo.nspm.entity.nspm.Subnet;

import java.util.List;
import java.util.Map;

public interface ISubnetService {

    Subnet selectObjById(Long id);

    Subnet selectObjByIp(String ip);

    Subnet selectObjByIpAndMask(String ip, Integer mask);

    List<Subnet> selectSubnetByParentId(Long id);

    List<Subnet> selectSubnetByParentIp(Long ip);

    List<Subnet> selectObjByMap(Map params);

    int save(Subnet instance);

    int update(Subnet instance);

    int delete(Long id);


}
