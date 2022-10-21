package com.metoo.nspm.core.service.zabbix.impl;

import com.metoo.nspm.core.manager.zabbix.utils.ZabbixApiUtil;
import com.metoo.nspm.core.service.zabbix.ZabbixHistoryService;
import com.metoo.nspm.dto.zabbix.HistoryDTO;
import io.github.hengyunabc.zabbix.api.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ZabbixHistoryServiceImpl implements ZabbixHistoryService {

    @Autowired
    private ZabbixApiUtil zabbixApiUtil;

    @Override
    public Object getHistory(HistoryDTO dto) {
        Request request = this.zabbixApiUtil.parseParam(dto, "history.get");
        return zabbixApiUtil.call(request);
    }
}
