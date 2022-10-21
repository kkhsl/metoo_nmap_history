package com.metoo.nspm.core.mapper;

import com.metoo.nspm.entity.Vendor;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface VendorMapper {

    Vendor selectObjById(Long id);

    List<Vendor> selectConditionQuery(Map params);

}
