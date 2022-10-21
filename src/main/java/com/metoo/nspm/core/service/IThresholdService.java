package com.metoo.nspm.core.service;

import com.metoo.nspm.entity.Threshold;

public interface IThresholdService {

    Threshold query();

    int update(Threshold instance);
}
