package com.metoo.nspm.core.manager.admin.tools;

import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.util.StringUtil;
import com.metoo.nspm.core.service.nspm.*;
import com.metoo.nspm.core.utils.ResponseUtil;
import com.metoo.nspm.core.utils.network.IpUtil;
import com.metoo.nspm.entity.nspm.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class RoutTool {


    @Autowired
    private IRoutService routService;
    @Autowired
    private IRoutHistoryService routHistoryService;
    @Autowired
    private IRoutTableService routTableService;
    @Autowired
    private IIPAddressService ipAddressServie;
    @Autowired
    private ZabbixSubnetService subnetService;
    @Autowired
    private IMacService macService;
    @Autowired
    private IMacHistoryService macHistoryService;
    @Autowired
    private ZabbixSubnetService zabbixSubnetService;
    @Autowired
    private IIPAddressHistoryService ipAddressHistoryServie;
    @Autowired
    private SubnetTool subnetTool;


    /**
     *
     * @param ipAddress 起点设备
     * @param destIp 终点ip
     * @return
     */
//    public List<IpAddress> generatorRout(IpAddress ipAddress, String destIp, String descMask, Date time) {
//        List<IpAddress> ipAddresses = new ArrayList<>();
//        if (ipAddress != null) {
//            ipAddress.setStatus(0);
//            Map params = new HashMap();
//            // 查询当前设备在路由表中是否已记录
//            params.clear();
//            params.put("ip", ipAddress.getIp());
//            params.put("maskBit", ipAddress.getMask());
//            params.put("deviceName", ipAddress.getDeviceName());
//            params.put("interfaceName", ipAddress.getInterfaceName());
//            params.put("mac", ipAddress.getMac());
//            List<RouteTable> ipaddressRouts = this.routTableService.selectObjByMap(params);
//            RouteTable ipaddressRoutTable = null;
//            if(ipaddressRouts.size() > 0) {
//                ipaddressRoutTable = ipaddressRouts.get(0);
//            }
//            Route rout = this.queryRout(destIp, descMask, ipAddress.getDeviceName(), time);// 查询起点路由
////            Map params = IpUtil.getNetworkIp(destIp, descMask);
////            params.put("deviceName", ipAddress.getDeviceName());
////            Route rout = this.routService.selectDestDevice(params);
//            if(rout != null){
//                params.clear();
//                params.put("deviceName", ipAddress.getDeviceName());
//                params.put("destination", rout.getDestination());
//                params.put("maskBit", rout.getMask());
//                List<Route> nexthops = this.routService.selectNextHopDevice(params);
////              List<Route> nexthops = this.routService.queryDestDevice(params);
//                if (nexthops.size() > 0) {
//                    outCycle:for (Route nextHop : nexthops) {
//                        if(nextHop.getNextHop() != null && !nextHop.getNextHop().equals("")){
//                            String nexeIp = nextHop.getNextHop();
//                            // 这里使用continue，继续进行下一个nexthop
//                            if(nexeIp == null || nexeIp.equals("") || nexeIp.equals("127.0.0.1") || nexeIp.equals("0.0.0.0")){
//                                // 不在执行下一跳，为终端设备
//                                ipaddressRoutTable.setStatus(3);
//                                this.routTableService.update(ipaddressRoutTable);
//                                continue;
//                            }
//
//                            Map srcmap = IpUtil.getNetworkIpDec(nextHop.getNextHop(), "255.255.255.255");
//                            IpAddress nextIpaddress = this.ipAddressServie.querySrcDevice(srcmap);
//                            if(nextIpaddress != null){
//                                nextHop.setIpAddress(nextIpaddress);
//
//                                // 保存下一跳路由
//                                if(ipaddressRoutTable != null){
//                                    if(ipaddressRoutTable.getRemoteDevices() != null){
//                                        // 校验下一跳的对端设备是否已存在（避免死循环）
//                                        List<Map> remoteDevices = JSONArray.parseArray(ipaddressRoutTable.getRemoteDevices(), Map.class);// 对端设备信息集合
//                                        for (Map map : remoteDevices){
//                                            String remotDevice = map.get("remoteDevice").toString();
//                                            String remoteInterface = map.get("remoteInterface").toString();
//                                            String remoteUuid = map.get("remoteUuid").toString();
//                                            if(nextIpaddress.getDeviceName().equals(remotDevice)
//                                                    && nextIpaddress.getInterfaceName().equals(remoteInterface)
//                                                    && nextIpaddress.getDeviceUuid().equals(remoteUuid)){
//                                                ipAddress.setStatus(2);
//                                                ipaddressRoutTable.setStatus(2);
//                                                this.routTableService.update(ipaddressRoutTable);
//                                                continue outCycle;
//                                            }
//                                        }
//                                    }
//                                }
////                                RouteTable ipaddressRoutTable = null;
////                                if(ipaddressRouts.size() > 0){
////                                    ipaddressRoutTable = ipaddressRouts.get(0);
////                                }else{
////                                    ipaddressRoutTable = new RouteTable();
////                                }
//                                List<Map> list = null;
//                                if(ipaddressRoutTable.getRemoteDevices() != null){
//                                    list = JSONArray.parseArray(ipaddressRoutTable.getRemoteDevices(), Map.class);
//                                }else{
//                                    list = new ArrayList<>();
//                                }
//                                // 下一跳对端设备信息
//                                Map remote = new HashMap();
//                                remote.put("remoteDevice", ipAddress.getDeviceName());
//                                remote.put("remoteInterface", ipAddress.getInterfaceName());
//                                remote.put("remoteUuid", ipAddress.getDeviceUuid());
//                                list.add(remote);
//                                // 保存所有连接路径
////                            List ipaddressList = JSONArray.parseArray(ipaddressRoutTable.getRemoteDevices(), Map.class);
////                            if(ipaddressList != null){
////                                list.addAll(ipaddressList);
////                            }
//                                // 查询下一跳是否已存在
//                                params.clear();
//                                params.put("ip", nextIpaddress.getIp());
//                                params.put("maskBit", nextIpaddress.getMask());
//                                params.put("deviceName", nextIpaddress.getDeviceName());
//                                params.put("interfaceName", nextIpaddress.getInterfaceName());
//                                params.put("mac", nextIpaddress.getMac());
//                                List<RouteTable> nextIpaddressRoutTables = this.routTableService.selectObjByMap(params);
//                                RouteTable nextIpaddressRoutTable = null;
//                                if(nextIpaddressRoutTables.size() > 0){
//                                    nextIpaddressRoutTable = nextIpaddressRoutTables.get(0);
//                                }else{
//                                    nextIpaddressRoutTable = new RouteTable();
//                                }
//                                nextIpaddressRoutTable.setRemoteDevices(JSONArray.toJSONString(list));
//                                String[] IGNORE_ISOLATOR_PROPERTIES = new String[]{"id"};
//                                BeanUtils.copyProperties(nextIpaddress,nextIpaddressRoutTable,IGNORE_ISOLATOR_PROPERTIES);
//
//                                nextIpaddressRoutTable.setRemoteDevice(ipAddress.getDeviceName());
//                                nextIpaddressRoutTable.setRemoteInterface(ipAddress.getInterfaceName());
//                                nextIpaddressRoutTable.setRemoteUuid(ipAddress.getDeviceUuid());
//                                this.routTableService.save(nextIpaddressRoutTable);
//                                generatorRout(nextIpaddress, destIp, descMask, time);
//                            }
//                        }
//                    }
//                    ipAddress.setRouts(nexthops);
//                }else{
//                    ipAddress.setStatus(1);
//                    ipaddressRoutTable.setStatus(1);
//                    this.routTableService.update(ipaddressRoutTable);
//                }
//                ipAddresses.add(ipAddress);
//                return ipAddresses;
//            }
//        }
//        return null;
//    }

    // 多起点
    public void generatorRout(IpAddress ipAddress, String destIp, Date time, Long userId) {
        if (ipAddress != null) {
            ipAddress.setStatus(0);
            Map params = new HashMap();
            // 查询当前设备在路由表中是否已记录
            params.clear();
            params.put("ip", ipAddress.getIp());
            params.put("maskBit", ipAddress.getMask());
            params.put("deviceName", ipAddress.getDeviceName());
            params.put("interfaceName", ipAddress.getInterfaceName());
            params.put("mac", ipAddress.getMac());
            params.put("userId", userId);
            List<RouteTable> ipaddressRouts = this.routTableService.selectObjByMap(params);
            RouteTable ipaddressRoutTable = null;
            if(ipaddressRouts.size() > 0) {
                ipaddressRoutTable = ipaddressRouts.get(0);
            }

            List<Route> routs = this.queryRout2(ipAddress.getDeviceUuid(), destIp);// 查询路由
//            Map params = IpUtil.getNetworkIp(destIp, descMask);
//            params.put("deviceName", ipAddress.getDeviceName());
//            Route rout = this.routService.selectDestDevice(params);
            if(routs.size() > 0){
                nexthop:for (Route rout : routs) {
                    if(rout != null){
//                        params.clear();
//                        params.put("deviceUuid", ipAddress.getDeviceUuid());
//                        params.put("destination", rout.getDestination());
//                        params.put("maskBit", rout.getMask());
//                        List<Route> nexthops = this.routService.selectNextHopDevice(params);

//                      List<Route> nexthops = this.routService.queryDestDevice(params);

                        List<Route> nexthops = this.queryRout2(ipAddress.getDeviceUuid(), rout.getDestination());

                        if (nexthops.size() > 0) {
                            outCycle:for (Route nextHop : nexthops) {
                                if(nextHop.getNextHop() != null && !nextHop.getNextHop().equals("") && !nextHop.getNextHop().equals("0")){
                                    String nexeIp = nextHop.getNextHop();
                                    // 这里使用continue，继续进行下一个nexthop
                                    if(nexeIp == null || nexeIp.equals("")
                                            || nexeIp.equals("127.0.0.1")
                                            || nexeIp.equals("0.0.0.0")
                                            ){

                                        // 不在执行下一跳，为终端设备
                                        ipaddressRoutTable.setStatus(3);
                                        this.routTableService.update(ipaddressRoutTable);
                                        continue;
                                    }
                                    Map map = IpUtil.getNetworkIpDec(nextHop.getNextHop(), "255.255.255.255");
                                    List<IpAddress> nextIpaddresses = this.ipAddressServie.querySrcDevice(map);// 下一跳Ipaddress
                                    if(nextIpaddresses.size() > 0){
                                        for (IpAddress nextIpaddress : nextIpaddresses) {
                                            if(nextIpaddress != null){
                                                if(nextIpaddress.getDeviceUuid().equals(ipAddress.getDeviceUuid())){
                                                    continue nexthop;
                                                }
                                                nextHop.setIpAddress(nextIpaddress);
                                                // 保存下一跳路由
                                                if(ipaddressRoutTable != null){
                                                    if(ipaddressRoutTable.getRemoteDevices() != null){
                                                        // 校验下一跳的对端设备是否已存在（避免死循环）
                                                        List<Map> remoteDevices = JSONArray.parseArray(ipaddressRoutTable.getRemoteDevices(), Map.class);// 对端设备信息集合
                                                        for (Map remoteDevice : remoteDevices){
                                                            String remotDevice = remoteDevice.get("remoteDevice").toString();
                                                            String remoteInterface = remoteDevice.get("remoteInterface").toString();
                                                            String remoteUuid = remoteDevice.get("remoteUuid").toString();
                                                            if(nextIpaddress.getDeviceName().equals(remotDevice)
                                                                    && nextIpaddress.getInterfaceName().equals(remoteInterface)
                                                                    && nextIpaddress.getDeviceUuid().equals(remoteUuid)){
                                                                ipAddress.setStatus(2);
                                                                ipaddressRoutTable.setStatus(2);
                                                                this.routTableService.update(ipaddressRoutTable);
                                                                continue outCycle;
                                                            }
                                                        }
                                                    }
                                                }
//                                RouteTable ipaddressRoutTable = null;
//                                if(ipaddressRouts.size() > 0){
//                                    ipaddressRoutTable = ipaddressRouts.get(0);
//                                }else{
//                                    ipaddressRoutTable = new RouteTable();
//                                }
                                                List<Map> list = null;
                                                if(ipaddressRoutTable.getRemoteDevices() != null){
                                                    list = JSONArray.parseArray(ipaddressRoutTable.getRemoteDevices(), Map.class);
                                                }else{
                                                    list = new ArrayList<>();
                                                }
                                                // 下一跳对端设备信息
                                                Map remote = new HashMap();
                                                remote.put("remoteDevice", ipAddress.getDeviceName());
                                                remote.put("remoteInterface", ipAddress.getInterfaceName());
                                                remote.put("remoteUuid", ipAddress.getDeviceUuid());
                                                list.add(remote);
                                                // 保存所有连接路径
//                            List ipaddressList = JSONArray.parseArray(ipaddressRoutTable.getRemoteDevices(), Map.class);
//                            if(ipaddressList != null){
//                                list.addAll(ipaddressList);
//                            }
                                                // 查询下一跳是否已存在
                                                params.clear();
                                                params.put("ip", nextIpaddress.getIp());
                                                params.put("maskBit", nextIpaddress.getMask());
                                                params.put("deviceName", nextIpaddress.getDeviceName());
                                                params.put("interfaceName", nextIpaddress.getInterfaceName());
                                                params.put("mac", nextIpaddress.getMac());
                                                params.put("userId", userId);
                                                List<RouteTable> nextIpaddressRoutTables = this.routTableService.selectObjByMap(params);
                                                RouteTable nextIpaddressRoutTable = null;
                                                if(nextIpaddressRoutTables.size() > 0){
                                                    nextIpaddressRoutTable = nextIpaddressRoutTables.get(0);
                                                }else{
                                                    nextIpaddressRoutTable = new RouteTable();
                                                }
                                                nextIpaddressRoutTable.setRemoteDevices(JSONArray.toJSONString(list));
                                                String[] IGNORE_ISOLATOR_PROPERTIES = new String[]{"id"};
                                                BeanUtils.copyProperties(nextIpaddress,nextIpaddressRoutTable,IGNORE_ISOLATOR_PROPERTIES);

                                                nextIpaddressRoutTable.setRemoteDevice(ipAddress.getDeviceName());
                                                nextIpaddressRoutTable.setRemoteInterface(ipAddress.getInterfaceName());
                                                nextIpaddressRoutTable.setRemoteUuid(ipAddress.getDeviceUuid());
                                                nextIpaddressRoutTable.setUserId(ShiroUserHolder.currentUser().getId());
                                                nextIpaddressRoutTable.setUserId(ShiroUserHolder.currentUser().getId());
                                                this.routTableService.save(nextIpaddressRoutTable);
                                                generatorRout(nextIpaddress, destIp, time, userId);
                                            }
                                        }
                                    }
                                }
                            }
                            ipAddress.setRouts(nexthops);
                        }else{
                            ipAddress.setStatus(1);
                            ipaddressRoutTable.setStatus(1);
                            this.routTableService.update(ipaddressRoutTable);
                        }
                        continue;
                    }
                }
            }
        }
    }

    // 二层路径查询
//    public List twoLayerPath(String srcMac, String destMac){
//        // 查询起点设备
//        Map params = new HashMap();
//        params.clear();
//        params.put("tag", "DT");
//        params.put("mac", srcMac);
//        List<Mac> srcDevices = this.macService.selectByMap(params);
//        if(srcDevices.size() <= 0){
//            params.clear();
//            params.put("tag", "L");
//            params.put("mac", srcMac);
//            srcDevices = this.macService.selectByMap(params);
//        }
//        if(srcDevices.size() > 0){
//            params.clear();
//            params.put("tag", "DT");
//            params.put("mac", destMac);
//            List<Mac> destDevices = this.macService.selectByMap(params);
//            if(destDevices.size() <= 0){
//                params.clear();
//                params.put("tag", "L");
//                params.put("mac", destMac);
//                destDevices = this.macService.selectByMap(params);
//            }
//            if(destDevices.size() > 0){
//                // 查询下一跳
//                Mac srcDevice = srcDevices.get(0);
//                Mac destDevice = destDevices.get(0);
//                List list = this.recursionLayPath(srcDevice.getUuid(), destDevice);
//                list.add(destDevice);
//                return list;
//            }
//        }
//        return new ArrayList();
//    }

    public Map secondLayer(String srcMac, String destMac){
        // 查询起点设备
        Map map = new HashMap();
        Map params = new HashMap();
        List<Mac> srcDevices = new ArrayList<>();
        if(srcMac.contains("0:0:5e")){
            params.put("tag", "LV");
            params.put("mac", srcMac);
            srcDevices = this.macService.selectByMap(params);
        }
        if(srcDevices.size() <= 0){
            params.clear();
            params.put("tag", "DT");
            params.put("mac", srcMac);
            srcDevices = this.macService.selectByMap(params);
            if(srcDevices.size() <= 0){
                params.clear();
                params.put("tag", "L");
                params.put("mac", srcMac);
                srcDevices = this.macService.selectByMap(params);
            }
        }
        if(srcDevices.size() > 0){
            List<Mac> destDevices = new ArrayList<>();
            if(destMac.contains("0:0:5e")){
                params.put("tag", "LV");
                params.put("mac", destMac);
                destDevices = this.macService.selectByMap(params);
            }
            if(destDevices.size() <= 0){
                params.clear();
                params.put("tag", "DT");
                params.put("mac", destMac);
                destDevices = this.macService.selectByMap(params);
                if(destDevices.size() <= 0){
                    params.clear();
                    params.put("tag", "L");
                    params.put("mac", destMac);
                    destDevices = this.macService.selectByMap(params);
                }
            }
            if(destDevices.size() > 0){
                // 查询下一跳
                Mac srcDevice = srcDevices.get(0);
                Mac destDevice = destDevices.get(0);
                String tag = "";
                List path = new ArrayList();
                if(destDevice.getTag().equals("L")){
                    path = this.nextHop(srcDevice.getUuid(), destDevice, "L");
                }else{
                    path = this.recursionLayPath(srcDevice.getUuid(), destDevice, tag);
                }
                path.add(destDevice);
                path.add(srcDevice);
                map.put("destDevice", destDevice);
                map.put("path", path);
                return map;
            }
        }
        return new HashMap();
    }

    public List secondLayers(String srcUuid, String destMac){
        // 查询起点设备
        Map params = new HashMap();
        if(StringUtils.isNotEmpty(srcUuid)){
            List<Mac> destDevices = new ArrayList<>();
            if(destMac.contains("0:0:5e")){
                params.put("tag", "LV");
                params.put("mac", destMac);
                destDevices = this.macService.selectByMap(params);
            }
            if(destDevices.size() <= 0){
                params.clear();
                params.put("tag", "DT");
                params.put("mac", destMac);
                destDevices = this.macService.selectByMap(params);
                if(destDevices.size() <= 0){
                    params.clear();
                    params.put("tag", "L");
                    params.put("mac", destMac);
                    destDevices = this.macService.selectByMap(params);
                }
            }
            if(destDevices.size() > 0){
                // 查询下一跳
                Mac destDevice = destDevices.get(0);
                String tag = "";
                List path = new ArrayList();
                if(destDevice.getTag().equals("L")){
                    path = this.nextHop(srcUuid, destDevice, "L");
                }else{
                    path = this.recursionLayPath(srcUuid, destDevice, tag);
                }
//                List path = this.recursionLayPath(srcUuid, destDevice, tag);

                return path;
            }
        }
        return new ArrayList<>();
    }

    // 递归查询二层路径
    public List recursionLayPath(String srcUuid, Mac destDevice, String tag){
        List list = new ArrayList();
        Map params = new HashMap();
        params.clear();
        params.put("uuid", srcUuid);
        params.put("mac", destDevice.getMac());
        if(!tag.equals("")){
            params.put("tag", "DE");
        }
        List<Mac> macs = this.macService.selectByMap(params);
        if(macs.size() > 0){
            // 多个下一跳路径
            for(Mac mac : macs){
                // 下一跳与终点
                if(mac.getId() == destDevice.getId()){
                    continue;
                }else{
                    if(tag.equals("")){
                        params.clear();
                        params.put("interfaceName", mac.getInterfaceName());
                        params.put("uuid", mac.getUuid());
                        params.put("tag", "DE");
                        List<Mac> vMacs = this.macService.selectByMap(params);
                        if(vMacs.size() > 0){
                            Mac vmac = vMacs.stream().findAny().get();
                            List listr = this.recursionLayPath(vmac.getRemoteUuid(), destDevice, "");
                            list.addAll(listr);
                            list.add(vmac);
                        }
                    }else{
                        List listr = this.recursionLayPath(mac.getRemoteUuid(), destDevice, tag);
                        list.addAll(listr);
                        list.add(mac);
                    }
                }
            }
            return list;
        }
        return new ArrayList();
    }

    public List nextHop(String srcUuid, Mac destDevice, String tag){
//        List nextHop_1 = this.nextHop_1(srcUuid, destDevice, tag);
//        if(nextHop_1.size() > 0){
//            return nextHop_1;
//        }
        List nextHop_2 = this.nextHop_2(srcUuid, destDevice);
        if(nextHop_2.size() > 0){
            return nextHop_2;
        }
        return new ArrayList();
    }

    /**
     * 查询下一跳：调整 1
     * @param srcUuid
     * @param destDevice
     * @param tag
     * @return
     */
    public List nextHop_1(String srcUuid, Mac destDevice, String tag){
        List list = new ArrayList();
        Map params = new HashMap();
        if(tag.equals("L")){
            params.clear();
            params.put("uuid", srcUuid);
            params.put("tag", "DE");
            List<Mac> deMac = this.macService.selectByMap(params);
            if(deMac.size() > 0){
                List<Mac> remoteMacs = deMac.stream().filter(e -> e.getRemoteUuid() != null).collect(Collectors.toList());
                if(remoteMacs.size() > 0){
                    Mac nextHop = remoteMacs.stream().filter(e -> e.getRemoteUuid().equals(destDevice.getUuid())).findAny().get();
                    List<Mac> des = remoteMacs.stream().filter(e -> e.getRemoteUuid().equals(destDevice.getUuid())).collect(Collectors.toList());
                    if(nextHop != null){
                        if(nextHop.getRemoteUuid().equals(destDevice.getUuid())){// 下一跳与终点
                             }else{
                                List listr = this.nextHop(nextHop.getRemoteUuid(), destDevice, "L");
                                list.addAll(listr);
                                list.add(nextHop);
                            }
//                        for(Mac mac : des){
//                            if(mac.getRemoteUuid().equals(destDevice.getUuid())){// 下一跳与终点
//                                continue;
//                            }else{
//                                List listr = this.recursionLayPath(mac.getRemoteUuid(), destDevice, "L");
//                                list.addAll(listr);
//                                list.add(mac);
//                            }
//                        }
                    }
                }
            }
        }else{

        }
        return list;
    }

    /**
     * 查询下一跳：调整 2
     * @param srcUuid
     * @param destDevice
     * @return
     */
    public List nextHop_2(String srcUuid, Mac destDevice){
        List list = new ArrayList();
        Map params = new HashMap();
            params.clear();
            params.put("uuid", srcUuid);
            params.put("mac", destDevice.getMac());
            List<Mac> macs = this.macService.selectByMap(params);
            if(macs.size() > 0) {
                // 多个下一跳路径
                for (Mac mac : macs) {
                    // 下一跳与终点
                    if (mac.getId() == destDevice.getId()) {
                        continue;
                    } else {
                        params.clear();
                        params.put("interfaceName", mac.getInterfaceName());
                        params.put("uuid", mac.getUuid());
                        params.put("tag", "DE");
                        List<Mac> demac = this.macService.selectByMap(params);
                        if (demac.size() > 0) {
                            Mac findAny = demac.stream().findAny().get();
                            List listr = this.nextHop_2(findAny.getRemoteUuid(), destDevice);
                            list.addAll(listr);
                            list.add(findAny);
                        }
                    }
                }
            }
        return list;
    }

//    public void generatorRout(IpAddress ipAddress, String destIp, String descMask, Date time) {
//        if (ipAddress != null) {
//            ipAddress.setStatus(0);
//            Map params = new HashMap();
//            // 查询当前设备在路由表中是否已记录
//            params.clear();
//            params.put("ip", ipAddress.getIp());
//            params.put("maskBit", ipAddress.getMask());
//            params.put("deviceName", ipAddress.getDeviceName());
//            params.put("interfaceName", ipAddress.getInterfaceName());
//            params.put("mac", ipAddress.getMac());
//            List<RouteTable> ipaddressRouts = this.routTableService.selectObjByMap(params);
//            RouteTable ipaddressRoutTable = null;
//            if(ipaddressRouts.size() > 0) {
//                ipaddressRoutTable = ipaddressRouts.get(0);
//            }
//
//            List<Route> routs = this.queryRout2(ipAddress.getDeviceUuid(), destIp);// 查询路由
////            Map params = IpUtil.getNetworkIp(destIp, descMask);
////            params.put("deviceName", ipAddress.getDeviceName());
////            Route rout = this.routService.selectDestDevice(params);
//            if(routs.size() > 0){
//                for (Route rout : routs) {
//                    if(rout != null){
//                        params.clear();
//                        params.put("deviceUuid", ipAddress.getDeviceUuid());
//                        params.put("destination", rout.getDestination());
////                        params.put("maskBit", rout.getMask());
//                        List<Route> nexthops = this.routService.selectNextHopDevice(params);
////              List<Route> nexthops = this.routService.queryDestDevice(params);
//                        if (nexthops.size() > 0) {
//                            outCycle:for (Route nextHop : nexthops) {
//                                if(nextHop.getNextHop() != null && !nextHop.getNextHop().equals("")){
//                                    String nexeIp = nextHop.getNextHop();
//                                    // 这里使用continue，继续进行下一个nexthop
//                                    if(nexeIp == null || nexeIp.equals("") || nexeIp.equals("127.0.0.1") || nexeIp.equals("0.0.0.0")){
//                                        // 不在执行下一跳，为终端设备
//                                        ipaddressRoutTable.setStatus(3);
//                                        this.routTableService.update(ipaddressRoutTable);
//                                        continue;
//                                    }
//
//                                    Map srcmap = IpUtil.getNetworkIpDec(nextHop.getNextHop(), "255.255.255.255");
//                                    IpAddress nextIpaddress = this.ipAddressServie.querySrcDevice(srcmap);
//                                    if(nextIpaddress != null){
//                                        nextHop.setIpAddress(nextIpaddress);
//
//                                        // 保存下一跳路由
//                                        if(ipaddressRoutTable != null){
//                                            if(ipaddressRoutTable.getRemoteDevices() != null){
//                                                // 校验下一跳的对端设备是否已存在（避免死循环）
//                                                List<Map> remoteDevices = JSONArray.parseArray(ipaddressRoutTable.getRemoteDevices(), Map.class);// 对端设备信息集合
//                                                for (Map map : remoteDevices){
//                                                    String remotDevice = map.get("remoteDevice").toString();
//                                                    String remoteInterface = map.get("remoteInterface").toString();
//                                                    String remoteUuid = map.get("remoteUuid").toString();
//                                                    if(nextIpaddress.getDeviceName().equals(remotDevice)
//                                                            && nextIpaddress.getInterfaceName().equals(remoteInterface)
//                                                            && nextIpaddress.getDeviceUuid().equals(remoteUuid)){
//                                                        ipAddress.setStatus(2);
//                                                        ipaddressRoutTable.setStatus(2);
//                                                        this.routTableService.update(ipaddressRoutTable);
//                                                        continue outCycle;
//                                                    }
//                                                }
//                                            }
//                                        }
////                                RouteTable ipaddressRoutTable = null;
////                                if(ipaddressRouts.size() > 0){
////                                    ipaddressRoutTable = ipaddressRouts.get(0);
////                                }else{
////                                    ipaddressRoutTable = new RouteTable();
////                                }
//                                        List<Map> list = null;
//                                        if(ipaddressRoutTable.getRemoteDevices() != null){
//                                            list = JSONArray.parseArray(ipaddressRoutTable.getRemoteDevices(), Map.class);
//                                        }else{
//                                            list = new ArrayList<>();
//                                        }
//                                        // 下一跳对端设备信息
//                                        Map remote = new HashMap();
//                                        remote.put("remoteDevice", ipAddress.getDeviceName());
//                                        remote.put("remoteInterface", ipAddress.getInterfaceName());
//                                        remote.put("remoteUuid", ipAddress.getDeviceUuid());
//                                        list.add(remote);
//                                        // 保存所有连接路径
////                            List ipaddressList = JSONArray.parseArray(ipaddressRoutTable.getRemoteDevices(), Map.class);
////                            if(ipaddressList != null){
////                                list.addAll(ipaddressList);
////                            }
//                                        // 查询下一跳是否已存在
//                                        params.clear();
//                                        params.put("ip", nextIpaddress.getIp());
//                                        params.put("maskBit", nextIpaddress.getMask());
//                                        params.put("deviceName", nextIpaddress.getDeviceName());
//                                        params.put("interfaceName", nextIpaddress.getInterfaceName());
//                                         params.put("mac", nextIpaddress.getMac());
//                                        List<RouteTable> nextIpaddressRoutTables = this.routTableService.selectObjByMap(params);
//                                        RouteTable nextIpaddressRoutTable = null;
//                                        if(nextIpaddressRoutTables.size() > 0){
//                                            nextIpaddressRoutTable = nextIpaddressRoutTables.get(0);
//                                        }else{
//                                            nextIpaddressRoutTable = new RouteTable();
//                                        }
//                                        nextIpaddressRoutTable.setRemoteDevices(JSONArray.toJSONString(list));
//                                        String[] IGNORE_ISOLATOR_PROPERTIES = new String[]{"id"};
//                                        BeanUtils.copyProperties(nextIpaddress,nextIpaddressRoutTable,IGNORE_ISOLATOR_PROPERTIES);
//
//                                        nextIpaddressRoutTable.setRemoteDevice(ipAddress.getDeviceName());
//                                        nextIpaddressRoutTable.setRemoteInterface(ipAddress.getInterfaceName());
//                                        nextIpaddressRoutTable.setRemoteUuid(ipAddress.getDeviceUuid());
//                                        this.routTableService.save(nextIpaddressRoutTable);
//                                        generatorRout(nextIpaddress, destIp, descMask, time);
//                                    }
//                                }
//                            }
//                            ipAddress.setRouts(nexthops);
//                        }else{
//                            ipAddress.setStatus(1);
//                            ipaddressRoutTable.setStatus(1);
//                            this.routTableService.update(ipaddressRoutTable);
//                        }
//                      continue;
//                    }
//                }
//            }
//        }
//    }


    // 查询七点设备路由1
    public Route queryRout(String descIp, String descMask, String deviceName, Date time){
//        String dm = IpUtil.bitMaskConvertMask(Integer.parseInt(descMask));
//        Map network = IpUtil.getNetworkIp(descIp, dm);
        Map params = new HashMap();
        params.put("deviceName", deviceName);
        params.put("descMask", descMask);
        params.put("orderBy", "mask_bit");
        params.put("orderType", "desc");
        List<Route> routs = null;
        if(time == null){
            routs = this.routService.selectObjByMap(params);
        }else{
            params.put("time", time);
            routs = this.routHistoryService.selectObjByMap(params);
        }
        List<Route> sameRouts = new ArrayList<>();
        if(routs != null){
            for(Route rout : routs){
                if(!StringUtil.isEmpty(rout.getDestination())
                        && !StringUtil.isEmpty(rout.getMask())){
                    boolean flag = isInRange(descIp,
                            IpUtil.decConvertIp(Long.parseLong(rout.getDestination()))
                                    + "/"
                                    +  rout.getMaskBit());
                    if(flag){
                        sameRouts.add(rout);
                    }
                }
            }
        }
        Route rout = null;
        if(sameRouts.size() > 0){
            rout = sameRouts.get(0);
        }
        if(rout == null){
            // dest不存在，查询0.0.0.0
            params.clear();
            params.put("deviceName", deviceName);
            params.put("destination", 0);
            params.put("maskBit", 0);
            if(time == null){
                rout = this.routService.selectDestDevice(params);
            }else{
                params.put("time", time);
                rout = this.routHistoryService.selectDestDevice(params);
            }
        }
        return rout;
    }

    // 查询七点设备路由2

    /**
     *
     * @param device_uuid 起点设备Uuid
     * @param destIp 终点设备Ip
     * @return
     */
    public List<Route> queryRout2(String device_uuid, String destIp){
        List<Route> routList2 = new ArrayList<>();
        Map params = new HashMap();
        params.put("destination", destIp);
        params.put("deviceUuid", device_uuid);
        List<Route> routes = this.routService.selectObjByMap(params);
        if(routes.size() <= 0) {
            params.clear();
            params.put("deviceUuid", device_uuid);
            routes = this.routService.selectObjByMap(params);
            if (routes.size() > 0) {
                List<Route> routList = new ArrayList<>();
                for (Route rout : routes) {
                    boolean flag = IpUtil.ipIsInNet(destIp, rout.getCidr());
                    if (flag) {
                        routList.add(rout);
                    }
                }
                if (routList.size() > 0) {
                    int maskBitMax = routList.get(0).getMaskBit();
                    for (Route rout : routList) {
                        if (rout.getMaskBit() == maskBitMax) {
                            routList2.add(rout);
                        }else if(rout.getMaskBit() > maskBitMax){
                            routList2.clear();
                            routList2.add(rout);
                        }
                    }
                    return routList2;
                } else {
                    params.clear();
                    params.put("destination", 0);
                    params.put("deviceUuid", device_uuid);
                    routes = this.routService.selectObjByMap(params);
                }
            }
        }
        return routes;
    }

//    public Route queryRout(String descIp, String descMask, String deviceName, Date time){
////        String dm = IpUtil.bitMaskConvertMask(Integer.parseInt(descMask));
////        Map network = IpUtil.getNetworkIp(descIp, dm);
//        Map params = new HashMap();
//        params.put("deviceName", deviceName);
//        params.put("descMask", descMask);
//        params.put("orderBy", "mask_bit");
//        params.put("orderType", "desc");
//        List<Route> routs = null;
//        if(time == null){
//            routs = this.routService.selectObjByMap(params);
//        }else{
//            params.put("time", time);
//            routs = this.routHistoryService.selectObjByMap(params);
//        }
//        List<Route> sameRouts = new ArrayList<>();
//        if(routs != null){
//            for(Route rout : routs){
//                if(!StringUtil.isEmpty(rout.getDestination())
//                        && !StringUtil.isEmpty(rout.getMask())){
//                    boolean flag = isInRange(descIp,
//                            IpUtil.decConvertIp(Long.parseLong(rout.getDestination()))
//                                    + "/"
//                                    +  rout.getMaskBit());
//                    if(flag){
//                        sameRouts.add(rout);
//                    }
//                }
//            }
//        }
//        Route rout = null;
//        if(sameRouts.size() > 0){
//            rout = sameRouts.get(0);
//        }
//        if(rout == null){
//            // dest不存在，查询0.0.0.0
//            params.clear();
//            params.put("deviceName", deviceName);
//            params.put("destination", 0);
//            params.put("maskBit", 0);
//            if(time == null){
//                rout = this.routService.selectDestDevice(params);
//            }else{
//                params.put("time", time);
//                rout = this.routHistoryService.selectDestDevice(params);
//            }
//        }
//        return rout;
//    }
//

    /**
     * @描述 判断某个ip是否在一个网段内
     * @param ip
     * @param cidr
     * @return
     */
    public static boolean isInRange(String ip, String cidr) {
        String[] ips = ip.split("\\.");
        int ipAddr = (Integer.parseInt(ips[0]) << 24)
                | (Integer.parseInt(ips[1]) << 16)
                | (Integer.parseInt(ips[2]) << 8) | Integer.parseInt(ips[3]);
        int type = Integer.parseInt(cidr.replaceAll(".*/", ""));
        int mask = 0xFFFFFFFF << (32 - type);
        String cidrIp = cidr.replaceAll("/.*", "");
        String[] cidrIps = cidrIp.split("\\.");
        int cidrIpAddr = (Integer.parseInt(cidrIps[0]) << 24)
                | (Integer.parseInt(cidrIps[1]) << 16)
                | (Integer.parseInt(cidrIps[2]) << 8)
                | Integer.parseInt(cidrIps[3]);

        return (ipAddr & mask) == (cidrIpAddr & mask);
    }

    /**
     * 二层设备
     * @param mac
     * @param deviceName
     * @return
     */
    public List<Mac> generetorSrcLayer_2_device(String mac, String deviceName, Date time){
        List list = new ArrayList<>();
        Map params = new HashMap();
        params.clear();
        params.put("mac", mac);
        params.put("deviceName", deviceName);
        List<Mac> srcMacs = this.queryMac(params, time);// 路由起点ip设备mac（包含起点IpMac）
        if(srcMacs.size() > 0){
            Mac srcMac = srcMacs.get(0);
            if(srcMac.getRemoteDevice() == null){
                params.clear();
                params.put("deviceName", deviceName);
                params.put("interfaceName", srcMac.getInterfaceName());
                params.put("tag", "DE");
                List<Mac> macs = this.queryMac(params, time);// 起点设备mac
                if(macs.size() > 0){
                    Mac mac1 = macs.get(0);
                    if(mac1.getRemoteDevice() != null){
                        srcMac.setRemoteDevice(mac1.getRemoteDevice());
                        srcMac.setInterfaceName(mac1.getInterfaceName());
                        srcMac.setRemoteUuid(mac1.getRemoteUuid());
                        list.addAll(generetorSrcLayer_2_device(mac, mac1.getRemoteDevice(),time));
                    }
                }
                list.add(srcMac);
                return list;
            }
        }
        return list;
    }

    public List<Mac> queryMac(Map params, Date time){
        List<Mac> srcMacs = null;
        if(time == null){
            srcMacs = this.macService.selectByMap(params);// 路由起点设备mac（包含起点IpMac）
        }else{
            params.put("time", time);
            srcMacs = this.macHistoryService.selectObjByMap(params);
        }
        return srcMacs;
    }

    public List queryRoutePath(String srcIp, String destIp, Date time, Mac destDevice){
        User user = ShiroUserHolder.currentUser();
        List<IpAddress> srcIpAddresses = this.queryRoutDevice(srcIp, time);
        if(srcIpAddresses.size() >= 0){
        // 终点设备
    //          List<IpAddress> destIpAddress = this.queryRoutDevice(destIp, time);
    //          if(destIpAddress.size() <= 0){
    //                return ResponseUtil.badArgument("终点Ip不存在");
    //          }
    //          map.put("destinationDevice", destIpAddress);
        Map params = new HashMap();
        this.routTableService.deleteObjByUserId(user.getId());// 清除 routTable
        // 保存起点设备到路由表
        // 多起点
        for (IpAddress srcIpAddress : srcIpAddresses) {
            boolean flag = true;
            if(destDevice != null
                    && !srcIpAddress.getDeviceUuid().equals(destDevice.getUuid())) {
                flag = false;
            }
            if(flag){
                params.clear();
                params.put("ip", srcIpAddress.getIp());
                params.put("mask", srcIpAddress.getMask());
                params.put("deviceName", srcIpAddress.getDeviceName());
                params.put("interfaceName", srcIpAddress.getInterfaceName());
                params.put("mac", srcIpAddress.getMac());
                params.put("userId", user.getId());
                List<RouteTable> routTables = this.routTableService.selectObjByMap(params);
                RouteTable routTable = null;
                if(routTables.size() > 0){
                    routTable = routTables.get(0);
                }else{
                    routTable = new RouteTable();
                }
                String[] IGNORE_ISOLATOR_PROPERTIES = new String[]{"id"};
                BeanUtils.copyProperties(srcIpAddress, routTable, IGNORE_ISOLATOR_PROPERTIES);
                routTable.setUserId(user.getId());
                this.routTableService.save(routTable);
                // 路由查询
                this.generatorRout(srcIpAddress, destIp, time, user.getId());
                }
            }
        }
        Map params = new HashMap();
        params.clear();
        params.put("userId", user.getId());
        List<RouteTable> routTableList = this.routTableService.selectObjByMap(params);
        return routTableList;
    }

    public List<IpAddress> queryRoutDevice(String Ip, Date time){
        List<IpAddress> list = new ArrayList<IpAddress>();
        Map params = new HashMap();
        params.put("ip", IpUtil.ipConvertDec(Ip));
        List<IpAddress> ipAddresses = this.ipAddressServie.selectObjByMap(params);
        if(ipAddresses.size() > 0){
            list.add(ipAddresses.get(0));
        }else{
            // 获取起点ip网络地址和广播地址
            Map originMap =  null;
            List<Subnet> subnets = this.zabbixSubnetService.selectSubnetByParentId(null);
            if(subnets.size() > 0){
                if(Ip != null){
                    if(Ip.equals("0.0.0.0")){
                    }
                    if(!IpUtil.verifyIp(Ip)){
                    }
                    // 判断ip地址是否属于子网
                    for(Subnet subnet : subnets){
                        Subnet sub = this.subnetTool.verifySubnetByIp(subnet, Ip);
                        if(sub != null){
                            String mask = IpUtil.bitMaskConvertMask(sub.getMask());
                            originMap = IpUtil.getNetworkIpDec(sub.getIp(), mask);
                            break;
                        }
                    }
                }
            }
            if(originMap == null || originMap.isEmpty()){
                return new ArrayList<>();
            }
            if(time == null){
                list = this.ipAddressServie.querySrcDevice(originMap);
            }else{
                originMap.put("time", time);
                list = this.ipAddressHistoryServie.querySrcDevice(originMap);
            }
        }
        return list;
    }

}
