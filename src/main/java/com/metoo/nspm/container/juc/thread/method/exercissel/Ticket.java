package com.metoo.nspm.container.juc.thread.method.exercissel;

import com.metoo.nspm.core.manager.admin.tools.MacUtil;
import com.metoo.nspm.core.manager.myzabbix.utils.ItemUtil;
import com.metoo.nspm.core.mapper.nspm.zabbix.MacMapper;
import com.metoo.nspm.core.mapper.zabbix.ItemMapper;
import com.metoo.nspm.core.mapper.zabbix.ItemTagMapper;
import com.metoo.nspm.core.service.api.zabbix.ZabbixItemService;
import com.metoo.nspm.core.service.nspm.*;
import com.metoo.nspm.core.service.topo.ITopoNodeService;
import com.metoo.nspm.core.service.zabbix.impl.IListUtils;
import com.metoo.nspm.core.service.zabbix.impl.ItemServiceImpl;
import com.metoo.nspm.entity.nspm.MacTemp;
import com.metoo.nspm.entity.zabbix.Item;
import com.metoo.nspm.entity.zabbix.ItemTag;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.CountDownLatch;

@RestController
@RequestMapping("/ttt")
public class Ticket {

    Logger log = LoggerFactory.getLogger(ItemServiceImpl.class);

    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private ItemTagMapper itemTagMapper;
    @Autowired
    private ITopoNodeService topoNodeService;
    @Autowired
    private IpDetailService ipDetailService;
    @Autowired
    private ZabbixItemService zabbixItemService;
    @Autowired
    private ItemUtil itemUtil;
    @Autowired
    private IArpService arpService;
    @Autowired
    private IArpTempService arpTempService;
    @Autowired
    private MacMapper macMapper;
    @Autowired
    private IMacTempService macTempService;
    @Autowired
    private IRoutTempService routTempService;
    @Autowired
    private IIPAddressTempService ipAddressTempService;
    @Autowired
    private ITopologyService topologyService;
    @Autowired
    private MacUtil macUtil;


    @RequestMapping("/ttt")
    public void ttt(){
        List<Map> devices = this.topoNodeService.queryNetworkElement();
        Map params = new HashMap();
        this.macTempService.truncateTable();

        List<MacTemp> batchInsert = new Vector();
        List<Thread> threadList = new ArrayList();
        for (Map map : devices) {
            Thread thread = new Thread(() ->{
                synchronized (this){
                    System.out.println(Thread.currentThread().getName());
                    String deviceName = String.valueOf(map.get("deviceName"));
                    String deviceType = String.valueOf(map.get("deviceType"));
                    String ip = String.valueOf(map.get("ip"));
                    String uuid = String.valueOf(map.get("uuid"));
                    params.clear();
                    params.put("ip", ip);
                    params.put("tag", "macvlan");
                    List<Item> vlanMacList = itemMapper.gatherItemByTag(params);
                    Map<String, String> macMap = null;
                    // 采集：ifbasic
                    params.clear();
                    params.put("ip", ip);
                    params.put("tag", "ifbasic");
                    params.put("tag_relevance", "ifbasic");
                    params.put("index", "ifindex");
                    params.put("index_relevance", "ifindex");
                    params.put("name_relevance", "ifname");
                    List<Item> items = itemMapper.gatherItemByTag(params);
                    if (items.size() > 0) {
                        final Map<String, String> macVlan = macMap;
                        items.parallelStream().forEach(item -> {
                            List<ItemTag> tags = item.getItemTags();
                            MacTemp macTemp = new MacTemp();
                            macTemp.setDeviceName(deviceName);
                            macTemp.setDeviceType(deviceType);
                            macTemp.setUuid(uuid);
                            macTemp.setDeviceIp(ip);
                            macTemp.setTag("L");
                            if (tags != null && tags.size() > 0) {
                                for (ItemTag tag : tags) {
                                    String value = tag.getValue();
                                    if (tag.getTag().equals("ifmac")) {
//                                    格式化mac
                                        if (!value.contains(":")) {
                                            value = value.trim().replaceAll(" ", ":");
                                        }
                                        String mac = macUtil.supplement(value);
                                        macTemp.setMac(mac);
                                        if (macVlan != null && !macVlan.isEmpty()) {
                                            String vlan = macVlan.get(value);
                                            macTemp.setVlan(vlan);
                                        }
                                        if (StringUtils.isNotEmpty(value)) {
                                            macTemp.setType("local");
                                        }
                                    }
                                    if (tag.getTag().equals("ifname")) {
                                        macTemp.setInterfaceName(value);
                                    }
                                    if (tag.getTag().equals("ifindex")) {
                                        macTemp.setIndex(value);
                                    }
                                }
                                // 保存Mac条目
                                if (macTemp.getInterfaceName() != null && !macTemp.getInterfaceName().equals("")
                                        && macTemp.getMac() != null && !macTemp.getMac().equals("{#MAC}")
                                        && !macTemp.getMac().equals("{#IFMAC}")) {
                                    macTemp.setTag("L");
                                    batchInsert.add(macTemp);
                                }
                            }
                        });
                    }

                }
            });
            threadList.add(thread);
            thread.start();
        }

        for (Thread thread : threadList) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(batchInsert.size());

    }



    static Random random = new Random();

    public static int random(int amount){
        return random.nextInt(amount) + 1;
    }
}

class TicketWindow{

    private int count;

    public TicketWindow(int count) {
        this.count = count;
    }

    public int getCount(){
        return this.count;
    }

    public synchronized int sell(int amout){
        if(this.count >= amout){
            this.count -= amout;
            return amout;
        }else{
            return 0;
        }
    }
}

