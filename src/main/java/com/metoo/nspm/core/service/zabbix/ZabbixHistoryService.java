package com.metoo.nspm.core.service.zabbix;

import com.metoo.nspm.dto.zabbix.HistoryDTO;

public interface ZabbixHistoryService {

    Object getHistory(HistoryDTO dto);
}
