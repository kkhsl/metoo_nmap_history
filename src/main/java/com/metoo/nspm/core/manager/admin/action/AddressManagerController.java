package com.metoo.nspm.core.manager.admin.action;

import com.metoo.nspm.core.service.IAddressService;
import com.metoo.nspm.core.service.zabbix.ZabbixSubnetService;
import com.metoo.nspm.core.utils.ResponseUtil;
import com.metoo.nspm.core.utils.network.IpUtil;
import com.metoo.nspm.entity.Address;
import com.metoo.nspm.entity.zabbix.Subnet;
import org.nutz.lang.random.R;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/admin/address")
@RestController
public class AddressManagerController {

    @Autowired
    private IAddressService addressService;
    @Autowired
    private ZabbixSubnetService zabbixSubnetService;

    /**
     * 创建子网Ip地址
     * @param address
     * @return
     */
    @PostMapping
    public Object save(@RequestBody(required = false) Address address){
        Subnet subnet = this.zabbixSubnetService.selectObjById(address.getSubnetId());
        if(subnet != null){
            if(address.getId() != null){
                Address obj = this.addressService.selectObjById(address.getId());
                if(obj == null){
                    return ResponseUtil.badArgument();
                }
            }
            if(address.getIp() != null){
                if(!IpUtil.verifyIp(subnet.getIp())){
                    return ResponseUtil.badArgument("ip格式错误");
                }
                // 判断ip地址是否属于子网
                boolean flag = IpUtil.ipIsInNet(address.getIp(), subnet.getIp() + "/" + subnet.getMask());
                if(!flag){
                    return ResponseUtil.badArgument("ip地址不在子网边界内");
                }
                Address obj = this.addressService.selectObjByIp(IpUtil.ipConvertDec(address.getIp()));
                if(obj != null){
                    // copy数据到obj，然后更新
                    String[] IGNORE_ISOLATOR_PROPERTIES = new String[]{"id"};
                    BeanUtils.copyProperties(address, obj, IGNORE_ISOLATOR_PROPERTIES);
                    obj.setIp(IpUtil.ipConvertDec(obj.getIp()));
                    int i = this.addressService.save(obj);
                    if(i >= 1){
                        return ResponseUtil.ok();
                    }
                    return ResponseUtil.error();
                }
            }
            address.setIp(IpUtil.ipConvertDec(address.getIp()));
            int i = this.addressService.save(address);
            if(i >= 1){
                return ResponseUtil.ok();
            }
            return ResponseUtil.error();
        }
        return ResponseUtil.badArgument("请选择子网");
    }

    @DeleteMapping
    public Object delete(@RequestParam(value = "id") Long id){
        Address address = this.addressService.selectObjById(id);
        if(address != null){
            int i = this.addressService.delete(address.getId());
            if(i >= 1){
                return ResponseUtil.ok();
            }
            return ResponseUtil.error();
        }
        return ResponseUtil.badArgument();
    }

}
