package com.metoo.nspm.core.manager.admin.action;

import com.github.pagehelper.util.StringUtil;
import com.metoo.nspm.core.service.IAddressService;
import com.metoo.nspm.core.service.phpipam.IpamSubnetService;
import com.metoo.nspm.core.service.zabbix.IIPAddressServie;
import com.metoo.nspm.core.service.zabbix.IpDetailService;
import com.metoo.nspm.core.service.zabbix.ZabbixSubnetService;
import com.metoo.nspm.core.service.zabbix.impl.IpAddressServiceImpl;
import com.metoo.nspm.core.utils.ResponseUtil;
import com.metoo.nspm.core.utils.StringUtils;
import com.metoo.nspm.core.utils.network.IpUtil;
import com.metoo.nspm.entity.Address;
import com.metoo.nspm.entity.Group;
import com.metoo.nspm.entity.zabbix.IpAddress;
import com.metoo.nspm.entity.zabbix.IpDetail;
import com.metoo.nspm.entity.zabbix.Subnet;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Api("子网管理")
@RequestMapping("/admin/subnet")
@RestController
public class SubnetManagerController {

    @Autowired
    private ZabbixSubnetService zabbixSubnetService;
    @Autowired
    private IAddressService addressService;
    @Autowired
    private IpDetailService ipDetailService;

    @RequestMapping("/list")
    public Object list(){
        // 获取所有子网一级
        List<Subnet> parentList = this.zabbixSubnetService.selectSubnetByParentId(null);
        if(parentList.size() > 0){
            for (Subnet subnet : parentList) {
                this.genericSubnet(subnet);
            }
            return ResponseUtil.ok(parentList);
        }
        return ResponseUtil.ok();
    }

    public List<Subnet> genericSubnet(Subnet subnet){
        List<Subnet> subnets = this.zabbixSubnetService.selectSubnetByParentId(subnet.getId());
        if(subnets.size() > 0){
            for(Subnet child : subnets){
                List<Subnet> subnetList = genericSubnet(child);
                if(subnetList.size() > 0){
                    child.setSubnetList(subnetList);
                }
            }
            subnet.setSubnetList(subnets);
        }
        return subnets;
    }


    // 编辑网段
    @PostMapping
    public Object save(@RequestBody Subnet subnet){
        // 校验Ip地址格式
        if(StringUtil.isEmpty(subnet.getIp())){
            return ResponseUtil.badArgument("以CIDR格式输入子网");
        }else{
            if(!IpUtil.verifyIp(subnet.getIp())){
                return ResponseUtil.badArgument("错误的CIDR格式");
            }
        }
        if(subnet.getMask() == null ){
            return ResponseUtil.badArgument("以CIDR格式输入子网");
        }else if(subnet.getMask() > 32 || subnet.getMask() < 0){
            return ResponseUtil.badArgument("错误的CIDR格式");
        }
        // 校验Ip地址不能为子网，并返回子网信息（IP address cannot be subnet! (Consider using 59.51.2.0)）
        String mask = IpUtil.bitMaskConvertMask(subnet.getMask());
        Map SubnetMap = IpUtil.getNetworkIp(subnet.getIp(), mask);
        String network = SubnetMap.get("network").toString();
        if(!subnet.getIp().equals(network)){
            return ResponseUtil.badArgument("Ip地址不能为子网!(考虑使用：" + network + ")");
        }
        // 如果主子网不为空。判断子网是否属于主子网
        // Subnet is not within boundaries of its master subnet

        if(subnet.getParentId() != null){
            Subnet parentSbunet = this.zabbixSubnetService.selectObjById(subnet.getParentId());
            // 此处IP为网段
            boolean flag = IpUtil.ipIsInNet(subnet.getIp(), parentSbunet.getIp()+"/"+parentSbunet.getMask());
            if(!flag){
                return ResponseUtil.badArgument("子网不在其主子网的边界内");
            }
        }

        return null;
    }

