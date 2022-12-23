package com.metoo.nspm.core.websocket.api;

import com.alibaba.fastjson.JSONObject;
import com.metoo.nspm.core.config.socket.NoticeWebsocketResp;
import com.metoo.nspm.core.service.zabbix.IProblemService;
import com.metoo.nspm.core.utils.ResponseUtil;
import com.metoo.nspm.entity.zabbix.Problem;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/websocket/api/problem")
@RestController
public class ProblemManagerApi {

    @Autowired
    private IProblemService problemService;

    @ApiOperation("告警信息")
    @GetMapping
    public NoticeWebsocketResp problem(
            @RequestParam(value = "requestParams", required = false) String requestParams){
        Map<String, Object> requestParam = JSONObject.parseObject(requestParams, Map.class);
        Map params = new HashMap();
        params.put("limit", requestParam.get("limit"));
        List<Problem> problemList = this.problemService.selectObjByMap(params);
        NoticeWebsocketResp rep = new NoticeWebsocketResp();
        if(problemList.size() > 0){
            rep.setNoticeType("6");
            rep.setNoticeStatus(1);
            rep.setNoticeInfo(problemList);
        }else{
            rep.setNoticeType("6");
            rep.setNoticeStatus(0);
        }
        return rep;
    }

}
