package com.metoo.nspm.core.manager.admin.action;

import com.github.pagehelper.Page;
import com.metoo.nspm.core.service.nspm.IMacHistoryService;
import com.metoo.nspm.core.service.nspm.IMacService;
import com.metoo.nspm.core.service.nspm.IMacVendorService;
import com.metoo.nspm.core.service.nspm.INetworkElementService;
import com.metoo.nspm.core.utils.MyStringUtils;
import com.metoo.nspm.core.utils.ResponseUtil;
import com.metoo.nspm.core.utils.query.PageInfo;
import com.metoo.nspm.dto.MacDTO;
import com.metoo.nspm.entity.nspm.Arp;
import com.metoo.nspm.entity.nspm.Mac;
import com.metoo.nspm.entity.nspm.MacVendor;
import com.metoo.nspm.entity.nspm.NetworkElement;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequestMapping("/admin/mac")
@RestController
public class MacManagerController {

    @Autowired
    private IMacService macService;
    @Autowired
    private IMacHistoryService macHistoryService;
    @Autowired
    private IMacVendorService macVendorService;
    @Autowired
    private INetworkElementService networkElementService;

    @ApiOperation("设备-Mac列表")
    @RequestMapping("/list")
    public Object deviceMac(@RequestBody MacDTO dto){
        NetworkElement networkElement = this.networkElementService.selectObjByUuid(dto.getUuid());
        if(networkElement != null){
            Map params = new HashMap();
            dto.setMacFilter("1");
            dto.setOrderBy("vlan");
            dto.setOrderType("ASC");
            Page<Mac> page = null;
            if(dto.getTime() != null){
                page = this.macHistoryService.selectObjConditionQuery(dto);
            }else{
                page = this.macService.selectObjConditionQuery(dto);
            }
            if(page.getResult().size() > 0){
                for(Mac mac : page.getResult()){
                    params.clear();
                    if (mac.getMac() != null && !mac.getMac().equals("")) {
                        String macAddr = mac.getMac();
                        int index = MyStringUtils.acquireCharacterPosition(macAddr, ":", 3);
                        if(index != -1){
                            macAddr = macAddr.substring(0, index);
                            params.put("mac", macAddr);
                            List<MacVendor> macVendors = this.macVendorService.selectObjByMap(params);
                            if (macVendors.size() > 0) {
                                MacVendor macVendor = macVendors.get(0);
                                mac.setVendor(macVendor.getVendor());
                            }
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
