package com.metoo.nspm.core.service.nspm.impl;

import com.metoo.nspm.core.mapper.nspm.DepartmentMapper;
import com.metoo.nspm.core.service.nspm.IDepartmentService;
import com.metoo.nspm.entity.nspm.Department;
import com.metoo.nspm.entity.nspm.Group;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
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

    @Override
    public List<Department> queryChild(Long id) {
        return this.departmentMapper.queryChild(id);
    }

    @Override
    public int save(Department instance) {
        if(instance.getId() == null || instance.getId().equals("")){
            instance.setAddTime(new Date());
            try {
               return this.departmentMapper.save(instance);
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        }else{
            try {
                return this.departmentMapper.update(instance);
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        }
    }

    @Override
    public int update(Department instance) {
        try {
            return this.departmentMapper.update(instance);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

}
