package com.metoo.nspm.core.service.nspm;

import com.metoo.nspm.entity.nspm.Department;

import java.util.List;
import java.util.Map;

public interface IDepartmentService {

    Department selectObjById(Long id);

    List<Department> selectObjByMap(Map params);
}
