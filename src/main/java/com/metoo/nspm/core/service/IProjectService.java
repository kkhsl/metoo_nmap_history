package com.metoo.nspm.core.service;

import com.github.pagehelper.Page;
import com.metoo.nspm.dto.ProjectDTO;
import com.metoo.nspm.entity.Project;

import java.util.List;
import java.util.Map;

public interface IProjectService {

    Project selectObjById(Long id);

    Page<Project> selectObjConditionQuery(ProjectDTO dto);

    List<Project> selectObjByMap(Map params);

    int save(Project instance);

    int update(Project instance);

    int delete(Long id);
}
