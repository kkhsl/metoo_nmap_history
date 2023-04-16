package com.metoo.nspm.core.manager.admin.action;

import com.metoo.nspm.core.service.nspm.IDepartmentService;
import com.metoo.nspm.core.utils.ResponseUtil;
import com.metoo.nspm.entity.nspm.Department;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/admin/department")
@RestController
public class DepartmentManagerController {

    @Autowired
    private IDepartmentService departmentService;

    @GetMapping
    public Object getAll(){
        Map params = new HashMap();
        params.clear();
        params.put("orderBy", "sequence");
        params.put("orderType", "desc");
        List<Department> departments= this.departmentService.selectObjByMap(params);
        return ResponseUtil.ok(departments);
    }
}
