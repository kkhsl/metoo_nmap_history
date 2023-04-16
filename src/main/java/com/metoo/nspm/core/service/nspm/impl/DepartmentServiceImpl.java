package com.metoo.nspm.core.service.nspm.impl;

import com.metoo.nspm.core.mapper.nspm.DepartmentMapper;
import com.metoo.nspm.core.service.nspm.IDepartmentService;
import com.metoo.nspm.entity.nspm.Department;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class DepartmentServiceImpl implements IDepartmentService {

    @Resource
    private DepartmentMapper departmentMapper;

    @Override
    public Department selectObjById(Long id) {
        return this.departmentMapper.selectObjById(id);
    }

    @Override
    public List<Department> selectObjByMap(Map params) {
        return this.departmentMapper.selectObjByMap(params);
    }

}
