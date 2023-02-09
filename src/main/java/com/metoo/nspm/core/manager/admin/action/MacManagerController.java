package com.metoo.nspm.core.manager.admin.action;

import com.github.pagehelper.Page;
import com.metoo.nspm.core.service.nspm.IMacService;
import com.metoo.nspm.core.service.nspm.IMacVendorService;
import com.metoo.nspm.core.service.nspm.INetworkElementService;
import com.metoo.nspm.core.utils.ResponseUtil;
import com.metoo.nspm.core.utils.query.PageInfo;
import com.metoo.nspm.dto.MacDTO;
import com.metoo.nspm.entity.nspm.Arp;
import com.metoo.nspm.entity.nspm.Mac;
import com.metoo.nspm.entity.nspm.MacVendor;
import com.metoo.nspm.entity.nspm.NetworkElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/admin/mac")
@RestController
public class MacManagerController {

    @Autowired
    private IMacService macService;
    @Autowired
    private IMacVendorService macVendorService;
    @Autowired
    private INetworkElementService networkElementService;

    @RequestMapping("/list")
    public Object deviceMac(@RequestBody MacDTO dto){
        NetworkElement networkElement = this.networkElementService.selectObjByUuid(dto.getUuid());
        if(networkElement != null){
            Map params = new HashMap();
            Page<Mac> page = this.macService.selectObjConditionQuery(dto);
            if(page.getResult().size() > 0){
                for(Mac mac : page.getResult()){
                    params.clear();
                    if (mac.getMac() != null && !mac.getMac().equals("")) {
                        String macAddr = mac.getMac();
                        int one_index = macAddr.indexOf(":");
                        String one = macAddr.substring(0, one_index);
                        if (one.equals("0")) {
                            one = "00";
                        }
                        int tow_index = macAddr.indexOf(":", one_index + 1);
                        String two = macAddr.substring(one_index, tow_index);
                        if (two.equals("0")) {
                            two = "00";
                        }
                        int three_index = macAddr.indexOf(":", tow_index + 1);
                        String three = macAddr.substring(tow_index, three_index);
                        if (three.equals("0")) {
                            three = "00";
                        }
                        macAddr = one + two + three;

                        params.put("mac", macAddr);
                        List<MacVendor> macVendors = this.macVendorService.selectObjByMap(params);
                        if (macVendors.size() > 0) {
                            MacVendor macVendor = macVendors.get(0);
                            mac.setVendor(macVendor.getVendor());
                        }
                    }
                }
                return ResponseUtil.ok(new PageInfo<Mac>(page));
            }
            return ResponseUtil.ok();
        }
        return ResponseUtil.badArgument("设备不存在");
    }
}
