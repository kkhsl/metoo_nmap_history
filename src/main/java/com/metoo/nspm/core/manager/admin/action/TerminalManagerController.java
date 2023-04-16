package com.metoo.nspm.core.manager.admin.action;

import com.metoo.nspm.core.manager.admin.tools.RsmsDeviceUtils;
import com.metoo.nspm.core.service.nspm.IDepartmentService;
import com.metoo.nspm.core.service.nspm.ITerminalService;
import com.metoo.nspm.core.service.nspm.ITerminalTypeService;
import com.metoo.nspm.core.utils.ResponseUtil;
import com.metoo.nspm.core.utils.network.IpUtil;
import com.metoo.nspm.entity.nspm.Department;
import com.metoo.nspm.entity.nspm.RsmsDevice;
import com.metoo.nspm.entity.nspm.Terminal;
import com.metoo.nspm.entity.nspm.TerminalType;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/terminal")
public class TerminalManagerController {

    @Autowired
    private ITerminalService terminalService;
    @Autowired
    private RsmsDeviceUtils rsmsDeviceUtils;
    @Autowired
    private IDepartmentService departmentService;
    @Autowired
    private ITerminalTypeService terminalTypeService;

    @GetMapping("/{id}")
    public Object edit(@PathVariable(value = "id") String id){
        if(id != null && !id.equals("")){
            Terminal terminal = this.terminalService.selectObjById(Long.parseLong(id));
            if(terminal != null){
                Map params = new HashMap();
                params.clear();
                params.put("orderBy", "sequence");
                params.put("orderType", "desc");
                List<Department> departments= this.departmentService.selectObjByMap(params);
                Map map = new HashMap();
                map.put("department", departments);
                // 查询设备信息
                Map deviceInfo = this.rsmsDeviceUtils.getDeviceInfo(terminal.getIp());
                map.put("deviceInfo", deviceInfo);
                List<TerminalType> terminalTypeList = this.terminalTypeService.selectObjAll();
                map.put("terminalType", terminalTypeList);
                map.put("terminal", terminal);
                return ResponseUtil.ok(map);
            }
        }
        return ResponseUtil.ok();
    }

    @PutMapping
    public Object update(@RequestBody Terminal instance){
        int i = this.terminalService.update(instance);
        if(i >= 1){
            return ResponseUtil.ok();
        }else{
            return ResponseUtil.error("保存失败");
        }
    }

}
