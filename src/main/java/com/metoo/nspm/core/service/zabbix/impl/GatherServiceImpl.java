package com.metoo.nspm.core.service.zabbix.impl;

import com.metoo.nspm.core.manager.admin.tools.DateTools;
import com.metoo.nspm.core.service.api.zabbix.ZabbixItemService;
import com.metoo.nspm.core.service.nspm.*;
import com.metoo.nspm.core.service.zabbix.IGatherService;
import com.metoo.nspm.core.service.zabbix.ItemService;
import com.metoo.nspm.core.utils.SystemOutputLogUtils;
import com.metoo.nspm.entity.nspm.ArpTemp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
@Service
//@Transactional
public class GatherServiceImpl implements IGatherService {

    Logger log = LoggerFactory.getLogger(GatherServiceImpl.class);

    @Autowired
    private ItemService itemService;
    @Autowired
    private ZabbixItemService zabbixItemService;
    @Autowired
    private IArpService arpService;
    @Autowired
    private IArpHistoryService arpHistoryService;
    @Autowired
    private IMacService macService;
    @Autowired
    private IMacHistoryService macHistoryService;
    @Autowired
    private IRoutService routService;
    @Autowired
    private IRoutHistoryService routHistoryService;
    @Autowired
    private IArpTempService arpTempService;
    @Autowired
    private IIPAddressService ipAddressService;
    @Autowired
    private IIPAddressHistoryService ipAddressHistoryService;

    @Override
    public void gatherArpItem(Date time) {
        this.itemService.gatherArpItem(time);
        // 打标签
        this.zabbixItemService.arpTag();
        // 同步到arp
        this.arpService.truncateTable();
        this.arpService.copyArpTemp();
        // 记录历史Arp
        this.arpHistoryService.copyArpTemp();

    }

    @Override
    public void gatherMacItem(Date time) {
        Long startTime = System.currentTimeMillis();
        log.info("Mac采集开始：" + DateTools.getCurrentDateByCh(startTime));
        this.itemService.gatherMacItem(time);
        log.info("Mac采集结束，采集时间为：" + (System.currentTimeMillis() - startTime) / 60 / 1000 + " 分钟"
                + (System.currentTimeMillis() - startTime) / 1000 + "秒 ");

        Long tagTime = System.currentTimeMillis();
        log.info("Mac-Tag 开始：" + DateTools.getCurrentDateByCh(tagTime));
        this.zabbixItemService.macTag();
        log.info("Mac-Tag 结束，采集时间为：" + (System.currentTimeMillis() - tagTime) / 60 / 1000 + " 分钟"
                + (System.currentTimeMillis() - tagTime) / 1000 + "秒 ");

        Long topologyTime = System.currentTimeMillis();
        log.info("Mac-Topology 开始：" + DateTools.getCurrentDateByCh(topologyTime));
        this.itemService.topologySyncToMac();
        log.info("Mac-Topology 结束，采集时间为：" + (System.currentTimeMillis() - topologyTime) / 60 / 1000 + " 分钟"
                + (System.currentTimeMillis() - topologyTime) / 1000 + "秒 ");

        Long copyTime = System.currentTimeMillis();
        log.info("Mac-copy 开始：" + DateTools.getCurrentDateByCh(copyTime));
        // 同步网元数据到Mac
        this.macService.truncateTable();
        this.macService.copyMacTemp();
        // 记录历史
        this.macHistoryService.copyMacTemp();
        log.info("Mac-copy 结束，采集时间为：" + (System.currentTimeMillis() - copyTime) / 60 / 1000 + " 分钟"
                + (System.currentTimeMillis() - copyTime) / 1000 + "秒 ");

    }

