package com.metoo.nspm.core.websocket.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.metoo.nspm.core.config.redis.util.MyRedisManager;
import com.metoo.nspm.core.config.websocket.demo.NoticeWebsocketResp;
import com.metoo.nspm.core.manager.admin.tools.DateTools;
import com.metoo.nspm.core.manager.admin.tools.MacUtil;
import com.metoo.nspm.core.manager.admin.tools.Md5Crypt;
import com.metoo.nspm.core.service.api.zabbix.ZabbixService;
import com.metoo.nspm.core.service.nspm.*;
import com.metoo.nspm.core.service.zabbix.ItemService;
import com.metoo.nspm.core.utils.network.IpUtil;
import com.metoo.nspm.entity.nspm.*;
import com.metoo.nspm.entity.zabbix.Item;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RequestMapping("/websocket/api/zabbix")
@RestController
public class TopologyManagerApi {

    @Autowired
    private IMacService macService;
    @Autowired
    private IMacHistoryService macHistoryService;
    @Autowired
    private IMacVendorService macVendorService;
    @Autowired
    private INetworkElementService networkElementService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private ZabbixService zabbixService;
    @Autowired
    private MacUtil macUtil;
    @Autowired
    private ISubnetService subnetService;
    @Autowired
    private IVlanService vlanService;
    @Autowired
    private ITerminalService terminalService;
    @Autowired
    private ITerminalTypeService terminalTypeService;

