package com.metoo.nspm.core.websocket.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.metoo.nspm.core.config.websocket.demo.NoticeWebsocketResp;
import com.metoo.nspm.core.manager.admin.tools.DateTools;
import com.metoo.nspm.core.manager.admin.tools.GroupTools;
import com.metoo.nspm.core.manager.admin.tools.MacUtil;
import com.metoo.nspm.core.manager.zabbix.tools.InterfaceUtil;
import com.metoo.nspm.core.service.api.zabbix.ZabbixService;
import com.metoo.nspm.core.service.nspm.IGroupService;
import com.metoo.nspm.core.service.nspm.IMacHistoryService;
import com.metoo.nspm.core.service.nspm.IMacService;
import com.metoo.nspm.core.service.nspm.INetworkElementService;
import com.metoo.nspm.core.service.zabbix.IProblemService;
import com.metoo.nspm.core.service.zabbix.InterfaceService;
import com.metoo.nspm.core.service.zabbix.ItemService;
import com.metoo.nspm.core.utils.collections.ListSortUtil;
import com.metoo.nspm.dto.NetworkElementDto;
import com.metoo.nspm.entity.nspm.Group;
import com.metoo.nspm.entity.nspm.Mac;
import com.metoo.nspm.entity.nspm.NetworkElement;
import com.metoo.nspm.entity.zabbix.Interface;
import com.metoo.nspm.entity.zabbix.Item;
import com.metoo.nspm.entity.zabbix.Problem;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * webwocket 网元列表
 */
@RequestMapping("/websocket/api/network")
@RestController
public class NetWorkManagerApi {

    @Autowired
    private INetworkElementService networkElementService;
    @Autowired
    private InterfaceService interfaceService;
    @Autowired
    private IGroupService groupService;
    @Autowired
    private GroupTools groupTools;
    @Autowired
    private InterfaceUtil interfaceUtil;
    @Autowired
    private IProblemService problemService;
    @Autowired
    private IMacService macService;
    @Autowired
    private IMacHistoryService macHistoryService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private ZabbixService zabbixService;
    @Autowired
    private MacUtil macUtil;


    @RequestMapping("/list")
    public NoticeWebsocketResp testApi(@RequestParam(value = "requestParams") String param){
        if(param != null && !param.isEmpty()){
            Map map = JSONObject.parseObject(param, Map.class);
            // 获取类型
            NetworkElementDto dto = JSONObject.parseObject(map.get("params").toString(), NetworkElementDto.class);
            if (dto == null) {
                dto = new NetworkElementDto();
            }
            dto.setUserId(Long.parseLong(map.get("userId").toString()));
            if (dto.getGroupId() != null) {
                Group group = this.groupService.selectObjById(dto.getGroupId());
                if (group != null) {
                    Set<Long> ids = this.groupTools.genericGroupId(group.getId());
                    dto.setGroupIds(ids);
                }
            }
            Page<NetworkElement> page = this.networkElementService.selectConditionQuery(dto);
            Map nes = new HashMap();
            if (page.getResult().size() > 0) {
                for (NetworkElement ne : page.getResult()) {
                    if (ne.getIp() != null) {
                        Interface obj = this.interfaceService.selectObjByIp(ne.getIp());
                        if (obj != null) {
                            ne.setAvailable(obj.getAvailable());
                            ne.setError(obj.getError());
                            nes.put(ne.getIp(), obj.getAvailable());
                        }
                    }
                }
                NoticeWebsocketResp rep = new NoticeWebsocketResp();
                rep.setNoticeType("1");
                rep.setNoticeStatus(1);
                rep.setNoticeInfo(nes);
                return rep;
            }
        }
        NoticeWebsocketResp rep = new NoticeWebsocketResp();
        rep.setNoticeStatus(0);
        return rep;
    }

