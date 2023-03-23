package com.metoo.nspm.core.service.nspm;

import com.metoo.nspm.entity.nspm.IpAlteration;

import java.util.List;
import java.util.Map;

public interface IpAlterationService {


    List<IpAlteration> selectObjByMap(Map params);

    List<IpAlteration> selectObjByAddTime(Map params);

    void RecordChange(String ip, String mac);
}
