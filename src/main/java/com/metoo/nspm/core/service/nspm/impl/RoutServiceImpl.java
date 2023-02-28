package com.metoo.nspm.core.service.nspm.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.metoo.nspm.core.mapper.nspm.zabbix.ZabbixRouteMapper;
import com.metoo.nspm.core.service.nspm.IRoutService;
import com.metoo.nspm.core.utils.ResponseUtil;
import com.metoo.nspm.core.utils.network.IpUtil;
import com.metoo.nspm.core.utils.query.PageInfo;
import com.metoo.nspm.dto.zabbix.RoutDTO;
import com.metoo.nspm.entity.nspm.Route;
import org.apache.commons.beanutils.BeanMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class RoutServiceImpl implements IRoutService {

    @Autowired
    private ZabbixRouteMapper zabbixRoutMapper;

    @Override
    public Route selectObjById(Long id) {
        return this.zabbixRoutMapper.selectObjById(id);
    }

    @Override
    public Page<Route> selectConditionQuery(RoutDTO instance) {
        if(instance == null){
            instance = new RoutDTO();
        }
        Page<Route> page = PageHelper.startPage(instance.getCurrentPage(), instance.getPageSize());
        this.zabbixRoutMapper.selectConditionQuery(instance);
        return page;
    }

    @Override
    public List<Route> selectObjByMap(Map params) {
        return this.zabbixRoutMapper.selectObjByMap(params);
    }

    @Override
    public int save(Route instance) {
        try {
            if(instance.getId() == null){
                instance.setAddTime(new Date());
            }
            return this.zabbixRoutMapper.save(instance);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int update(Route instance) {
        try {
            return this.zabbixRoutMapper.update(instance);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int delete(Long id) {
        try {
            return this.zabbixRoutMapper.delete(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void truncateTable() {
        this.zabbixRoutMapper.truncateTable();
    }

    @Override
    public List<Route> queryDestDevice(Map params) {
        return this.zabbixRoutMapper.queryDestDevice(params);
    }

    @Override
    public Route selectDestDevice(Map params) {
        return this.zabbixRoutMapper.selectDestDevice(params);
    }

    @Override
    public List<Route> selectNextHopDevice(Map params) {
        return this.zabbixRoutMapper.selectNextHopDevice(params);
    }

    @Override
    public void copyRoutTemp() {
        this.zabbixRoutMapper.copyRoutTemp();
    }

    @Override
    public Object queryDeviceRout(RoutDTO dto, String ip) {
        if(StringUtils.isEmpty(dto.getDestination())){
            Page<Route> page = null;
            dto.setOrderBy("destination + 0");
            dto.setOrderType("asc");
            page = this.selectConditionQuery(dto);
            return ResponseUtil.ok(new PageInfo<Route>(page));
        }else{
            Page<Route> page = new Page<>();
            dto.setDestination(null);
            Map<String, Object> params = new BeanMap(dto);
            List<Route> routes = this.selectObjByMap(params);
            if (routes.size() > 0) {
                List<Route> routList = new ArrayList<>();
                for (Route rout : routes) {
                    boolean flag = IpUtil.ipIsInNet(ip, rout.getCidr());
                    if (flag) {
                        routList.add(rout);
                    }
                }
                if (routList.size() > 0) {
                    int maskBitMax = routList.get(0).getMaskBit();
                    List<Route> routList2 = new ArrayList<>();
                    for (Route rout : routList) {
                        if (rout.getMaskBit() >= maskBitMax) {
                            routList2.clear();
                            routList2.add(rout);
                        }
                    }
                    page.setPageNum(1);
                    page.setPageSize(routList2.size());
                    page.setPageSize(routList2.size());
                    page.getResult().clear();
                    page.getResult().addAll(routList2);
                } else {
                    dto.setDestination("0");
                    page = this.selectConditionQuery(dto);
                }
            }
            return ResponseUtil.ok(new PageInfo<Route>(page));
        }
    }


}