    @ApiOperation("拓扑端口事件")
    @GetMapping("/interface/event")
    public Object interfaceEvent(@RequestParam(value = "requestParams") String param){
        Map map = new HashMap();
        List list = new ArrayList();
        if(param != null && !param.equals("")){
            List<Object> requestParams = JSONObject.parseObject(param, List.class);
            for (Object requestParam : requestParams){
                Map ele = JSONObject.parseObject(requestParam.toString(), Map.class);
                Set<Map.Entry<String, String>> entrySet = ele.entrySet();
                for(Map.Entry<String, String> entry : entrySet){
                    if(entry.getKey().equals("from") || entry.getKey().equals("to")){
                        String[] data = entry.getValue().split("&");
                        if(data.length >= 2){
                            String ip = data[0];
                            String interfaceName = data[1];
                            // 获取端口事件状态
                            Map params = new HashMap();
                            params.clear();
                            params.put("ip", ip);
                            params.put("interfaceName", interfaceName);
                            params.put("event", "is not null");
                            params.put("objectid", "is not null");
                            List<Problem> problemList = this.problemService.selectObjByMap(params);
                            if(problemList.size() > 0){
                                List events = new ArrayList();
                                for(Problem problem : problemList){
                                    Map event = new HashMap();
                                    if(problem.getEvent().equals("interfacestatus") && problem.getStatus() == 0){
                                        event.put("event", problem.getEvent());
                                        event.put("status", problem.getStatus());
                                        event.put("level",  3);
                                        events.add(event);
                                        break;
                                    }else if(problem.getEvent().equals("interfacestatus") && problem.getStatus() == 1){
                                        event.put("event", problem.getEvent());
                                        event.put("status", problem.getStatus());
                                        event.put("level",  1);
                                    }else if(problem.getEvent().equals("interfacestatus") && problem.getStatus() == 2){
                                        event.put("event", problem.getEvent());
                                        event.put("status", problem.getStatus());
                                        event.put("level",  1);
                                    }else if(problem.getEvent().equals("trafficexceeded") && problem.getStatus() == 0){
                                        event.put("event", problem.getEvent());
                                        event.put("status", problem.getStatus());
                                        event.put("level",  2);
                                    }else if(problem.getEvent().equals("trafficexceeded") && problem.getStatus() == 1){
                                        event.put("event", problem.getEvent());
                                        event.put("status", problem.getStatus());
                                        event.put("level",  1);
                                    }else if(problem.getEvent().equals("trafficexceeded") && problem.getStatus() == 2){
                                        event.put("event", problem.getEvent());
                                        event.put("status", problem.getStatus());
                                        event.put("level",  1);
                                    }
                                    if(event.size() > 0){
                                        events.add(event);
                                    }
                                }
                                if(events.size() == 1){
                                    ele.put(entry.getKey(), events.get(0));
                                }else{
                                    if(events.size() > 0){
                                        ListSortUtil.intSort(events);
                                        ele.put(entry.getKey(), events.get(0));
                                    }else{
                                        Map event = new HashMap();
                                        event.put("status", 2);
                                        ele.put(entry.getKey(), event);
                                    }
                                }
                            }else{
                                Map event = new HashMap();
                                event.put("event", "");
                                event.put("status", 2);
                                ele.put(entry.getKey(), event);
                            }
                        }else{
                            ele.put(entry.getKey(), "");
                        }
                    }
                }
                list.add(ele);
            }
        }
        NoticeWebsocketResp rep = new NoticeWebsocketResp();
        if (list.size() > 0) {
            rep.setNoticeStatus(1);
            rep.setNoticeType("5");
            rep.setNoticeInfo(list);
        }else{
            rep.setNoticeType("5");
            rep.setNoticeStatus(0);
        }
        return rep;
    }

//    @ApiOperation("拓扑端口事件")
//    @GetMapping("/interface/event")
//    public Object interfaceEvent(@RequestParam(value = "requestParams") String param){
//        Map map = new HashMap();
//        if(param != null && !param.equals("")){
//            List<String> requestParams = JSONObject.parseObject(param, List.class);
//            for (String requestParam : requestParams){
//                String[] data = requestParam.split("&");
//                String ip = data[0];
//                String interfaceName = data[1];
//                // 获取端口snmp可用性
////                String avaliable = this.interfaceUtil.getInterfaceAvaliable(ip);
////                result.put("snmp", avaliable);
//                // 获取端口事件状态
//                Map params = new HashMap();
//                params.clear();
//                params.put("ip", ip);
//                params.put("interfaceName", interfaceName);
//                params.put("event", "is not null");
//                List<Problem> problemList = this.problemService.selectObjByMap(params);
//                if(problemList.size() > 0){
//                    List list = new ArrayList();
//                    for(Problem problem : problemList){
//                        Map event = new HashMap();
//                        if(problem.getEvent().equals("interfacestatus") && problem.getStatus() == 0){
//                            event.put("event", problem.getEvent());
//                            event.put("status", problem.getStatus());
//                            event.put("level",  3);
//                            list.add(event);
//                            break;
//                        }else if(problem.getEvent().equals("interfacestatus") && problem.getStatus() == 1){
//                            event.put("event", problem.getEvent());
//                            event.put("status", problem.getStatus());
//                            event.put("level",  1);
//                        }else if(problem.getEvent().equals("interfacestatus") && problem.getStatus() == 2){
//                            event.put("event", problem.getEvent());
//                            event.put("status", problem.getStatus());
//                            event.put("level",  1);
//                        }else if(problem.getEvent().equals("trafficexceeded") && problem.getStatus() == 0){
//                            event.put("event", problem.getEvent());
//                            event.put("status", problem.getStatus());
//                            event.put("level",  2);
//                        }else if(problem.getEvent().equals("trafficexceeded") && problem.getStatus() == 1){
//                            event.put("event", problem.getEvent());
//                            event.put("status", problem.getStatus());
//                            event.put("level",  1);
//                        }else if(problem.getEvent().equals("trafficexceeded") && problem.getStatus() == 2){
//                            event.put("event", problem.getEvent());
//                            event.put("status", problem.getStatus());
//                            event.put("level",  1);
//                        }
//                        if(event.size() > 0){
//                            list.add(event);
//                        }
//                    }
//                    if(list.size() == 1){
//                        map.put(requestParam, list.get(0));
//                    }else{
//                        if(list.size() > 0){
//                            ListSortUtil.intSort(list);
//                            map.put(requestParam, list.get(0));
//                        }else{
//                            Map event = new HashMap();
//                            event.put("status", 2);
//                            map.put(requestParam, event);
//                        }
//                    }
//                }else{
//                    Map event = new HashMap();
//                    event.put("event", "");
//                    event.put("status", 2);
//                    map.put(requestParam, event);
//                }
//            }
//        }
//        NoticeWebsocketResp rep = new NoticeWebsocketResp();
//        if (!map.isEmpty()) {
//            rep.setNoticeStatus(1);
//            rep.setNoticeType("5");
//            rep.setNoticeInfo(map);
//        }else{
//            rep.setNoticeType("5");
//            rep.setNoticeStatus(0);
//        }
//        return rep;
//    }

//    @ApiOperation("拓扑设备状态")
//    @GetMapping("/snmp/status")
//    public Object snmapStatus(String ips){
//        Map map = new HashMap();
//        if(ips != null && !ips.equals("")){
//            String[] iparray = ips.split(",");
//            for (String ip : iparray){
//                // 获取端口snmp可用性
//                String avaliable = this.interfaceUtil.getInterfaceAvaliable(ip);
//                map.put(ip, avaliable);
//            }
//        }
//        NoticeWebsocketResp rep = new NoticeWebsocketResp();
//        if (!map.isEmpty()) {
//            rep.setNoticeStatus(1);
//            rep.setNoticeType("2");
//            rep.setNoticeInfo(map);
//        }else{
//            rep.setNoticeType("2");
//            rep.setNoticeStatus(0);
//        }
//        return rep;
//    }

