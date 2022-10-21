package com.metoo.nspm.core.service.impl;

import com.metoo.nspm.core.service.ISysConfigService;
import com.metoo.nspm.core.service.ITopologyTokenService;
import com.metoo.nspm.core.topology.mapper.TopologyTokenMapper;
import com.metoo.nspm.core.utils.NodeUtil;
import com.metoo.nspm.entity.SysConfig;
import com.metoo.nspm.entity.TopologyToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class TopologyTokenServiceImpl implements ITopologyTokenService {

    @Autowired
    private TopologyTokenMapper topologyTokenMapper;
    @Autowired
    private NodeUtil nodeUtil;
    @Autowired
    private ISysConfigService sysConfigService;

    public void  initToken(){
        // 验证当前token是否生效
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String token = sysConfig.getNspmToken();
        if(token != null) {
            String url = "/topology/cycle/getCyclePage/";
            try {
                Object object =  this.nodeUtil.getBody(null, url, token);
                if(object == null){
                    Map map = new HashMap();
                    map.put("type", "client_credentials");
                    map.put("orderBy", "create_time");
                    map.put("orderType", "desc");
                    List<TopologyToken> TopologyTokens = this.topologyTokenMapper.query(map);
                    if(TopologyTokens.size() > 0){
                        TopologyToken topologyToken = TopologyTokens.get(0);
                        // updateToken
                        sysConfig.setNspmToken(topologyToken.getToken_value());
                        sysConfig.setId(sysConfig.getId());
                        this.sysConfigService.update(sysConfig);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Map map = new HashMap();
                map.put("type", "client_credentials");
                map.put("orderBy", "create_time");
                map.put("orderType", "desc");
                List<TopologyToken> TopologyTokens = this.topologyTokenMapper.query(map);
                if(TopologyTokens.size() > 0){
                    TopologyToken topologyToken = TopologyTokens.get(0);
                    // updateToken
                    sysConfig.setNspmToken(topologyToken.getToken_value());
                    sysConfig.setId(sysConfig.getId());
                    this.sysConfigService.update(sysConfig);
                }
            }
        }
    }
}
