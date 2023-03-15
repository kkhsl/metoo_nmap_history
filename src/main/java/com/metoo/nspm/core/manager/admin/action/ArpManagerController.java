package com.metoo.nspm.core.manager.admin.action;

import com.github.pagehelper.Page;
import com.metoo.nspm.core.config.redis.util.MyRedisManager;
import com.metoo.nspm.core.service.nspm.IArpHistoryService;
import com.metoo.nspm.core.service.nspm.IArpService;
import com.metoo.nspm.core.service.nspm.IDeviceService;
import com.metoo.nspm.core.service.nspm.INetworkElementService;
import com.metoo.nspm.core.utils.ResponseUtil;
import com.metoo.nspm.core.utils.query.PageInfo;
import com.metoo.nspm.dto.ArpDTO;
import com.metoo.nspm.entity.nspm.Arp;
import com.metoo.nspm.entity.nspm.DeviceConfig;
import com.metoo.nspm.entity.nspm.NetworkElement;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sun.nio.ch.Net;

import java.util.*;
import java.util.stream.Collectors;

@RequestMapping("/admin/arp")
@RestController
public class ArpManagerController {

    @Autowired
    private IArpService arpService;
    @Autowired
    private IArpHistoryService arpHistoryService;
    @Autowired
    private INetworkElementService networkElementService;

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private static MyRedisManager redisMacChange = new MyRedisManager("change");

    @RequestMapping("/list")
    public Object deviceArp(@RequestBody(required = false) ArpDTO dto){
        NetworkElement networkElement = this.networkElementService.selectObjByUuid(dto.getUuid());
        if(networkElement != null){
            if(StringUtils.isNotEmpty(dto.getOrderBy())){
                dto.setOrderBy("ip + 0");
            }
            if(StringUtils.isNotEmpty(dto.getOrderType())){
                dto.setOrderType("ASC");
            }
            dto.setMacFilter("1");
            Page<Arp> page = null;
            if(dto != null){
                page = this.arpHistoryService.selectObjConditionQuery(dto);
            }else{
                page = this.arpService.selectObjConditionQuery(dto);
            }
            if(page.getResult().size() > 0){
                return ResponseUtil.ok(new PageInfo<Arp>(page));
            }
            return ResponseUtil.ok();
        }
        return ResponseUtil.badArgument("设备不存在");
    }

    @GetMapping("/conflicting")
    public Object conflicting(){
        int size = redisMacChange.size();
        if(size > 0){
            HashOperations<String, String, Integer> ops = redisTemplate.opsForHash();
            final Map<String, Integer> data = ops.entries("change");
            List<String> ips = new ArrayList<>();
            data.forEach((key, value) -> {
                if(value >= 2){
                    ips.add(key);
                }
            });
            Set<String> set = new HashSet<>();
            set.addAll(ips);
            ips.clear();
            ips.addAll(set);
            Map params = new HashMap();
            params.clear();
            params.put("ips", ips);
            List<Arp> arp = this.arpService.selectObjByMap(params);
            return ResponseUtil.ok(arp);
        }
        return ResponseUtil.ok();
    }

}
