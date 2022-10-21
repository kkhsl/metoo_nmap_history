package com.metoo.nspm.core.service;

import com.metoo.nspm.entity.MacVendor;

import java.util.List;
import java.util.Map;

public interface IMacVendorService {

    List<MacVendor> selectObjByMap(Map params);
}
