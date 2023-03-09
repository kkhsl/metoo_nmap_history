package com.metoo.nspm.core.service.nspm.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.metoo.nspm.core.mapper.nspm.GatherAlarmMapper;
import com.metoo.nspm.core.service.nspm.IArpService;
import com.metoo.nspm.core.service.nspm.IGatherAlarmService;
import com.metoo.nspm.core.service.nspm.IMacService;
import com.metoo.nspm.dto.GatherAlarmDTO;
import com.metoo.nspm.entity.nspm.Arp;
import com.metoo.nspm.entity.nspm.GatherAlarm;
import com.metoo.nspm.entity.nspm.Mac;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class GatherAlarmServiceImpl implements IGatherAlarmService
{

    @Autowired
    private GatherAlarmMapper gatherAlarmsMapper;
    @Autowired
    private IArpService arpService;
    @Autowired
    private IMacService macService;

    @Override
    public Page<GatherAlarm> selectConditionQuery(GatherAlarmDTO dto) {
        if(dto == null){
            dto = new GatherAlarmDTO();
        }
        Page<GatherAlarm> page = PageHelper.startPage(dto.getCurrentPage(), dto.getPageSize());
        this.gatherAlarmsMapper.selectConditionQuery(dto);
        return page;
    }

    @Override
    public List<GatherAlarm> selectObjByMap(Map params) {
        return this.gatherAlarmsMapper.selectObjByMap(params);
    }

    @Override
    public int save(GatherAlarm instance) {
        if(instance.getId() == null
                || instance.getId().equals("")){
            instance.setAddTime(new Date());
        }
        if(instance.getId() == null
                || instance.getId().equals("")){
            try {
                return this.gatherAlarmsMapper.save(instance);
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        }else{
            try {
                return this.gatherAlarmsMapper.update(instance);
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        }
    }

    @Override
    public int update(GatherAlarm instance) {
        try {
            return this.gatherAlarmsMapper.update(instance);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public void gatherAlarms() {
        List<Arp> arps = this.arpService.selectMacCountGT2();
        if(arps.size() > 0){
            Map params = new HashMap();
            for (Arp arp : arps) {
                if(arp.getMac() != null && !arp.getMac().equals("")){
                    GatherAlarm gatherAlarms = new GatherAlarm();
                    gatherAlarms.setDeviceUuid(arp.getUuid());
                    gatherAlarms.setDeviceInterface(arp.getInterfaceName());
                    gatherAlarms.setIp(arp.getIp());
                    gatherAlarms.setMac(arp.getMac());
                    params.clear();
                    params.put("mac", arp.getMac());
                    params.put("tag", "L");
                    List<Mac> macs = this.macService.selectObjByMap(params);
                    if(macs.size() > 0){
                        gatherAlarms.setType(1);
                    }else{
                        params.clear();
                        params.put("mac", arp.getMac());
                        params.put("tag", "DT");
                        List<Mac> dtMacs = this.macService.selectObjByMap(params);
                        if(dtMacs.size() > 0){
                            gatherAlarms.setType(2);
                            gatherAlarms.setRemoteDeviceUuid(arp.getRemoteUuid());
                            gatherAlarms.setRemoteDeviceInterface(arp.getRemoteInterface());
                        }
                    }
                    this.save(gatherAlarms);
                }
            }
        }
    }
}
