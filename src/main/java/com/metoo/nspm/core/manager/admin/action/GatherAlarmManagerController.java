package com.metoo.nspm.core.manager.admin.action;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.metoo.nspm.core.service.nspm.IGatherAlarmService;
import com.metoo.nspm.core.utils.ResponseUtil;
import com.metoo.nspm.dto.GatherAlarmDTO;
import com.metoo.nspm.entity.nspm.GatherAlarm;
import com.metoo.nspm.entity.nspm.Res;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.ws.Response;

@RequestMapping("/admin/gather/alarm")
@RestController
public class GatherAlarmManagerController {

    @Autowired
    private IGatherAlarmService gatherAlarmService;

    @RequestMapping("/list")
    public Object alarms(@RequestBody(required = false) GatherAlarmDTO dto){
        Page<GatherAlarm> page = this.gatherAlarmService.selectConditionQuery(dto);
        return ResponseUtil.ok(new PageInfo<GatherAlarm>(page));
    }
}