    /**
     * 根据子网Id查询直接从属子网
     * @param id
     * @return
     */
    @GetMapping(value = {"","/{id}"})
    public Object getSubnet(@PathVariable(value = "id", required = false) Long id){
        if(id == null){
            // 获取所有子网一级
            List<Subnet> parentList = this.zabbixSubnetService.selectSubnetByParentId(null);
            if(parentList.size() > 0){
                for (Subnet subnet : parentList) {
                    this.genericSubnet(subnet);
                }
                return ResponseUtil.ok(parentList);
            }
        }else{
            // 校验子网是否存在
            Subnet subnet = this.zabbixSubnetService.selectObjById(id);
            if(subnet != null){
                // 当前网段
                Map map = new HashMap();
                map.put("subnet", subnet);
                // 获取从子网列表
                List<Subnet> subnets = this.zabbixSubnetService.selectSubnetByParentId(id);
                //
                map.put("subnets", subnets);
                // 查询IP addresses in subnets
                if(subnets.size() <= 0 && subnet.getMask() >= 21){
                    // 获取地址列表
                    // 获取最大Ip地址和最小Ip地址
                    String mask = IpUtil.bitMaskConvertMask(subnet.getMask());
                    Map networkMap = IpUtil.getNetworkIp(subnet.getIp(), mask);
                    String[] ips = IpUtil.getSubnetList(networkMap.get("network").toString(),
                            subnet.getMask());
                    if(ips.length > 0){
                        Map addresses = new LinkedHashMap();
                        for(String ip : ips){
                            Address address = this.addressService.selectObjByIp(IpUtil.ipConvertDec(ip));
                            if(address != null){
                                IpDetail ipDetail = this.ipDetailService.selectObjByIp(IpUtil.ipConvertDec(ip));
                                address.setIpDetail(ipDetail);
                            }
                            addresses.put(ip, address);
                        }
                        map.put("addresses", addresses);
                    }
                }else{
                    // 查询子网ip地址列表
                    Map params = new HashMap();
                    params.put("subnetId", subnet.getId());
                    List<Address> address = this.addressService.selectObjByMap(params);
                    map.put("mastSubnetAddress", address);
                }
                return ResponseUtil.ok(map);
            }
            return ResponseUtil.badArgument("网段不存在");
        }
        return ResponseUtil.ok();
    }

    @DeleteMapping
    public Object delete(@RequestParam(value = "id") Long id){
        Subnet subnet = this.zabbixSubnetService.selectObjById(id);
        if(subnet != null){
            // 查询子网ip地址列表
            Map params = new HashMap();
            params.put("subnetId", subnet.getId());
            List<Address> address = this.addressService.selectObjByMap(params);
            for (Address obj : address){
                this.addressService.delete(obj.getId());
            }
//            // 递归删除所有ip
            try {
                this.genericDel(subnet);
            } catch (Exception e) {
                e.printStackTrace();
            }
//            List<Subnet> subnets = this.zabbixSubnetService.selectSubnetByParentId(subnet.getId());
//            for (Subnet obj : subnets){
//                params.clear();
//                params.put("subnetId", obj.getId());
//                List<Address> addresses = this.addressService.selectObjByMap(params);
//                for (Address address1 : addresses){
//                    this.addressService.delete(address1.getId());
//                }
//                this.zabbixSubnetService.delete(obj.getId());
//            }
            // 批量
//            if(subnet != null){
//                this.genericSubnet(subnet);
//            }
//            this.zabbixSubnetService.delete(id);
        }
        return ResponseUtil.ok();
    }


    public void genericDel(Subnet subnet){
        List<Subnet> childs = this.zabbixSubnetService.selectSubnetByParentId(subnet.getId());
        if(childs.size() > 0){
            for(Subnet child : childs){
                genericDel(child);
            }
        }
        Map params = new HashMap();
        params.clear();
        params.put("subnetId", subnet.getId());
        List<Address> addresses = this.addressService.selectObjByMap(params);
        for (Address address : addresses){
            this.addressService.delete(address.getId());
        }
        this.zabbixSubnetService.delete(subnet.getId());
    }

    @GetMapping("/address")
    public Object subnet(){
        // 获取所有子网一级
        List<Subnet> subnets = this.zabbixSubnetService.selectSubnetByParentId(null);
        List<IpDetail> ipdetails = this.ipDetailService.selectObjByMap(null);
            if(subnets.size() > 0){
                for(IpDetail ipDetail : ipdetails){
                    if(ipDetail.getIp().equals("0.0.0.0")){
                        continue;
                    }
                    String ip = IpUtil.decConvertIp(Long.parseLong(ipDetail.getIp()));
                    if(!IpUtil.verifyIp(ip)){
                        continue;
                    }
                    // 判断ip地址是否属于子网
                    for(Subnet subnet : subnets){
                        genericNoSubnet(subnet, ipDetail);
                    }
                }
            }
        return null;
    }

    public void genericNoSubnet(Subnet subnet, IpDetail ipDetail){
        List<Subnet> childs = this.zabbixSubnetService.selectSubnetByParentId(subnet.getId());
        if(childs.size() > 0){
            for(Subnet child : childs){
                genericNoSubnet(child, ipDetail);
            }
        }else{
            // 判断ip是否属于从属子网
            boolean flag = IpUtil.ipIsInNet(IpUtil.decConvertIp(Long.parseLong(ipDetail.getIp())), subnet.getIp() + "/" + subnet.getMask());
            if(flag){
                Address obj = this.addressService.selectObjByIp(ipDetail.getIp());
                if(obj != null){
                    obj.setSubnetId(subnet.getId());
                    int i = this.addressService.update(obj);
                }else{
                    Address address = new Address();
                    address.setIp(ipDetail.getIp());
                    address.setHostName(ipDetail.getDeviceName());
                    address.setMac(ipDetail.getMac());
                    address.setSubnetId(subnet.getId());
                    int i = this.addressService.save(address);
                }
            }
        }
    }
}