    @ApiOperation("拓扑设备状态")
    @GetMapping("/snmp/status")
    public Object status(@RequestParam(value = "requestParams") String param){
        List list = new ArrayList();
        if(param != null && !param.equals("")){
            List<Object> requestParams = JSONObject.parseObject(param, List.class);
            for (Object ip : requestParams){
                try {
                    Map map = new HashMap();
                    String[] str = ip.toString().split("&");
                    // 获取端口snmp可用性
                    String avaliable = this.interfaceUtil.getInterfaceAvaliable(str[0]);
                    map.put("snmp", avaliable);
                    map.put("uuid", str[1]);
                    list.add(map);
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
            }
        }
        NoticeWebsocketResp rep = new NoticeWebsocketResp();
        if (list.size() > 0) {
            rep.setNoticeStatus(1);
            rep.setNoticeType("2");
            rep.setNoticeInfo(list);
        }else{
            rep.setNoticeType("2");
            rep.setNoticeStatus(0);
        }
        return rep;
    }

//    @ApiOperation("9：网元|端口列表")
//    @GetMapping("/ne/interface/all")
//    public NoticeWebsocketResp neInterfaces(@RequestParam(value = "requestParams", required = false) String requestParams){
//        Map params = JSONObject.parseObject(requestParams, Map.class);
//        if(!params.isEmpty()){
//            Map subarea = new HashMap();
//            for(Object key : params.keySet()){
//                String value = params.get(key).toString();
//                JSONArray ary = JSONArray.parseArray(value);
//                if(ary.size() > 0){
//                    Map map = new HashMap();
//                    for (Object param : ary) {
//                        JSONObject ele = JSONObject.parseObject(param.toString());
//                        String uuid = ele.getString("uuid");
//                        String time = ele.getString("time");
//                        NetworkElement ne = this.networkElementService.selectObjByUuid(uuid);
//                        if(ne != null){
//                            // 端口列表
//                            String interfaces = ele.getString("interface");
//                            JSONArray array = JSONArray.parseArray(interfaces);
//                            if(array.size() > 0){
//                                Map interfaceMap = new HashMap();
//                                for(Object inf : array){
//                                    Map flux_terminal = new HashMap();
//                                    Map args = new HashMap();
//                                    args.put("uuid", uuid);
//                                    args.put("interfaceName", inf);
//                                    args.put("tag", "DT");
//                                    args.put("uuid", uuid);
//                                    args.put("orderBy", "ip");
//                                    args.put("orderType", "ASC");
//                                    List<Mac> macs = null;
//                                    if(time != null && !"".equals(time)){
//                                        Date date = DateTools.parseDate(time, "yyyy-MM-dd HH:mm");
//                                        args.put("time", date);
//                                        macs = this.macHistoryService.selectObjByMap(args);
//                                    }else{
//                                        macs = this.macService.selectByMap(args);
//                                    }
//                                    if(macs != null && macs.size() > 0){
//                                        macUtil.macJoint(macs);
//                                        flux_terminal.put("terminal", macs);
//                                    }else{
//                                        List a = new ArrayList<>();
//                                        flux_terminal.put("terminal", a);
//                                    }
//                                    if(time == null || "".equals(time)){
//                                        // 流量
//                                        Map flux = new HashMap();
//                                        args.clear();
//                                        args.put("ip", ne.getIp());
//                                        // 采集ifbasic,然后查询端口对应的历史流量
//                                        args.put("tag", "ifreceived");
//                                        args.put("available", 1);
//                                        List<Item> items = this.itemService.selectTagByMap(args);
////                                Map ele = new HashMap();
//                                        if(items.size() > 0){
//                                            for (Item item : items) {
//                                                String lastvalue = this.zabbixService.getItemLastvalueByItemId(item.getItemid().intValue());
//                                                flux.put("received", lastvalue);
//                                                break;
//                                            }
//                                        } else{
//                                            flux.put("received", "0");
//                                        }
//                                        args.clear();
//                                        args.put("ip", ne.getIp());
//                                        // 采集ifbasic,然后查询端口对应的历史流量
//                                        args.put("tag", "ifsent");
//                                        args.put("available", 1);
//                                        List<Item> ifsents = this.itemService.selectTagByMap(args);
//                                        if(ifsents.size() > 0){
//                                            for (Item item : ifsents) {
//                                                String lastvalue = this.zabbixService.getItemLastvalueByItemId(item.getItemid().intValue());
//                                                flux.put("sent", lastvalue);
//                                                break;
//                                            }
//                                        }else{
//                                            flux.put("sent", "0");
//                                        }
//                                        flux_terminal.put("flux", flux);
//                                    }
//                                    interfaceMap.put(inf, flux_terminal);
//                                }
//                                map.put(uuid, interfaceMap);
//                            }
//                        }
//                    }
//                    subarea.put(key, map);
//                }
//            }
//
//            NoticeWebsocketResp rep = new NoticeWebsocketResp();
//            rep.setNoticeType("9");
//            rep.setNoticeStatus(1);
//            rep.setNoticeInfo(subarea);
//            return rep;
//
//
//        }
//        NoticeWebsocketResp rep = new NoticeWebsocketResp();
//        rep.setNoticeType("9");
//        rep.setNoticeStatus(0);
//        return rep;
//    }

    @ApiOperation("9：网元|端口列表")
    @GetMapping("/ne/interface/all")
    public NoticeWebsocketResp neInterfaces(@RequestParam(value = "requestParams", required = false) String requestParams){
        Map params = JSONObject.parseObject(requestParams, Map.class);
        if(!params.isEmpty()){
            Map subarea = new HashMap();
            for(Object key : params.keySet()){
                String value = params.get(key).toString();
                JSONArray ary = JSONArray.parseArray(value);
                if(ary.size() > 0){
                    Map map = new HashMap();
                    for (Object param : ary) {
                        JSONObject ele = JSONObject.parseObject(param.toString());
                        String uuid = ele.getString("uuid");
                        String time = ele.getString("time");
                        NetworkElement ne = this.networkElementService.selectObjByUuid(uuid);
                        if(ne != null){
                            // 端口列表
                            String interfaces = ele.getString("interface");
                            JSONArray array = JSONArray.parseArray(interfaces);
                            if(array.size() > 0){
                                Map interfaceMap = new HashMap();
                                for(Object inf : array){
                                    Map flux_terminal = new HashMap();
                                    Map args = new HashMap();
                                    args.put("uuid", uuid);
                                    args.put("interfaceName", inf);
                                    args.put("tag", "DT");
                                    args.put("uuid", uuid);
                                    args.put("orderBy", "ip");
                                    args.put("orderType", "ASC");
                                    List<Mac> macs = null;
                                    if(time != null && !"".equals(time)){
                                        Date date = DateTools.parseDate(time, "yyyy-MM-dd HH:mm");
                                        args.put("time", date);
                                        macs = this.macHistoryService.selectObjByMap(args);
                                    }else{
                                        macs = this.macService.selectByMap(args);
                                    }
                                    if(macs != null && macs.size() > 0){
                                        macUtil.macJoint(macs);
                                        flux_terminal.put("terminal", macs);
                                    }else{
                                        List a = new ArrayList<>();
                                        flux_terminal.put("terminal", a);
                                    }
                                    if(time == null || "".equals(time)){
                                        // 流量
                                        Map flux = new HashMap();
                                        args.clear();
                                        args.put("ip", ne.getIp());
                                        // 采集ifbasic,然后查询端口对应的历史流量
                                        args.put("tag", "ifreceived");
                                        args.put("available", 1);
                                        List<Item> items = this.itemService.selectTagByMap(args);
//                                Map ele = new HashMap();
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
                                        flux_terminal.put("flux", flux);
                                    }
                                    interfaceMap.put(inf, flux_terminal);
                                }
                                map.put(uuid, interfaceMap);
                            }
                        }
                    }
                    subarea.put(key, map);
                }
            }

            NoticeWebsocketResp rep = new NoticeWebsocketResp();
            rep.setNoticeType("9");
            rep.setNoticeStatus(1);
            rep.setNoticeInfo(subarea);
            return rep;
        }
        NoticeWebsocketResp rep = new NoticeWebsocketResp();
        rep.setNoticeType("9");
        rep.setNoticeStatus(0);
        return rep;
    }
}
