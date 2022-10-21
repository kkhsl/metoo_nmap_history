package com.metoo.nspm.core.service;

import com.metoo.nspm.dto.RsmsDeviceDTO;
import com.metoo.nspm.entity.RsmsDevice;
import com.metoo.nspm.vo.RsmsDeviceVo;
import com.github.pagehelper.Page;

import java.util.List;
import java.util.Map;

public interface IRsmsDeviceService {

    RsmsDevice getObjById(Long id);

    RsmsDevice getObjAndProjectById(Long id);

    Page<RsmsDevice> selectConditionQuery(RsmsDeviceDTO instance);

    List<RsmsDeviceVo> selectNameByMap(Map map);

    List<RsmsDevice> selectObjByMap(Map map);

    int save(RsmsDevice instance);

    int update(RsmsDevice instance);

    int delete(Long id);

    int batchDel(String ids);
}
