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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequestMapping("/websocket/api/gather/alarm")
@RestController
public class GatherAlarmmanagerApi {

    @Autowired
    private RedisResponseUtils redisResponseUtils;
    @Autowired
    private IGatherAlarmService gatherAlarmService;

    @RequestMapping("/list")
    public Object alarms(@RequestParam(value = "requestParams") String requestParams){
        NoticeWebsocketResp rep = new NoticeWebsocketResp();
        Map param = JSONObject.parseObject(String.valueOf(requestParams), Map.class);
        String sessionId = (String) param.get("sessionId");
        GatherAlarmDTO dto = JSONObject.parseObject(param.get("params").toString(), GatherAlarmDTO.class);
        Page<GatherAlarm> page = this.gatherAlarmService.selectConditionQuery(dto);
        rep.setNoticeType("10");
        rep.setNoticeInfo(new PageInfo<GatherAlarm>(page));
        this.redisResponseUtils.syncRedis(sessionId, new PageInfo<GatherAlarm>(page), 10);
        return rep;
    }

}
