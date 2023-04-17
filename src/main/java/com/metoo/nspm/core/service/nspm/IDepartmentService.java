package com.metoo.nspm.core.service.nspm;

import com.metoo.nspm.entity.nspm.Department;
import com.metoo.nspm.entity.nspm.Group;

import java.util.List;
import java.util.Map;

public interface IDepartmentService {

    Department selectObjById(Long id);

    List<Department> selectObjByMap(Map params);

    List<Department> queryChild(Long id);

    int save(Department instance);

    int update(Department instance);




}
