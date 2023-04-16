package com.metoo.nspm.core.mapper.nspm;

import com.metoo.nspm.entity.nspm.Department;

import java.util.List;
import java.util.Map;

public interface DepartmentMapper {

    Department selectObjById(Long id);

    List<Department> selectObjByMap(Map params);
}