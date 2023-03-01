package com.metoo.nspm.core.websocket.api;

import com.alibaba.fastjson.JSONObject;
import com.metoo.nspm.core.config.websocket.demo.NoticeWebsocketResp;
import com.metoo.nspm.core.manager.admin.tools.DateTools;
import com.metoo.nspm.core.manager.admin.tools.MacUtil;
import com.metoo.nspm.core.service.nspm.IMacHistoryService;
import com.metoo.nspm.core.service.nspm.IMacService;
import com.metoo.nspm.core.service.nspm.IMacVendorService;
import com.metoo.nspm.entity.nspm.Mac;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.crypto.hash.Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Array;
import java.util.*;

@RequestMapping("/websocket/api/zabbix")
@RestController
public class TopologyManagerApi {

    @Autowired
    private IMacService macService;
    @Autowired
    private IMacHistoryService macHistoryService;
    @Autowired
    private IMacVendorService macVendorService;
    @Autowired
    private MacUtil macUtil;

    @ApiOperation("设备 Mac (DT))")
    @GetMapping(value = {"/mac/dt"})
    public NoticeWebsocketResp getObjMac(@RequestParam(value = "requestParams", required = false) String requestParams){
//        Date time = null;
//        List<String> list = new ArrayList<>();
        Map params = JSONObject.parseObject(String.valueOf(requestParams), Map.class);
        Date time = DateTools.parseDate(String.valueOf(params.get("time")), "yyyy-MM-dd HH:mm");
        List<String> list = JSONObject.parseObject(String.valueOf(params.get("params")), List.class);
//        if(requestParams instanceof Map){
//            Map params = JSONObject.parseObject(String.valueOf(requestParams), Map.class);
//            time = DateTools.parseDate(String.valueOf(params.get("time")), "yyyy-MM-dd HH:mm");
//            list = JSONObject.parseObject(String.valueOf(params.get("params")), List.class);
//        }
//        if(requestParams instanceof String){
//            list = JSONObject.parseObject(String.valueOf(requestParams), List.class);
//        }
        Map map = new HashMap();
        Map prm = new HashMap();
        if(time == null){
            for (String item : list) {
                prm.clear();
                prm.put("uuid", item);
                prm.put("tag", "DT");
                List<Mac> macs = this.macService.selectByMap(prm);
                this.macUtil.macJoint(macs);
                map.put(item, macs);
            }
        }else{
            for (String item : list) {
                prm.clear();
                prm.put("uuid", item);
                prm.put("tag", "DT");
                prm.put("time", time);
                List<Mac> macs = this.macHistoryService.selectByMap(prm);
                this.macUtil.macJoint(macs);
                map.put(item, macs);
            }
        }

        NoticeWebsocketResp rep = new NoticeWebsocketResp();
        if(map.size() > 0){
            rep.setNoticeType("4");
            rep.setNoticeStatus(1);
            rep.setNoticeInfo(map);
        }else{
            rep.setNoticeType("4");
            rep.setNoticeStatus(0);
        }
        return rep;
    }
}
