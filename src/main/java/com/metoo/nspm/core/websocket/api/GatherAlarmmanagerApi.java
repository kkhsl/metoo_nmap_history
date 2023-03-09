package com.metoo.nspm.core.websocket.api;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.metoo.nspm.core.config.websocket.demo.NoticeWebsocketResp;
import com.metoo.nspm.core.service.nspm.IGatherAlarmService;
import com.metoo.nspm.core.utils.ResponseUtil;
import com.metoo.nspm.dto.GatherAlarmDTO;
import com.metoo.nspm.dto.NetworkElementDto;
import com.metoo.nspm.entity.nspm.GatherAlarm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequestMapping("/websocket/api/gather/alarm")
@RestController
public class GatherAlarmmanagerApi {

    @Autowired
    private IGatherAlarmService gatherAlarmService;

    @RequestMapping("/list")
    public Object alarms(@RequestParam(value = "requestParams") String requestParams){

        GatherAlarmDTO dto = JSONObject.parseObject(requestParams, GatherAlarmDTO.class);
        Page<GatherAlarm> page = this.gatherAlarmService.selectConditionQuery(dto);
        NoticeWebsocketResp rep = new NoticeWebsocketResp();
        if(page.getResult().size() > 0){
            rep.setNoticeStatus(1);
            rep.setNoticeType("10");
            rep.setNoticeInfo(new PageInfo<GatherAlarm>(page));
        }else{
            rep.setNoticeType("10");
            rep.setNoticeStatus(0);
        }
        return rep;
    }

}
