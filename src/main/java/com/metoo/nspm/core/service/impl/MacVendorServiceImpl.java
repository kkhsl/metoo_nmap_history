package com.metoo.nspm.core.service.impl;

import com.metoo.nspm.core.mapper.MacVendorMapper;
import com.metoo.nspm.core.service.IMacVendorService;
import com.metoo.nspm.entity.MacVendor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class MacVendorServiceImpl implements IMacVendorService {

    @Autowired
    private MacVendorMapper macVendorMapper;

    @Override
    public List<MacVendor> selectObjByMap(Map params) {
        return this.macVendorMapper.selectObjByMap(params);
    }
}
