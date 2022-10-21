package com.metoo.nspm.core.service;

import com.metoo.nspm.dto.DomainDTO;
import com.metoo.nspm.entity.Domain;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface IDomainService {

    Domain selectObjById(Long id);

    List<Domain> selectObjConditionQuery(DomainDTO dto);

    List<Domain> selectObjByMap(Map params);

    int save(Domain instance);

    int update(Domain instance);

    int delete(Long id);
}
