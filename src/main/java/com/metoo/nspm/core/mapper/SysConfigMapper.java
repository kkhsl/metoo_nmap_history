package com.metoo.nspm.core.mapper;

import com.metoo.nspm.entity.SysConfig;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysConfigMapper {

    SysConfig findObjById();

    /**
     * 查询所有 配置
     * @return
     */
    SysConfig select();

    /**
     * 更新
     * @param instance
     * @return
     */
    int update(SysConfig instance);
}
