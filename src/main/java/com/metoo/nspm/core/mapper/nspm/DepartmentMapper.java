package com.metoo.nspm.core.mapper.nspm;

import com.metoo.nspm.entity.nspm.Department;
import com.metoo.nspm.entity.nspm.Group;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface DepartmentMapper {

    Department selectObjById(Long id);

    List<Department> selectObjByMap(Map params);

    List<Department> queryChild(Long id);

    int save(Department instance);

    int update(Department instance);
}