    @Autowired
    private static MyRedisManager redisWss = new MyRedisManager("ws");

//    @ApiOperation("设备 Mac (DT))")
//    @GetMapping(value = {"/mac/dt"})
//    public NoticeWebsocketResp getObjMac(@RequestParam(value = "requestParams", required = false) String requestParams){
//        Map params = JSONObject.parseObject(String.valueOf(requestParams), Map.class);
//        Date time = DateTools.parseDate(String.valueOf(params.get("time")), "yyyy-MM-dd HH:mm");
//        List<String> list = JSONObject.parseObject(String.valueOf(params.get("params")), List.class);
//        Map map = new HashMap();
//        Map args = new HashMap();
//        if(time == null){
//            for (String uuid : list) {
//                Map flux_terminal = new HashMap();
//                args.clear();
//                args.put("uuid", uuid);
//                List<Terminal> terminals = this.terminalService.selectObjByMap(args);
//                terminals.stream().forEach(item -> {
//                    String terminalIp = item.getIp();
//                    if(StringUtils.isNotEmpty(terminalIp) && StringUtils.isEmpty(item.getVlan())){
//                        // 获取网络地址
//                        String network = IpUtil.getNBIP(terminalIp,"255.255.255.255", 0);
//                        Subnet subnet = this.subnetService.selectObjByIp(network);
//                        if(subnet != null){
//                            if(subnet.getVlanId() != null && !subnet.getVlanId().equals("")){
//                                Vlan vlan = this.vlanService.selectObjById(subnet.getVlanId());
//                                if(vlan != null){
//                                    item.setVlan(vlan.getName());
//                                }
//                            }
//                        }
//                    }
//                });
//                this.macUtil.terminalJoint(terminals);
//                terminals.stream().forEach(e ->{
//                    TerminalType terminalType = this.terminalTypeService.selectObjById(e.getTerminalTypeId());
//                    e.setTerminalTypeName(terminalType.getName());
//                });
//                flux_terminal.put("terminal", terminals);
//                // 流量
//                Map flux = new HashMap();
//                params.clear();
//                NetworkElement ne = this.networkElementService.selectObjByUuid(uuid);
//                if(ne != null){
//                    args.put("ip", ne.getIp());
//                    // 采集ifbasic,然后查询端口对应的历史流量
//                    args.put("tag", "ifreceived");
//                    args.put("available", 1);
//                    List<Item> items = this.itemService.selectTagByMap(args);
//                    //  Map ele = new HashMap();
//                    if(items.size() > 0){
//                        for (Item item : items) {
//                            String lastvalue = this.zabbixService.getItemLastvalueByItemId(item.getItemid().intValue());
//                            flux.put("received", lastvalue);
//                            break;
//                        }
//                    } else{
//                        flux.put("received", "0");
//                    }
//                    args.clear();
//                    args.put("ip", ne.getIp());
//                    // 采集ifbasic,然后查询端口对应的历史流量
//                    args.put("tag", "ifsent");
//                    args.put("available", 1);
//                    List<Item> ifsents = this.itemService.selectTagByMap(args);
//                    if(ifsents.size() > 0){
//                        for (Item item : ifsents) {
//                            String lastvalue = this.zabbixService.getItemLastvalueByItemId(item.getItemid().intValue());
//                            flux.put("sent", lastvalue);
//                            break;
//                        }
//                    }else{
//                        flux.put("sent", "0");
//                    }
//                }
//                flux_terminal.put("flux", flux);
//                map.put(uuid, flux_terminal);
//            }
//        }else{
//            for (String item : list) {
//                args.clear();
//                args.put("uuid", item);
//                args.put("time", time);
//                List<Mac> macs = this.macHistoryService.selectByMap(args);
//                this.macUtil.macJoint(macs);
//                map.put(item, macs);
//            }
//        }
//
//        NoticeWebsocketResp rep = new NoticeWebsocketResp();
//        if(map.size() > 0){
//            rep.setNoticeType("4");
//            rep.setNoticeStatus(1);
//            rep.setNoticeInfo(map);
//        }else{
//            rep.setNoticeType("4");
//            rep.setNoticeStatus(0);
//        }
//        return rep;
//    }

//    @ApiOperation("设备 Mac (DT))")
//    @GetMapping(value = {"/mac/dt"})
//    public NoticeWebsocketResp getObjMac(@RequestParam(value = "requestParams", required = false) String requestParams){
//        Map params = JSONObject.parseObject(String.valueOf(requestParams), Map.class);
//        Date time = DateTools.parseDate(String.valueOf(params.get("time")), "yyyy-MM-dd HH:mm");
//        List<String> list = JSONObject.parseObject(String.valueOf(params.get("params")), List.class);
//        Map map = new HashMap();
//        Map args = new HashMap();
//        if(time == null){
//            for (String uuid : list) {
//                Map flux_terminal = new HashMap();
//                args.clear();
//                args.put("uuid", uuid);
//                args.put("tag", "DT");
//                List<Mac> macs = this.macService.selectByMap(args);
//                macs.stream().forEach(item -> {
//                    String terminalIp = item.getIp();
//                    if(StringUtils.isNotEmpty(terminalIp) && StringUtils.isEmpty(item.getVlan())){
//                        // 获取网络地址
//                        String network = IpUtil.getNBIP(terminalIp,"255.255.255.255", 0);
//                        Subnet subnet = this.subnetService.selectObjByIp(network);
//                        if(subnet != null){
//                            if(subnet.getVlanId() != null && !subnet.getVlanId().equals("")){
//                                Vlan vlan = this.vlanService.selectObjById(subnet.getVlanId());
//                                if(vlan != null){
//                                    item.setVlan(vlan.getName());
//                                }
//                            }
//                        }
//                    }
//                });
//                this.macUtil.macJoint(macs);
//                this.macUtil.writerType(macs);
//                flux_terminal.put("terminal", macs);
//                // 流量
//                Map flux = new HashMap();
//                params.clear();
//                NetworkElement ne = this.networkElementService.selectObjByUuid(uuid);
//                if(ne != null){
//                    args.put("ip", ne.getIp());
//                    // 采集ifbasic,然后查询端口对应的历史流量
//                    args.put("tag", "ifreceived");
//                    args.put("available", 1);
//                    List<Item> items = this.itemService.selectTagByMap(args);
//                    //  Map ele = new HashMap();
//                    if(items.size() > 0){
//                        for (Item item : items) {
//                            String lastvalue = this.zabbixService.getItemLastvalueByItemId(item.getItemid().intValue());
//                            flux.put("received", lastvalue);
//                            break;
//                        }
//                    } else{
//                        flux.put("received", "0");
//                    }
//                    args.clear();
//                    args.put("ip", ne.getIp());
//                    // 采集ifbasic,然后查询端口对应的历史流量
//                    args.put("tag", "ifsent");
//                    args.put("available", 1);
//                    List<Item> ifsents = this.itemService.selectTagByMap(args);
//                    if(ifsents.size() > 0){
//                        for (Item item : ifsents) {
//                            String lastvalue = this.zabbixService.getItemLastvalueByItemId(item.getItemid().intValue());
//                            flux.put("sent", lastvalue);
//                            break;
//                        }
//                    }else{
//                        flux.put("sent", "0");
//                    }
//                }
//                flux_terminal.put("flux", flux);
//                map.put(uuid, flux_terminal);
//            }
//        }else{
//            for (String item : list) {
//                args.clear();
//                args.put("uuid", item);
//                args.put("tag", "DT");
//                args.put("time", time);
//                List<Mac> macs = this.macHistoryService.selectByMap(args);
//                this.macUtil.macJoint(macs);
//                this.macUtil.writerType(macs);
//                map.put(item, macs);
//            }
//        }
//
//        NoticeWebsocketResp rep = new NoticeWebsocketResp();
//        if(map.size() > 0){
//            rep.setNoticeType("4");
//            rep.setNoticeStatus(1);
//            rep.setNoticeInfo(map);
//        }else{
//            rep.setNoticeType("4");
//            rep.setNoticeStatus(0);
//        }
//        return rep;
//    }

//    @ApiOperation("设备 Mac (DT))")
//    @GetMapping(value = {"/mac/dt"})
//    public NoticeWebsocketResp getObjMac(@RequestParam(value = "requestParams", required = false) String requestParams) throws Exception {
//
//        Map params = JSONObject.parseObject(String.valueOf(requestParams), Map.class);
//        Date time = DateTools.parseDate(String.valueOf(params.get("time")), "yyyy-MM-dd HH:mm");
//        List<String> list = JSONObject.parseObject(String.valueOf(params.get("params")), List.class);
//        Map map = new HashMap();
//        Map args = new HashMap();
//        if(time == null){
//            for (String uuid : list) {
//                Map flux_terminal = new HashMap();
//                args.clear();
//                args.put("uuid", uuid);
//                args.put("online", 1);
//                args.put("interfaceStatus", 1);
//                args.put("tag", "DT");
////                List<Mac> macs = this.macService.selectByMap(args);
//                List<Terminal> terminals = this.terminalService.selectObjByMap(args);
//                terminals.stream().forEach(item -> {
//                    String terminalIp = item.getIp();
//                    if(StringUtils.isNotEmpty(terminalIp) && StringUtils.isEmpty(item.getVlan())){
//                        // 获取网络地址
//                        String network = IpUtil.getNBIP(terminalIp,"255.255.255.255", 0);
//                        Subnet subnet = this.subnetService.selectObjByIp(network);
//                        if(subnet != null){
//                            if(subnet.getVlanId() != null && !subnet.getVlanId().equals("")){
//                                Vlan vlan = this.vlanService.selectObjById(subnet.getVlanId());
//                                if(vlan != null){
//                                    item.setVlan(vlan.getName());
//                                }
//                            }
//                        }
//                    }
//                });
//                this.macUtil.terminalJoint(terminals);
//                terminals.stream().forEach(e -> {
//                    if(e.getTerminalTypeId() != null
//                            && !e.getTerminalTypeId().equals("")){
//                        TerminalType terminalType = this.terminalTypeService.selectObjById(e.getTerminalTypeId());
//                        e.setTerminalTypeName(terminalType.getName());
//                    }
//                });
//                flux_terminal.put("terminal", terminals);
//                // 流量
//                Map flux = new HashMap();
//                params.clear();
//                NetworkElement ne = this.networkElementService.selectObjByUuid(uuid);
//                if(ne != null){
//                    args.put("ip", ne.getIp());
//                    // 采集ifbasic,然后查询端口对应的历史流量
//                    args.put("tag", "ifreceived");
//                    args.put("available", 1);
//                    List<Item> items = this.itemService.selectTagByMap(args);
//                    //  Map ele = new HashMap();
//                    if(items.size() > 0){
//                        for (Item item : items) {
//                            String lastvalue = this.zabbixService.getItemLastvalueByItemId(item.getItemid().intValue());
//                            flux.put("received", lastvalue);
//                            break;
//                        }
//                    } else{
//                        flux.put("received", "0");
//                    }
//                    args.clear();
//                    args.put("ip", ne.getIp());
//                    // 采集ifbasic,然后查询端口对应的历史流量
//                    args.put("tag", "ifsent");
//                    args.put("available", 1);
//                    List<Item> ifsents = this.itemService.selectTagByMap(args);
//                    if(ifsents.size() > 0){
//                        for (Item item : ifsents) {
//                            String lastvalue = this.zabbixService.getItemLastvalueByItemId(item.getItemid().intValue());
//                            flux.put("sent", lastvalue);
//                            break;
//                        }
//                    }else{
//                        flux.put("sent", "0");
//                    }
//                }
//                flux_terminal.put("flux", flux);
//                map.put(uuid, flux_terminal);
//            }
//        }else{
//            for (String item : list) {
//                args.clear();
//                args.put("uuid", item);
//                args.put("tag", "DT");
//                args.put("time", time);
//                List<Mac> macs = this.macHistoryService.selectByMap(args);
//                this.macUtil.macJoint(macs);
//                this.macUtil.writerType(macs);
//                map.put(item, macs);
//            }
//        }
//
//        NoticeWebsocketResp rep = new NoticeWebsocketResp();
//        if(map.size() > 0){
//            rep.setNoticeType("4");
//            rep.setNoticeStatus(1);
//            rep.setNoticeInfo(map);
//        }else{
//            rep.setNoticeType("4");
//            rep.setNoticeStatus(0);
//        }
//        return rep;
//    }



