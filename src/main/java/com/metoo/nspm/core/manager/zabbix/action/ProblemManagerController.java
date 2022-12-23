package com.metoo.nspm.core.manager.zabbix.action;

import com.metoo.nspm.core.service.zabbix.IProblemService;
import com.metoo.nspm.core.utils.ResponseUtil;
import com.metoo.nspm.entity.zabbix.Problem;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/admin/zabbix/problem")
@RestController
public class ProblemManagerController {

    @Autowired
    private IProblemService problemService;

    @ApiOperation("告警信息")
    @GetMapping
    public Object problem(@RequestParam(required = false) String ip,
                          @RequestParam(required = false) Integer limit){
        Map params = new HashMap();
        params.put("ip", ip);
        params.put("limit", limit);
        List<Problem> problemList = this.problemService.selectObjByMap(params);
        return ResponseUtil.ok(problemList);
    }


}
