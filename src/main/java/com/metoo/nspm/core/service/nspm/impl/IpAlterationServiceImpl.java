package com.metoo.nspm.core.service.nspm.impl;

import com.metoo.nspm.core.mapper.nspm.IpAlterationMapper;
import com.metoo.nspm.core.service.nspm.IpAlterationService;
import com.metoo.nspm.entity.nspm.IpAlteration;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class IpAlterationServiceImpl implements IpAlterationService {

    @Resource
    private IpAlterationMapper ipAlterationMapper;

    @Override
    public List<IpAlteration> selectObjByMap(Map params) {
        return this.ipAlterationMapper.selectObjByMap(params);
    }

    @Override
    public List<IpAlteration> selectObjByAddTime(Map params) {
        return this.ipAlterationMapper.selectObjByAddTime(params);
    }

    @Override
    public void RecordChange(String ip, String mac) {
        // 查找ip，根据时间降序查找第一条
        Map params = new HashMap();
        params.put("orderBy", "addTime");
        params.put("orderType", "DESC");
        params.put("ip", ip);
        params.put("mac", mac);
        List<IpAlteration> ipAlterations = this.ipAlterationMapper.selectObjByMap(params);
        if(ipAlterations.size() <= 0){
            // 新增
            try {
                this.save(ip, mac);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            IpAlteration ipAlteration = ipAlterations.get(0);
            if(!ipAlteration.getMac().equals(mac)){
                try {
                    this.save(ip, mac);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public int save(String ip, String mac){
        IpAlteration ipAlteration = new IpAlteration();
        ipAlteration.setAddTime(new Date());
        ipAlteration.setIp(ip);
        ipAlteration.setMac(mac);
        return this.ipAlterationMapper.save(ipAlteration);
        //        synchronized (ipAlteration){
//            ipAlteration.setAddTime(new Date());
//            ipAlteration.setIp(ip);
//            ipAlteration.setMac(mac);
//            return this.ipAlterationMapper.save(ipAlteration);
//        }
    }
}
