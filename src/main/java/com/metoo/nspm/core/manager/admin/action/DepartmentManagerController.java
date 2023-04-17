package com.metoo.nspm.core.manager.admin.action;

import com.metoo.nspm.core.manager.admin.tools.ShiroUserHolder;
import com.metoo.nspm.core.service.nspm.IDepartmentService;
import com.metoo.nspm.core.service.nspm.IUserService;
import com.metoo.nspm.core.utils.ResponseUtil;
import com.metoo.nspm.dto.GroupDto;
import com.metoo.nspm.entity.nspm.Department;
import com.metoo.nspm.entity.nspm.Group;
import com.metoo.nspm.entity.nspm.User;
import org.apache.ibatis.annotations.Delete;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/admin/department")
@RestController
public class DepartmentManagerController {

    @Autowired
    private IUserService userService;
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

    @GetMapping("/list")
    public Object list(){
        List<Department> parents = this.departmentService.queryChild(null);
        List<Department> branchList = new ArrayList<>();
        if(parents != null && parents.size() > 0){
            for (Department parent : parents) {
                if(this.genericDepartment(parent).size() > 0){
                    this.genericDepartment(parent);
                }
                branchList.add(parent);
            }
            return ResponseUtil.ok(branchList);
        }
        return ResponseUtil.ok();
    }

    public List<Department> genericDepartment(Department department){
        List<Department> departments = this.departmentService.queryChild(department.getId());
        if(departments.size() > 0){
            for(Department child : departments){
                List<Department> branchList = genericDepartment(child);
                if(branchList.size() > 0){
                    child.setBranchList(branchList);
                }
            }
            department.setBranchList(departments);
        }
        return departments;
    }

    @PostMapping("/save")
    public Object save(@RequestBody Department department){
        if(Strings.isNotBlank(department.getName())){
            return ResponseUtil.ok(this.departmentService.save(department));
        }
        return ResponseUtil.badArgument("请输入部门名称");
    }

    @DeleteMapping("/save")
    public Object delete(@RequestBody Department department){
        if(Strings.isNotBlank(department.getName())){
            return ResponseUtil.ok(this.departmentService.save(department));
        }
        return ResponseUtil.badArgument("请输入部门名称");
    }


}
