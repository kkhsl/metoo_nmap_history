package com.metoo.nspm.core.service;

import com.metoo.nspm.entity.Order;

public interface IOrderService {

    Order getObjByOrderId(Long orderId);

    Order getObjByOrderNo(String orderNo);

    int save(Order instance);
}
