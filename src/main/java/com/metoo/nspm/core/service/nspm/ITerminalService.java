package com.metoo.nspm.core.service.nspm;

import com.metoo.nspm.entity.nspm.Mac;
import com.metoo.nspm.entity.nspm.Terminal;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ITerminalService {

    List<Terminal> selectObjByMap(Map params);

    int insert(Terminal instance);

    int update(Terminal instance);

    int batchInert(List<Terminal> instances);

    int batchUpdate(List<Terminal> instances);

    // 同步DT信息
    void syncMacToTerminal();

    void syncHistoryMac(Date time);
}
