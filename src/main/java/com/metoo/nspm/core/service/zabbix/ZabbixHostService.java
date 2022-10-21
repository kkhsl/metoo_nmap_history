package com.metoo.nspm.core.service.zabbix;

import com.metoo.nspm.dto.zabbix.HostDTO;

public interface ZabbixHostService {

    Object getHost(HostDTO dto);

    String getHostId(String ip);

    boolean getHostMaintenanceStatus(String ip);

    boolean verifyHost(String ip);

    boolean deleteHost(String ip);

}