    @ApiOperation("设备 Mac (DT))")
    @GetMapping(value = {"/mac/dt"})
    public NoticeWebsocketResp getObjMac(@RequestParam(value = "requestParams", required = false) String requestParams) throws Exception {
        Map params = JSONObject.parseObject(String.valueOf(requestParams), Map.class);
        String sessionid = String.valueOf(params.get("sessionid"));
        Date time = DateTools.parseDate(String.valueOf(params.get("time")), "yyyy-MM-dd HH:mm");
        List<String> list = JSONObject.parseObject(String.valueOf(params.get("params")), List.class);
        Map map = new HashMap();
        Map args = new HashMap();
        if(time == null){
            for (String uuid : list) {
                Map flux_terminal = new HashMap();
                args.clear();
                args.put("uuid", uuid);
                args.put("online", 1);
                args.put("interfaceStatus", 1);
                args.put("tag", "DT");
//                List<Mac> macs = this.macService.selectByMap(args);
                List<Terminal> terminals = this.terminalService.selectObjByMap(args);
                terminals.stream().forEach(item -> {
                    String terminalIp = item.getIp();
                    if(StringUtils.isNotEmpty(terminalIp) && StringUtils.isEmpty(item.getVlan())){
                        // 获取网络地址
                        String network = IpUtil.getNBIP(terminalIp,"255.255.255.255", 0);
                        Subnet subnet = this.subnetService.selectObjByIp(network);
                        if(subnet != null){
                            if(subnet.getVlanId() != null && !subnet.getVlanId().equals("")){
                                Vlan vlan = this.vlanService.selectObjById(subnet.getVlanId());
                                if(vlan != null){
                                    item.setVlan(vlan.getName());
                                }
                            }
                        }
                    }
                });
                this.macUtil.terminalJoint(terminals);
                terminals.stream().forEach(e -> {
                    if(e.getTerminalTypeId() != null
                            && !e.getTerminalTypeId().equals("")){
                        TerminalType terminalType = this.terminalTypeService.selectObjById(e.getTerminalTypeId());
                        e.setTerminalTypeName(terminalType.getName());
                    }
                });
                flux_terminal.put("terminal", terminals);
                // 流量
                Map flux = new HashMap();
                params.clear();
                NetworkElement ne = this.networkElementService.selectObjByUuid(uuid);
                if(ne != null){
                    args.put("ip", ne.getIp());
                    // 采集ifbasic,然后查询端口对应的历史流量
                    args.put("tag", "ifreceived");
                    args.put("available", 1);
                    List<Item> items = this.itemService.selectTagByMap(args);
                    //  Map ele = new HashMap();
                    if(items.size() > 0){
                        for (Item item : items) {
                            String lastvalue = this.zabbixService.getItemLastvalueByItemId(item.getItemid().intValue());
                            flux.put("received", lastvalue);
                            break;
                        }
                    } else{
                        flux.put("received", "0");
                    }
                    args.clear();
                    args.put("ip", ne.getIp());
                    // 采集ifbasic,然后查询端口对应的历史流量
                    args.put("tag", "ifsent");
                    args.put("available", 1);
                    List<Item> ifsents = this.itemService.selectTagByMap(args);
                    if(ifsents.size() > 0){
                        for (Item item : ifsents) {
                            String lastvalue = this.zabbixService.getItemLastvalueByItemId(item.getItemid().intValue());
                            flux.put("sent", lastvalue);
                            break;
                        }
                    }else{
                        flux.put("sent", "0");
                    }
                }
                flux_terminal.put("flux", flux);
                map.put(uuid, flux_terminal);
            }
        }else{
            for (String item : list) {
                args.clear();
                args.put("uuid", item);
                args.put("tag", "DT");
                args.put("time", time);
                List<Mac> macs = this.macHistoryService.selectByMap(args);
                this.macUtil.macJoint(macs);
                this.macUtil.writerType(macs);
                map.put(item, macs);
            }
        }
        if(!sessionid.equals("")){
            String key = sessionid + ":" + "4";
            String key0 = key + ":0";
            Object value = redisWss.get(key0);
            String key1 = "";
            if(value == null || "".equals(value)){
                key1 = key + ":1";
                value = redisWss.get(key1);
            }
            if(value == null || "".equals(value)){
                redisWss.put(key + ":1", map);
            }else{
                boolean flag = Md5Crypt.getDiffrent(value, map);
                if(flag){
                    if(key1 != ""){
                        redisWss.remove(key1);
                    }
                    redisWss.put(key + ":0", map);
                }else{
                    if(key0 != ""){
                        redisWss.remove(key0);
                    }
                    redisWss.put(key + ":1", map);
                }
            }
        }

        NoticeWebsocketResp rep = new NoticeWebsocketResp();
        if(map.size() > 0){
            rep.setNoticeType("4");
            rep.setNoticeStatus(1);
            rep.setNoticeInfo(map);
        }else{
            rep.setNoticeType("4");
            rep.setNoticeStatus(0);
        }
        return rep;
    }
}
