package com.metoo.nspm.core.manager.admin.action;

import com.metoo.nspm.core.manager.admin.tools.GroupTools;
import com.metoo.nspm.core.manager.admin.tools.ShiroUserHolder;
import com.metoo.nspm.core.service.nspm.IDomainService;
import com.metoo.nspm.core.service.nspm.IGroupService;
import com.metoo.nspm.core.service.nspm.IVlanService;
import com.metoo.nspm.core.service.nspm.ZabbixSubnetService;
import com.metoo.nspm.core.service.phpipam.IpamSubnetService;
import com.metoo.nspm.core.utils.ResponseUtil;
import com.metoo.nspm.core.utils.network.IpUtil;
import com.metoo.nspm.entity.nspm.*;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @description Vlan管理
 *
 * @author HKK
 *
 * @create 2023/02/22
 *
 */
@RequestMapping("/admin/vlan")
@RestController
public class VlanManagerController {

    @Autowired
    private IVlanService vlanService;
    @Autowired
    private IDomainService domainService;
    @Autowired
    private IGroupService groupService;
    @Autowired
    private ZabbixSubnetService subnetService;
    @Autowired
    private GroupTools groupTools;

    /**
     *
     * @param domainId 非必填，默认查询全部
     * @return
     */
    @ApiOperation("Vlan列表")
    @GetMapping
    public Object list(@RequestParam(value = "domainId",required = false) Long domainId){
            User user = ShiroUserHolder.currentUser();
            Group group = this.groupService.selectObjById(user.getGroupId());
            if(group != null){
                Map params = new HashMap();
                Set<Long> ids = this.groupTools.genericGroupId(group.getId());
                params.put("groupIds", ids);
                Domain domain = this.domainService.selectObjById(domainId);
                if(domain != null){
                    params.put("domainId", domain.getId());
                }
                List<Vlan> vlans = this.vlanService.selectObjByMap(params);
//                for (Vlan vlan : vlans) {
//                    if(vlan.getSubnetId() != null && !vlan.getSubnetId().equals("")){
//                        Subnet subnet = this.subnetService.selectObjById(vlan.getSubnetId());
//                        vlan.setSubnetIp(IpUtil.decConvertIp(Long.parseLong(subnet.getIp())));
//                    }
//                }
//
//                vlans.forEach(e -> {
//                    if(e.getSubnetId() != null && !e.getSubnetId().equals("")){
//                        Subnet subnet = this.subnetService.selectObjById(e.getSubnetId());
//                        e.setSubnetIp(subnet.getIp());
//                    }
//                });

                vlans.stream().forEach(e -> {
                    if(e.getSubnetId() != null && !e.getSubnetId().equals("")) {
                        Subnet subnet = this.subnetService.selectObjById(e.getSubnetId());
                        e.setSubnetIp(subnet.getIp());
                        e.setMaskBit(subnet.getMask());
                    }
                });
                return ResponseUtil.ok(vlans);

        }
        return ResponseUtil.ok();
    }

    @ApiOperation("Vlan添加")
    @GetMapping("/add")
    public Object add(){
        Map map = new HashMap();
        Map params = new HashMap();
        List<Domain> domains = this.domainService.selectObjByMap(params);
        map.put("domain", domains);
//        List<Subnet> subnets = this.subnetService.selectObjByMap(null);
//        map.put("subnet", subnets);
        List<Subnet> parentList = this.subnetService.selectSubnetByParentId(null);
        if(parentList.size() > 0){
            for (Subnet subnet : parentList) {
                this.genericSubnet(subnet);
            }
        }
        map.put("subnet", parentList);
        return ResponseUtil.ok(map);
    }

    public List<Subnet> genericSubnet(Subnet subnet){
        List<Subnet> subnets = this.subnetService.selectSubnetByParentId(subnet.getId());
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

    @ApiOperation("VLan更新,数据回显")
    @GetMapping("/update")
    public Object updadte(@RequestParam(value = "id") Long id){
        Map map = new HashMap();
        Vlan vlan = this.vlanService.selectObjById(id);
        if(vlan == null){
            return ResponseUtil.badArgument("Vlan不存在");
        }
        Domain domain = this.domainService.selectObjById(vlan.getDomainId());
        if(domain != null){
            vlan.setDomainName(domain.getName());
        }
        if(vlan.getSubnetId() != null){
            Subnet subnet = this.subnetService.selectObjById(vlan.getSubnetId());
            vlan.setSubnetIp(subnet.getIp());
            vlan.setMaskBit(subnet.getMask());
        }
        map.put("vlan", vlan);
        Map params = new HashMap();
        List<Domain> domains = this.domainService.selectObjByMap(params);
        map.put("domain", domains);
//        List<Subnet> subnets = this.subnetService.selectObjByMap(null);
//        map.put("subnet", subnets);

        List<Subnet> parentList = this.subnetService.selectSubnetByParentId(null);
        if(parentList.size() > 0){
            for (Subnet subnet : parentList) {
                this.genericSubnet(subnet);
            }
        }
        map.put("subnet", parentList);
        return ResponseUtil.ok(map);
    }

    @PostMapping
    public Object save(@RequestBody Vlan vlan){
        if(vlan.getName() == null || vlan.getName().equals("")){
            return ResponseUtil.badArgument("名称不能为空");
        }else{
            Map params = new HashMap();
            params.put("vlanId", vlan.getId());
            params.put("name", vlan.getName());
            // 当前分组内不重名
            User user = ShiroUserHolder.currentUser();
            Group group = this.groupService.selectObjById(user.getGroupId());
            if(group != null) {
                Set<Long> ids = this.groupTools.genericGroupId(group.getId());
                params.put("groupIds", ids);
            }
            List<Vlan> domains = this.vlanService.selectObjByMap(params);
            if(domains.size() > 0){
                return ResponseUtil.badArgument("名称重复");
            }
        }
        if(vlan.getDomainId() != null && !vlan.getDomainId().equals("")){
            Domain domain = this.domainService.selectObjById(vlan.getDomainId());
            if(domain == null){
                return ResponseUtil.badArgument("二层域不存在");
            }
        }

        if(vlan.getSubnetId() != null){
            Subnet subnet = this.subnetService.selectObjById(vlan.getSubnetId());
            if(subnet == null){
                return ResponseUtil.badArgument("网段不存在");
            }
        }
        int result = this.vlanService.save(vlan);
        if(result >= 1){
            return ResponseUtil.ok();
        }
        return ResponseUtil.error();
    }

    @DeleteMapping
    public Object delete(String ids){
        for (String id : ids.split(",")){
            Vlan vlan = this.vlanService.selectObjById(Long.parseLong(id));
            if(vlan == null){
                return ResponseUtil.badArgument();
            }
            int i = this.vlanService.delete(Long.parseLong(id));
            if(i <= 0){
                return ResponseUtil.error();
            }
        }
        return ResponseUtil.ok();
    }
}
