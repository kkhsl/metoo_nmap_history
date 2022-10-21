package com.metoo.nspm.core.service;


import com.metoo.nspm.entity.Vendor;

import java.util.List;
import java.util.Map;

public interface IVendorService {

    Vendor selectObjById(Long id);

    List<Vendor> selectConditionQuery(Map params);
}
