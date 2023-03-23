package com.metoo.nspm.core.manager.admin.action;

import com.metoo.nspm.core.service.nspm.IpAlterationService;
import com.metoo.nspm.core.utils.ResponseUtil;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/admin/ip/alteration")
@RestController
public class IpAlterationManagerController {

    @Autowired
    private IpAlterationService ipAlterationService;

    @GetMapping
    public Object list(){
        Date now = new Date();
        Date now_10 = new Date(now.getTime() - 600000); //10分钟前的时间
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//可以方便地修改日期格式
        String nowTime_10 = dateFormat.format(now_10);

        Map params = new HashMap();
        params.put("addTime", now_10);
        List list = this.ipAlterationService.selectObjByAddTime(params);
        return ResponseUtil.ok(list);
    }
}
