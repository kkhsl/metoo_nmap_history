package com.metoo.nspm.core.service.topo.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.metoo.nspm.core.service.INetworkElementService;
import com.metoo.nspm.core.service.ISysConfigService;
import com.metoo.nspm.core.service.IUserService;
import com.metoo.nspm.core.service.topo.ITopoNodeService;
import com.metoo.nspm.core.utils.NodeUtil;
import com.metoo.nspm.dto.TopoNodeDto;
import com.metoo.nspm.entity.NetworkElement;
import com.metoo.nspm.entity.SysConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TopoNodeServiceImpl implements ITopoNodeService {

    @Autowired
    private ISysConfigService sysConfigService;
    @Autowired
    private NodeUtil nodeUtil;
    @Autowired
    private IUserService userService;
    @Autowired
    private INetworkElementService networkElementService;

    @Override
    public List<Map> queryAnt() {
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String token = sysConfig.getNspmToken();
        if (token != null) {
            List<Map> ipList = new ArrayList<>();
            String url = "topology/node/queryNode.action";
            TopoNodeDto dto = new TopoNodeDto();
//            User user = ShiroUserHolder.currentUser();
//            if (user.getGroupLevel() == null || user.getGroupLevel().equals("")) {
//                dto.setBranchLevel(user.getGroupLevel());
//            }
            dto.setStart(1);
            dto.setLimit(100000);
            Object result = this.nodeUtil.getBody(dto, url, token);
            JSONObject object = JSONObject.parseObject(result.toString());
            if (object.get("data") != null && object.getInteger("total") > 0) {
                JSONArray arrays = JSONArray.parseArray(object.get("data").toString());
                for (Object array : arrays) {
                    Map map = new HashMap();
                    JSONObject data = JSONObject.parseObject(array.toString());
                    if (object != null && object.get("errorMess") != null) {
                        if (data.get("errorMess") != null && !data.get("errorMess").toString().equals("")) {
                            return null;
                        }
                    }

                    if(data.get("isVsys") == null || !data.getBoolean("isVsys")){
//                            ipList.add(data.getString("ip"));
                        map.put("ip", data.getString("ip"));
                        map.put("deviceName", data.getString("deviceName"));
                        map.put("pluginId", data.getString("pluginId"));
                        map.put("uuid", data.getString("uuid"));
                        ipList.add(map);
                    }
                }
                return ipList;
            }
        }
        return null;
    }

    @Override
    public List<Map> queryMetoo() {
        List<NetworkElement> nes = this.networkElementService.selectObjByMap(null);
        List<Map> ipList = new ArrayList<>();
        if(nes.size() > 0) {
            for (NetworkElement ne : nes) {
                Map map = new HashMap();
                map.put("ip", ne.getIp());
                map.put("deviceName", ne.getDeviceName());
                map.put("uuid", ne.getUuid());
                map.put("deviceType", ne.getDeviceTypeName());
                ipList.add(map);
            }
        }
        return ipList;
    }

}
