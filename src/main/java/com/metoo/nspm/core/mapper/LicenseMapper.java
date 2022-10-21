package com.metoo.nspm.core.mapper;

import com.metoo.nspm.entity.License;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LicenseMapper {

    List<License> query();

    int update(License instance);
}
