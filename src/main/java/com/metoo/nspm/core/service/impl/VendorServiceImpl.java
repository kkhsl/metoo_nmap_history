package com.metoo.nspm.core.service.impl;

import com.metoo.nspm.core.mapper.VendorMapper;
import com.metoo.nspm.core.service.IVendorService;
import com.metoo.nspm.entity.Vendor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class VendorServiceImpl implements IVendorService {

    @Autowired
    private VendorMapper vendorMapper;

    @Override
    public Vendor selectObjById(Long id) {
        return this.vendorMapper.selectObjById(id);
    }

    @Override
    public List<Vendor> selectConditionQuery(Map params) {
        return this.vendorMapper.selectConditionQuery(params);
    }
}
