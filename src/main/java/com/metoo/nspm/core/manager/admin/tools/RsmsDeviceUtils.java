package com.metoo.nspm.core.manager.admin.tools;

import com.metoo.nspm.core.service.nspm.IDepartmentService;
import com.metoo.nspm.core.service.nspm.IRsmsDeviceService;
import com.metoo.nspm.core.utils.network.IpUtil;
import com.metoo.nspm.entity.nspm.Department;
import com.metoo.nspm.entity.nspm.RsmsDevice;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class RsmsDeviceUtils {

    @Autowired
    private IRsmsDeviceService rsmsDeviceService;
    @Autowired
    private IDepartmentService departmentService;

    public int syncUpdateDevice(String ip,String mac,  String location, String duty,
                                Long departmentId){

        if(ip != null && IpUtil.verifyIp(ip)){
            Map params = new HashMap();
            params.put("ip", ip);
            List<RsmsDevice> deviceList = this.rsmsDeviceService.selectObjByMap(params);
            if(deviceList.size() > 0){
                RsmsDevice rsmsDevice = deviceList.get(0);
                rsmsDevice.setDepartmentId(departmentId);
                rsmsDevice.setMac(mac);
                rsmsDevice.setLocation(location);
                rsmsDevice.setDuty(duty);
                int i = this.rsmsDeviceService.update(rsmsDevice);
                return i;
            }
        }
        return 0;
    }

    public Map getDeviceInfo(String ip){
        Map map = new HashMap();
        if(Strings.isNotBlank(ip)){
            boolean flag = IpUtil.verifyIp(ip);
            if(flag){
                Map params = new HashMap();
                params.put("ip", ip);
                List<RsmsDevice> rsmsDevices = this.rsmsDeviceService.selectObjByMap(params);
                if(rsmsDevices.size() > 0){
                    RsmsDevice device = rsmsDevices.get(0);
                    map.put("mac", device.getMac());
                    map.put("location", device.getLocation());
                    map.put("duty", device.getDuty());
                    if(device.getDepartmentId() != null){
                        Department department = this.departmentService.selectObjById(device.getDepartmentId());
                        map.put("departmentName", department.getName());
                        map.put("departmentId", department.getId());
                    }
                    return map;
                }
            }
        }
        return map;
    }
}
