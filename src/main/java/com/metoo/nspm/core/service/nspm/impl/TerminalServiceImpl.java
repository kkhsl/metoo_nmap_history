package com.metoo.nspm.core.service.nspm.impl;

import com.metoo.nspm.core.mapper.nspm.TerminalMapper;
import com.metoo.nspm.core.service.nspm.IMacHistoryService;
import com.metoo.nspm.core.service.nspm.IMacService;
import com.metoo.nspm.core.service.nspm.ITerminalService;
import com.metoo.nspm.core.service.nspm.ITerminalTypeService;
import com.metoo.nspm.core.service.zabbix.ItemService;
import com.metoo.nspm.entity.nspm.Mac;
import com.metoo.nspm.entity.nspm.Terminal;
import com.metoo.nspm.entity.nspm.TerminalType;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class TerminalServiceImpl implements ITerminalService {

    @Autowired
    private TerminalMapper terminalMapper;
    @Autowired
    private ITerminalTypeService terminalTypeService;
    @Autowired
    private IMacService macService;
    @Autowired
    private IMacHistoryService macHistoryService;
    @Autowired
    private ItemService itemService;

    @Override
    public List<Terminal> selectObjByMap(Map params) {
        return this.terminalMapper.selectObjByMap(params);
    }

    @Override
    public int insert(Terminal instance) {
        if(instance.getId() == null || instance.getId().equals("")){
            instance.setAddTime(new Date());
        }
        if(instance.getId() == null || instance.getId().equals("")){
            try {
                return this.terminalMapper.insert(instance);
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        }else{
            try {
                return this.terminalMapper.update(instance);
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        }
    }

    @Override
    public int update(Terminal instance) {
        try {
            return this.terminalMapper.update(instance);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public int batchInert(List<Terminal> instances) {
        try {
            return this.terminalMapper.batchInert(instances);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public int batchUpdate(List<Terminal> instances) {
        try {
            return this.terminalMapper.batchUpdate(instances);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public void syncMac() {
        Map params = new HashMap();
        params.clear();
        params.put("tag", "DT");
        List<Mac> macs = this.macService.selectByMap(params);
        if(macs.size() < 0){
            List<Terminal> terminals = this.terminalMapper.selectObjByMap(null);
            terminals = terminals.stream().map(e -> {
                        if(e.getOnline() == 1){
                            e.setOnline(0);
                            if(e.getInterfaceName().equals("PortN")){
                                e.setInterfaceStatus(1);
                            }
                            return e;
                        }
                        return null;
                    }
            ).collect(Collectors.toList());
            if(terminals.size() > 0){

                this.terminalMapper.batchUpdate(terminals);
            }
        }else{
            TerminalType terminalType = this.terminalTypeService.selectObjByType(1);
            List<Long> ids = new ArrayList<>();
            macs.stream().forEach(e -> {
                // 更新端口状态
                params.clear();
                params.put("interfaceName", e.getInterfaceName());
                params.put("ip", e.getDeviceIp());
                Integer ifup = this.itemService.selectInterfaceStatus(params);
                params.clear();
                params.put("mac", e.getMac());
                List<Terminal> terminals = this.terminalMapper.selectObjByMap(params);
                if(terminals.size() > 0){
                    terminals.stream().forEach(t -> {
                        if(t.getOnline() == 0){
                            t.setOnline(1);
                        }
                        if(t.getInterfaceStatus() != ifup && !t.getInterfaceName().equals("PortN")){
                            t.setInterfaceStatus(ifup);
                        }
                        if(!t.getUuid().equals(e.getUuid())
                                ||
                        t.getInterfaceName() == null  || !t.getInterfaceName().equals(e.getInterfaceName())
                            ||
                        t.getIp() == null || !t.getIp().equals(e.getIp())){
                            String[] IGNORE_ISOLATOR_PROPERTIES = new String[]{"id"};
                            BeanUtils.copyProperties(e, t, IGNORE_ISOLATOR_PROPERTIES);
                        }
                        this.terminalMapper.update(t);
                        ids.add(t.getId());
                    });
                }else{
                    Terminal terminal = new Terminal();
                    BeanUtils.copyProperties(e, terminal);
                    terminal.setOnline(1);
                    terminal.setTerminalTypeId(terminalType.getId());
                    if(e.getInterfaceName().equals("PortN")){
                        terminal.setInterfaceStatus(1);
                    }else{
                        terminal.setInterfaceStatus(ifup);
                    }
                    this.terminalMapper.insert(terminal);
                    ids.add(terminal.getId());
                }
            });
            params.clear();
            params.put("notIds", ids);
            List<Terminal> terminals = this.terminalMapper.selectObjByMap(params);
            terminals = terminals.stream().map(e -> {
                        if(e.getOnline() == 1){
                            e.setOnline(0);
                            return e;
                        }
                          return null;
                    }
            ).filter(Objects::nonNull).collect(Collectors.toList());
            if(terminals.size() > 0){
                this.terminalMapper.batchUpdate(terminals);
            }
        }
    }

    @Override
    public void syncHistoryMac(Date time) {
        Map params = new HashMap();
        params.clear();
        params.put("tag", "DT");
        params.put("time", time);
        List<Mac> macs = this.macHistoryService.selectByMap(params);
        if(macs.size() > 0){
            macs.stream().forEach(e -> {
                params.clear();
                params.put("mac", e.getMac());
                List<Terminal> terminals = this.terminalMapper.selectObjByMap(params);
                if(terminals.size() > 0){
                    Terminal terminal = terminals.get(0);
                    if(terminal.getTerminalTypeId() != null && !terminal.getTerminalTypeId().equals("")){
                        TerminalType terminalType = this.terminalTypeService.selectObjById(terminal.getTerminalTypeId());
                        e.setTerminalTypeName(terminalType.getName());
                        this.macHistoryService.update(e);
                    }
                }
            });
        }
    }
}