    @Override
    public void gatherMacBatch(Date time) {
        Long gatherTime = System.currentTimeMillis();
        SystemOutputLogUtils.start(log, gatherTime, "Zabbix:Mac采集开始");

        Long start = System.currentTimeMillis();
        SystemOutputLogUtils.start(log, start, "Mac采集开始");
        this.itemService.gatherMacBatch(time);
        SystemOutputLogUtils.diff(log, start, System.currentTimeMillis(), "Mac 采集结束");

        start = System.currentTimeMillis();
        SystemOutputLogUtils.start(log, start, "Mac-Tag 采集开始");
        this.zabbixItemService.labelTheMac();// 批量插入
        SystemOutputLogUtils.diff(log, start, System.currentTimeMillis(), "Mac-Tag 采集结束");

        start = System.currentTimeMillis();
        SystemOutputLogUtils.start(log, start, "Mac-Topology 采集开始");
        this.itemService.topologySyncToMacBatch(time);
        SystemOutputLogUtils.diff(log, start, System.currentTimeMillis(), "Mac-Topology 采集结束");

        start = System.currentTimeMillis();
        SystemOutputLogUtils.start(log, start, "Mac-copy 采集开始");
        // 同步网元数据到Mac
        this.macService.truncateTable();
        this.macService.copyMacTemp();
        // 记录历史
        this.macHistoryService.copyMacTemp();
        SystemOutputLogUtils.diff(log, start, System.currentTimeMillis(), "Mac-copy 采集结束");

        SystemOutputLogUtils.diff(log, gatherTime, System.currentTimeMillis(), "Zabbix:mac 采集结束");
    }

    @Override
    public void gatherMacBatchStream(Date time) {
        Long gatherTime = System.currentTimeMillis();
        SystemOutputLogUtils.start(log, gatherTime, "Zabbix:Mac采集开始");

        Long start = System.currentTimeMillis();
        SystemOutputLogUtils.start(log, start, "Mac采集开始");
        this.itemService.gatherMacBatchStream(time);
        SystemOutputLogUtils.diff(log, start, System.currentTimeMillis(), "Mac 采集结束");

        start = System.currentTimeMillis();
        SystemOutputLogUtils.start(log, start, "Mac-Tag 采集开始");
        this.zabbixItemService.labelTheMac();// 批量插入
        SystemOutputLogUtils.diff(log, start, System.currentTimeMillis(), "Mac-Tag 采集结束");

        start = System.currentTimeMillis();
        SystemOutputLogUtils.start(log, start, "Mac-Topology 采集开始");
        this.itemService.topologySyncToMacBatch(time);
        SystemOutputLogUtils.diff(log, start, System.currentTimeMillis(), "Mac-Topology 采集结束");

        start = System.currentTimeMillis();
        SystemOutputLogUtils.start(log, start, "Mac-copy 采集开始");
        // 同步网元数据到Mac
        this.macService.truncateTable();
        this.macService.copyMacTemp();
        // 记录历史
        this.macHistoryService.copyMacTemp();
        SystemOutputLogUtils.diff(log, start, System.currentTimeMillis(), "Mac-copy 采集结束");

        SystemOutputLogUtils.diff(log, gatherTime, System.currentTimeMillis(), "Zabbix:mac 采集结束");
    }

    @Override
    public void gatherRouteItem(Date time) {
        this.itemService.gatherRouteItem(time);
        this.routService.truncateTable();
        this.routService.copyRoutTemp();
        this.routHistoryService.copyRoutTemp();

    }

    @Override
    public void gatherIpaddressItem(Date time) {
        this.itemService.gatherIpaddressItem(time);
        this.ipAddressService.truncateTable();
        this.ipAddressService.copyIpAddressTemp();
        // 记录历史Ipaddress
        this.ipAddressHistoryService.copyIpAddressTemp();
    }

    @Override
    public void gatherProblemItem(Date time) {
        this.itemService.gatherProblemItem(time);
//        this.ipAddressService.truncateTable();
//        this.ipAddressService.copyIpAddressTemp();
//        // 记录历史Ipaddress
//        this.ipAddressHistoryService.copyIpAddressTemp();
    }

    @Override
    public void testTransactional() {
        ArpTemp arpTemp = new ArpTemp();
        arpTemp.setDeviceName("a");
        this.arpTempService.save(arpTemp);
        this.itemService.testTransactional();
    }
}
