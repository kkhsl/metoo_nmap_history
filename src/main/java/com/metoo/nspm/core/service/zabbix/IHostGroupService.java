package com.metoo.nspm.core.service.zabbix;

import com.alibaba.fastjson.JSONObject;
import com.metoo.nspm.dto.zabbix.HostGroupDTO;

public interface IHostGroupService {

    JSONObject get(HostGroupDTO dto);

    String getHostGroupId();
}
