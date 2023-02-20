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
    private IMacVendorService macVendorService;
    @Autowired
    private INetworkElementService networkElementService;

    public static void main(String[] args) {
        String str = "00:25:9e:03:76:12";
        Pattern pattern = Pattern.compile(":");
        Matcher findMatcher = pattern.matcher(str);
        List list = new ArrayList();
        while(findMatcher.find()) {
            list.add(findMatcher.start());
        }
        System.out.println(list.get(2));
    }


    /**
     * 设备Mac 列表
     * @param dto
     * @return
     */
    @RequestMapping("/list")
    public Object deviceMac(@RequestBody MacDTO dto){
        NetworkElement networkElement = this.networkElementService.selectObjByUuid(dto.getUuid());
        if(networkElement != null){
            Map params = new HashMap();
            dto.setMacFilter("1");
            dto.setOrderBy("vlan");
            dto.setOrderType("ASC");
            Page<Mac> page = this.macService.selectObjConditionQuery(dto);
            if(page.getResult().size() > 0){
                for(Mac mac : page.getResult()){
                    params.clear();
                    if (mac.getMac() != null && !mac.getMac().equals("")) {
                        String macAddr = mac.getMac();
                        int index = com.metoo.nspm.core.utils.StringUtils.acquireCharacterPosition(macAddr, ":", 3);
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
